package io.ei.jsontoxls.resources;

import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;

@Path("/xls")
@Produces({"application/ms-excel"})
public class JsonToXlsResource {
    Logger logger = LoggerFactory.getLogger(JsonToXlsResource.class);
    private final String excelTemplate;

    public JsonToXlsResource(String excelTemplate) {
        this.excelTemplate = excelTemplate;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response generateExcelFromTemplate(String data) {
        logger.debug("Got request with JSON: " + data);
        String className = "Data";
        String packageName = "io.ei.jsontoxls.core";
        String outputDirectory = "output";
        JsonPojoConverter converter = new JsonPojoConverter(packageName, className, outputDirectory, logger);
        try {
            String generatedPackageName = converter.generateJavaClasses(data);
            Map<String, Object> beans = new HashMap<>();
            beans.put(className.toLowerCase(), converter.makeJsonObject(generatedPackageName, data));
            FileInputStream inputStream = new FileInputStream(excelTemplate);
            XLSTransformer transformer = new XLSTransformer();
            Workbook workbook = transformer.transformXLS(inputStream, beans);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return Response.ok(getOut(outputStream.toByteArray())).build();
        } catch (Exception e) {
            logger.error(MessageFormat.format("XLS Transformation failed. Exception Message: {0}. Stack trace: {1}", e.getMessage(),
                    getFullStackTrace(e)));
            return Response.status(503).build();
        }
        finally {
            converter.cleanup(packageName);
        }
    }

    private StreamingOutput getOut(final byte[] excelBytes) {
        return new StreamingOutput() {
            @Override
            public void write(OutputStream out) throws IOException, WebApplicationException {
                out.write(excelBytes);
            }
        };
    }
}
