package io.ei.jsontoxls.resources;

import com.google.gson.Gson;
import io.ei.jsontoxls.core.Data;
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
import java.util.HashMap;
import java.util.Map;

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
        Map beans = new HashMap();
        beans.put("data", new Gson().fromJson(data, Data.class));
        XLSTransformer transformer = new XLSTransformer();
        try {
            FileInputStream inputStream = new FileInputStream(excelTemplate);
            Workbook workbook = transformer.transformXLS(inputStream, beans);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return Response.ok(getOut(outputStream.toByteArray())).build();
        } catch (Exception e) {
            logger.error("XLS Transformation failed. Exception: " + e);
            return Response.status(503).build();
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
