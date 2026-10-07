package com.rcl.msrpg.core.infrastructure.web;

import io.javalin.Javalin;

@FunctionalInterface
public interface WebController {
    void registerRoutes(Javalin app);
}
