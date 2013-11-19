package io.ei.jsontoxls.resources;

import io.ei.jsontoxls.util.PackageUtils;
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
    private Logger logger = LoggerFactory.getLogger(JsonToXlsResource.class);
    private final String excelTemplate;

    public JsonToXlsResource(String excelTemplate) {
        this.excelTemplate = excelTemplate;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response generateExcelFromTemplate(String data) {
        logger.debug("Got request with JSON: " + data);
        String packageName = "io.ei.jsontoxls.domain";
        String outputDirectory = "output";
        String generatedPackageName = "";
        String className = "Data";
        JsonPojoConverter converter = new JsonPojoConverter(packageName, className, outputDirectory);
        ObjectDeserializer objectDeserializer = new ObjectDeserializer(outputDirectory, className);
        try {
            generatedPackageName = converter.generateJavaClasses(data);
            Map<String, Object> beans = new HashMap<>();
            beans.put("data", objectDeserializer.makeJsonObject(generatedPackageName, data));
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
        } finally {
            new PackageUtils(outputDirectory).cleanup(generatedPackageName);
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
