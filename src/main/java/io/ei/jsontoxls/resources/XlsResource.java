package io.ei.jsontoxls.resources;

import io.ei.jsontoxls.Messages;
import io.ei.jsontoxls.repository.TemplateRepository;
import io.ei.jsontoxls.util.ExcelUtils;
import io.ei.jsontoxls.util.JsonPojoConverter;
import io.ei.jsontoxls.util.ObjectDeserializer;
import io.ei.jsontoxls.util.PackageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

import static java.text.MessageFormat.format;
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
    public Response generateExcelFromTemplate(@PathParam("token") String token, String data) {
        logger.debug("Got request with JSON: " + data);
        String generatedPackageName = "";
        try {
            byte[] template = templateRepository.findByToken(token);
            if (template == null) {
                logger.error(format(Messages.NO_TEMPLATE_FOR_GIVEN_TOKEN, token));
                return Response.status(404).entity(Messages.NO_TEMPLATE_FOR_GIVEN_TOKEN).build();
            }
            generatedPackageName = converter.generateJavaClasses(data);
            Map<String, Object> beans = new HashMap<>();
            beans.put("data", objectDeserializer.makeJsonObject(generatedPackageName, data));
            return Response.ok(excelUtil.generateExcelWorkbook(beans, template)).build();
        } catch (Exception e) {
            logger.error(format(Messages.TRANSFORMATION_FAILURE, e.getMessage(),
                    getFullStackTrace(e)));
        } finally {
            packageUtil.cleanup(generatedPackageName);
        }
        return Response.status(500).build();
    }
}
