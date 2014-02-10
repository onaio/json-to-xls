package io.ei.jsontoxls.resources;

import io.ei.jsontoxls.Messages;
import io.ei.jsontoxls.repository.TemplateRepository;
import io.ei.jsontoxls.util.ExcelUtils;
import io.ei.jsontoxls.util.ResponseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;

import static io.ei.jsontoxls.AllConstants.MEDIA_TYPE_MS_EXCEL;
import static io.ei.jsontoxls.AllConstants.TOKEN_PATH_PARAM;
import static io.ei.jsontoxls.util.UUIDUtils.newUUID;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;

@Path("/templates")
public class TemplateResource {
    Logger logger = LoggerFactory.getLogger(TemplateResource.class);
    private TemplateRepository templateRepository;
    private ExcelUtils excelUtils;

    public TemplateResource(TemplateRepository templateRepository, ExcelUtils excelUtils) {
        this.templateRepository = templateRepository;
        this.excelUtils = excelUtils;
    }

    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.TEXT_PLAIN)
    public Response save(byte[] templateData) {
        try {
            if (!excelUtils.isExcel(templateData)) {
                return ResponseFactory.badRequest(Messages.INVALID_TEMPLATE);
            }

            String token = newUUID();
            templateRepository.add(token, templateData);
            logger.info(format("Saved template with {0} number of bytes. Token: {1}", templateData.length, token));
            return ResponseFactory.created(token);
        } catch (Exception e) {
            logger.error(format("Unable to save template. Exception Message: {0}. Stack trace: {1}.", e.getMessage(),
                    getFullStackTrace(e)));
            return ResponseFactory.internalServerError(Messages.UNABLE_TO_SAVE_TEMPLATE);
        }
    }

    @PUT
    @Path("/{" + TOKEN_PATH_PARAM + "}")
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces(MediaType.TEXT_PLAIN)
    public Response update(@PathParam(TOKEN_PATH_PARAM) String templateToken, byte[] templateData) {
        try {
            if (templateRepository.findByToken(templateToken) == null) {
                return ResponseFactory.notFound(MessageFormat.format(Messages.INVALID_TEMPLATE_TOKEN, templateToken));
            }

            if (!excelUtils.isExcel(templateData)) {
                return ResponseFactory.badRequest(Messages.INVALID_TEMPLATE);
            }

            templateRepository.update(templateToken, templateData);
            logger.info(format("Updated template with {0} number of bytes for token: {1}", templateData.length, templateToken));
            return ResponseFactory.ok(templateToken);
        } catch (Exception e) {
            logger.error(format("Unable to update template. Exception Message: {0}. Stack trace: {1}.", e.getMessage(),
                    getFullStackTrace(e)));
            return ResponseFactory.internalServerError(Messages.UNABLE_TO_UPDATE_TEMPLATE);
        }
    }

    @GET
    @Path("/{" + TOKEN_PATH_PARAM + "}")
    @Produces(MEDIA_TYPE_MS_EXCEL)
    public Response get(@PathParam(TOKEN_PATH_PARAM) String token) {
        byte[] generatedExcel = templateRepository.findByToken(token);
        if (generatedExcel == null) {
            return ResponseFactory.notFound(MessageFormat.format(Messages.INVALID_TEMPLATE_TOKEN, token));
        }

        return ResponseFactory.excel(getExcelAsOutputStream(generatedExcel));
    }

    private StreamingOutput getExcelAsOutputStream(final byte[] excelBytes) {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream out) throws IOException, WebApplicationException {
                out.write(excelBytes);
            }
        };
    }
}
