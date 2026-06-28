package com.rcl.msrpg.bootstrap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rcl.msrpg.shared.configuration.AppContainer;
import com.rcl.msrpg.shared.exception.ResponseError;
import com.rcl.msrpg.shared.exception.api.RuntimeExceptionHandler;
import com.rcl.msrpg.shared.log.RuntimeLogHandler;

import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;

import java.util.UUID;

public class ServerBootstrap {

    private static final String ENV_PORT = "MSRPG_PORT";
    private static final String DEV_MODE = "MSRPG_DEV_MODE";

    private ServerBootstrap() {}

    public static Javalin start(AppContainer container, String sessionToken) {
        int port = resolvePort();

        Javalin app = Javalin.create(config -> {
            config.jsonMapper(new JavalinJackson(objectMapper(), true));

            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(rule -> {
                    rule.allowHost("http://localhost:4200");
                    rule.allowHost("http://127.0.0.1:4200");
                    rule.allowCredentials = true;
                });
            });
        });

        registerSecurity(app, sessionToken);
        
        RuntimeLogHandler.register(app);
        RuntimeExceptionHandler.register(app);

        RouteRegistry.register(app, container);

        app.start(port);

        return app;
    }

    public static String generateSessionToken() {
        return UUID.randomUUID().toString();
    }

    private static int resolvePort() {
        String envPort = System.getenv(ENV_PORT);

        if (envPort == null || envPort.isBlank()) {
            return 0;
        }

        try {
            return Integer.parseInt(envPort);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Porta inválida em " + ENV_PORT + ": " + envPort);
        }
    }

    private static void registerSecurity(Javalin app, String sessionToken) {
        app.before(ctx -> {
            if (isDevMode()) {
                return;
            }

            String path = ctx.path();

            String receivedToken = ctx.header("X-Session-Token");

            if (!sessionToken.equals(receivedToken)) {
                ctx.status(401)
                    .json(new ResponseError(401, "Token local inválido."))
                    .skipRemainingHandlers();
            }
        });
    }

    private static boolean isDevMode() {
        return "true".equalsIgnoreCase(System.getenv(DEV_MODE));
    }

    private static ObjectMapper objectMapper() {
        return new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

}
