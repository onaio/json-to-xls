package io.ei.jsontoxls.resources;

import io.ei.jsontoxls.AllConstants;
import io.ei.jsontoxls.Messages;
import io.ei.jsontoxls.repository.ExcelRepository;
import io.ei.jsontoxls.repository.TemplateRepository;
import io.ei.jsontoxls.util.*;

import org.codehaus.jackson.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import static io.ei.jsontoxls.AllConstants.*;
import static io.ei.jsontoxls.util.ResponseFactory.*;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.exception.ExceptionUtils.getStackTrace;

@Path("/xls/{" + TOKEN_PATH_PARAM + "}")
public class XlsResource {
    private ExcelUtils excelUtil;
    private Logger logger = LoggerFactory.getLogger(XlsResource.class);
    private JsonPojoConverter converter;
    private ObjectDeserializer objectDeserializer;
    private PackageUtils packageUtil;
    private TemplateRepository templateRepository;
    private ExcelRepository excelRepository;

    public XlsResource(JsonPojoConverter converter, ObjectDeserializer objectDeserializer, PackageUtils packageUtil,
                       ExcelUtils excelUtil, TemplateRepository templateRepository, ExcelRepository excelRepository) {
        this.converter = converter;
        this.objectDeserializer = objectDeserializer;
        this.packageUtil = packageUtil;
        this.excelUtil = excelUtil;
        this.templateRepository = templateRepository;
        this.excelRepository = excelRepository;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces("text/plain")
    public Response generateExcelFromTemplate(@PathParam(TOKEN_PATH_PARAM) String templateToken, String jsonData) {
        logger.debug(format("Got request with Token: {0} and JSON: {1}", templateToken, jsonData));
        String generatedPackageName = "";
        Object deserializedObject;
        try {
            byte[] template = templateRepository.findByToken(templateToken);
            if (template == null) {
                return notFound(format(Messages.INVALID_TOKEN, templateToken));
            }
            if (isBlank(jsonData)) {
                return badRequest(format(Messages.EMPTY_JSON_DATA, templateToken));
            }

            if(jsonData.startsWith("[")){
                //Able to handle json array
                deserializedObject = objectDeserializer.makeJsonList(generatedPackageName,
                    jsonData);
            }else{
                generatedPackageName = converter.generateJavaClasses(jsonData);
                deserializedObject = objectDeserializer.makeJsonObject(generatedPackageName,
                    jsonData);
            }

            Map<String, Object> beans = new HashMap<>();
            beans.put(ROOT_DATA_OBJECT, deserializedObject);
            String generatedExcelToken = UUIDUtils.newUUID();
            byte[] generatedExcel = excelUtil.generateExcel(beans, template, generatedExcelToken);

            excelRepository.add(generatedExcelToken, templateToken, generatedExcel);
            return ResponseFactory.created(URI.create("/xls/" + generatedExcelToken).toString());
        } catch (JsonParseException e) {
            logger.error(format(Messages.MALFORMED_JSON, e.getMessage(),
                    getStackTrace(e)));
            return badRequest(Messages.MALFORMED_JSON);
        } catch (Exception e) {
            logger.error(format(Messages.TRANSFORMATION_FAILURE, e.getMessage(),
                    getStackTrace(e)));
            return internalServerError(Messages.UNABLE_TO_GENERATE_EXCEL_ERROR);
        } finally {
            packageUtil.cleanup(generatedPackageName);
        }
    }

    @GET
    @Produces(MEDIA_TYPE_MS_EXCEL)
    public Response get(@PathParam(TOKEN_PATH_PARAM) String token) {
        byte[] generatedExcel = excelRepository.findByToken(token);
        if (generatedExcel == null) {
            return ResponseFactory.notFound(MessageFormat.format(Messages.INVALID_EXCEL_TOKEN, token));
        }

        String filename = token + "." + AllConstants.DEFAULT_EXTENSION;
        return ResponseFactory.excel(getExcelAsOutputStream(generatedExcel),
                                     filename);
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
