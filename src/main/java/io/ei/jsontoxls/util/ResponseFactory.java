package io.ei.jsontoxls.util;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.Response.Status.*;

public class ResponseFactory {

    public static Response internalServerError(String entity) {
        return Response
                .status(INTERNAL_SERVER_ERROR)
                .entity(entity)
                .build();
    }

    public static Response notFound(String entity) {
        return Response
                .status(NOT_FOUND)
                .entity(entity)
                .build();
    }

    public static Response badRequest(String entity) {
        return Response
                .status(BAD_REQUEST)
                .entity(entity)
                .build();
    }
}
