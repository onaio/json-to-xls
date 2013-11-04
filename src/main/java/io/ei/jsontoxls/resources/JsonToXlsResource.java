package io.ei.jsontoxls.resources;

import io.ei.jsontoxls.core.Employee;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Path("/xls")
@Produces({"application/ms-excel"})
public class JsonToXlsResource {
    Logger logger = LoggerFactory.getLogger(JsonToXlsResource.class);
    private final String excelTemplate;

    public JsonToXlsResource(String excelTemplate) {
        this.excelTemplate = excelTemplate;
    }

    @GET
    public Response generateExcelFromTemplate() {
        Collection staff = new HashSet();
        staff.add(new Employee("Derek", 35, 3000, 0.30));
        staff.add(new Employee("Elsa", 28, 1500, 0.15));
        staff.add(new Employee("Oleg", 32, 2300, 0.25));
        staff.add(new Employee("Neil", 34, 2500, 0.00));
        staff.add(new Employee("Maria", 34, 1700, 0.15));
        staff.add(new Employee("John", 36, 2800, 0.20));
        Map beans = new HashMap();
        beans.put("employee", staff);
        XLSTransformer transformer = new XLSTransformer();
        try {
            FileInputStream inputStream = new FileInputStream("employees.xls");

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
