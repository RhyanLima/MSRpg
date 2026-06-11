package com.rcl.msrpg.shared.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rcl.msrpg.shared.exception.api.ApiException;

import io.javalin.Javalin;

public class RuntimeExectionHandler {

    private static final Logger log = LoggerFactory.getLogger(RuntimeExectionHandler.class);

    private RuntimeExectionHandler() {}

    public static void register(Javalin app) {
        app.exception(ApiException.class, (exception, context) -> {
            String requestId = getRequestId(context);
        });
    }

}
