package io.ei.jsontoxls.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;

@Path("/xls")
@Produces({"application/ms-excel"})
public class JsonToXlsResource {
    private final String excelTemplate;

    public JsonToXlsResource(String excelTemplate) {
        this.excelTemplate = excelTemplate;
    }

    @GET
    public Response generateExcelFromTemplate() {
        return Response.ok(getOut(new byte[]{})).build();
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
