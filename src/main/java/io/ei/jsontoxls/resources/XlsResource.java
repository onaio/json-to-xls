package io.ei.jsontoxls.resources;

import io.ei.jsontoxls.Messages;
import io.ei.jsontoxls.repository.TemplateRepository;
import io.ei.jsontoxls.util.ExcelUtils;
import io.ei.jsontoxls.util.JsonPojoConverter;
import io.ei.jsontoxls.util.ObjectDeserializer;
import io.ei.jsontoxls.util.PackageUtils;
import org.codehaus.jackson.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static io.ei.jsontoxls.util.ResponseFactory.*;
import static java.text.MessageFormat.format;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;

@Path("/xls/{token}")
@Produces({"application/ms-excel"})
public class XlsResource {
    private ExcelUtils excelUtil;
    private Logger logger = LoggerFactory.getLogger(XlsResource.class);
    private JsonPojoConverter converter;
    private ObjectDeserializer objectDeserializer;
    private PackageUtils packageUtil;
    private TemplateRepository templateRepository;

    public XlsResource(JsonPojoConverter converter, ObjectDeserializer objectDeserializer, PackageUtils packageUtil,
                       ExcelUtils excelUtil, TemplateRepository templateRepository) {
        this.converter = converter;
        this.objectDeserializer = objectDeserializer;
        this.packageUtil = packageUtil;
        this.excelUtil = excelUtil;
        this.templateRepository = templateRepository;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response generateExcelFromTemplate(@PathParam("token") String token, String jsonData) {
        logger.debug(format("Got request with Token: {0} and JSON: {1}", token, jsonData));
        String generatedPackageName = "";
        try {
            byte[] template = templateRepository.findByToken(token);
            if (template == null) {
                return notFound(format(Messages.INVALID_TOKEN, token));
            }
            if (isBlank(jsonData)) {
                return badRequest(format(Messages.EMPTY_JSON_DATA, token));
            }
            generatedPackageName = converter.generateJavaClasses(jsonData);
            Map<String, Object> beans = new HashMap<>();
            beans.put("data", objectDeserializer.makeJsonObject(generatedPackageName, jsonData));
            return Response.ok(excelUtil.generateExcelWorkbook(beans, template)).build();
        } catch (JsonParseException e) {
            logger.error(format(Messages.MALFORMED_JSON, e.getMessage(),
                    getFullStackTrace(e)));
            return badRequest(Messages.MALFORMED_JSON);
        } catch (Exception e) {
            logger.error(format(Messages.TRANSFORMATION_FAILURE, e.getMessage(),
                    getFullStackTrace(e)));
            return internalServerError(Messages.UNABLE_TO_GENERATE_EXCEL_ERROR);
        } finally {
            packageUtil.cleanup(generatedPackageName);
        }
    }

}
