package com.rcl.msrpg.shared.log;

import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.javalin.Javalin;

public class RuntimeLogHandler {

    private RuntimeLogHandler() {}

    private static final Logger log = LoggerFactory.getLogger(RuntimeLogHandler.class);

    private static final Set<String> SENSITIVE_HEADERS = Set.of(
            "authorization",
            "cookie",
            "set-cookie",
            "x-session-token"
    );

    public static void register(Javalin app) {
        app.before(context -> {
            String requestId = UUID.randomUUID().toString();

            context.attribute("requestId", requestId);
            context.attribute("startedAt", System.nanoTime());

            context.header("X-Request-Id", requestId);

            log.info(
                    "Request started. requestId={}, method={}, path={}, ip={}",
                    requestId,
                    context.method(),
                    context.path(),
                    context.ip()
            );
        });

        app.after(context -> {
            String requestId = context.attribute("requestId");
            Long startedAt = context.attribute("startedAt");

            long durationMs = 0;

            if (startedAt != null) {
                durationMs = (System.nanoTime() - startedAt) / 1_000_000;
            }

            log.info(
                    "Request finished. requestId={}, method={}, path={}, status={}, durationMs={}",
                    requestId,
                    context.method(),
                    context.path(),
                    context.status().getCode(),
                    durationMs
            );
        });
    }

    public static boolean isSensitiveHeader(String headerName) {
        if (headerName == null) {
            return true;
        }

        return SENSITIVE_HEADERS.contains(headerName.toLowerCase());
    }


}
