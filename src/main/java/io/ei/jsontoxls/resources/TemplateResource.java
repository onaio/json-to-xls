package io.ei.jsontoxls.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;

@Path("/templates")
public class TemplateResource {
    Logger logger = LoggerFactory.getLogger(TemplateResource.class);

    @POST
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    @Produces({"application/octet-stream"})
    public Response save(byte[] templateData) {
        try {
            logger.info("Got template with " + templateData.length + " number of bytes.");
            return Response.ok(getOut(templateData), MediaType.APPLICATION_OCTET_STREAM).build();
        } catch (Exception e) {
            logger.error("Unable to save template. Exception: " + e.getStackTrace());
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
