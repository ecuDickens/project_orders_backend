package com.orders.exception.mappers;

import com.orders.exception.HttpException;
import com.orders.types.ErrorType;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class HttpExceptionMapper implements ExceptionMapper<HttpException> {
    @Override
    public Response toResponse(HttpException x) {
        Response.ResponseBuilder builder;

        String message = x.getMessage();
        if (message == null) {
            builder = Response.status(x.getStatusCode());
        } else {
            final ErrorType errorEntity = new ErrorType(message);
            builder = Response.status(x.getStatusCode()).entity(errorEntity);
        }

        return builder.build();
    }
}
