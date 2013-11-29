package io.ei.jsontoxls.resources;

import io.ei.jsontoxls.util.ResponseFactory;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileInputStream;
import java.io.IOException;

@Path("/")
public class APIResource {
    private String apiDetailsFile;
    private String apiDetails;

    public APIResource(String apiDetailsFile) {
        this.apiDetailsFile = apiDetailsFile;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response details() throws IOException {
        if (apiDetails == null) {
            apiDetails = IOUtils.toString(new FileInputStream(apiDetailsFile));
        }
        return ResponseFactory.ok(apiDetails);
    }
}
