package com.rcl.msrpg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import io.javalin.Javalin;

import org.flywaydb.core.Flyway;
import org.jdbi.v3.core.Jdbi;

import com.rcl.msrpg.config.DatabaseConfig;
import com.rcl.msrpg.controller.HealthController;


public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class); 

    private App() {
    }

    public static void main( String[] args ) {
        
        String databasePath = DatabaseConfig.resolveDatabasePath();

        runMigrations(databasePath);

        Jdbi jdbi = DatabaseConfig.create(databasePath);

        String sessionToken = UUID.randomUUID().toString();
        int port = resolvePort();

        HealthController healthController = new HealthController(jdbi);

        Javalin app = Javalin.create(config -> {
            
            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(rule -> {
                    rule.allowHost("http://localhost:4200");
                    rule.allowHost("http://127.0.0.1:4200");
                    rule.allowCredentials = true;
                    rule.exposeHeader("X-Session-token");
                });
            });

        });

        app.before(context -> {
                
            String path = context.path();
                
            if (path.equals("/api/health")) {
                return;     
            }
                
            String token = context.header("X-Session-token");
                
            if (!sessionToken.equals(token)) {
                context.status(401).json(new ErrorResponse("Unauthorized"));
                context.skipRemainingHandlers();
            }
        });

        app.get("/api/health", healthController::health);

        app.exception(Exception.class, (exception, context) -> {
            log.error("Unhandled exception", exception);
            context.status(500).json(new ErrorResponse("Internal server error"));
        });

        app.start(port);

        System.out.println("SERVER_PORT=" + app.port());
        System.out.println("SESSION_TOKEN=" + sessionToken);
        System.out.flush();

        log.info("MSRpg backend iniciado na porta {}", app.port());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Parando MSRpg backend");
            app.stop();
        }));

    }

    private static void runMigrations(String databasePath) {
        Flyway flyway = Flyway.configure()
            .dataSource("jdbc:sqlite:" + databasePath, "", "")
            .locations("classpath:database/migration")
            .load();
        flyway.migrate();
    }

    private static int resolvePort() {
        String envPort = System.getenv("MSRPG_SERVER_PORT");

        if (envPort == null || envPort.isBlank()) {
            return 0;
        }

        return Integer.parseInt(envPort);
    }

    private record ErrorResponse(String message) {
    }

}
