package com.rcl.msrpg.shared.infrastructure.web;

import io.javalin.Javalin;

@FunctionalInterface
public interface WebController {
    void registerRoutes(Javalin app);
}
