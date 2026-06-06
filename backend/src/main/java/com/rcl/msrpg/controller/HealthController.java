package com.rcl.msrpg.controller;

import java.time.Instant;
import java.util.Map;

import org.jdbi.v3.core.Jdbi;

import io.javalin.http.Context;

public class HealthController {

    private final Jdbi jdbi;

    public HealthController(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public void health(Context ctx) {
        String databaseStatus = jdbi.withHandle(handle ->
            handle.createQuery("SELECT value FROM app_metadata WHERE key = 'schema_version_label'")
            .mapTo(String.class)
            .findOne()
            .orElse("unknown")
        );

        ctx.json(Map.of(
            "status", "ok",
            "app", "MSRpg",
            "version", "0.1.0",
            "database", databaseStatus,
            "timestamp", Instant.now().toString()
        ));
    }
    

}
