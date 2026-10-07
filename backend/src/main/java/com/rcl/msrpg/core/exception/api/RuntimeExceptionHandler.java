package com.rcl.msrpg.core.exception.api;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcl.msrpg.core.exception.DomainValidationException;

import io.javalin.Javalin;

public class RuntimeExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RuntimeExceptionHandler.class);

    private RuntimeExceptionHandler() {}

    public static void register(Javalin app) {
        
        app.exception(ApiException.class, (exception, context) -> {
            String requestId = getRequestId(context);

            log.warn(
                "Handled API exception. requestId={}, status={}, code={}, path={}, message={}",
                requestId,
                exception.getStatusCode(),
                exception.getCode(),
                context.path(),
                exception.getMessage()
            );

            context.status(exception.getStatusCode()).json(
                    ApiErrorResponse.of(
                            exception.getStatusCode(),
                            exception.getCode(),
                            exception.getMessage(),
                            context.path(),
                            requestId
                    )
            );
        });

        // Invariantes de domínio (Value Objects). Mensagens geradas pelo domínio,
        app.exception(DomainValidationException.class, (exception, context) -> {
            String requestId = getRequestId(context);

            log.warn(
                "Domain validation failed. requestId={}, path={}, message={}",
                requestId,
                context.path(),
                exception.getMessage()
            );

            context.status(400).json(
                    ApiErrorResponse.of(
                            400,
                            "VALIDATION_ERROR",
                            exception.getMessage(),
                            context.path(),
                            requestId
                    )
            );
        });

        app.exception(SQLException.class, (exception, context) -> {
            String requestId = getRequestId(context);

            log.error(
                    "SQL error. requestId={}, path={}",
                    requestId,
                    context.path(),
                    exception
            );

            context.status(500).json(
                    ApiErrorResponse.of(
                            500,
                            "DATABASE_ERROR",
                            "A database error occurred.",
                            context.path(),
                            requestId
                    )
            );
        });

        app.exception(Exception.class, (exception, context) -> {
            String requestId = getRequestId(context);

            log.error(
                    "Unhandled exception. requestId={}, path={}",
                    requestId,
                    context.path(),
                    exception
            );

            context.status(500).json(
                    ApiErrorResponse.of(
                            500,
                            "INTERNAL_SERVER_ERROR",
                            "Internal server error.",
                            context.path(),
                            requestId
                    )
            );
        });
    }

    private static String getRequestId(io.javalin.http.Context context) {
        String requestId = context.attribute("requestId");

        if (requestId == null || requestId.isBlank()) {
            return "unknown";
        }

        return requestId;
    }

}
