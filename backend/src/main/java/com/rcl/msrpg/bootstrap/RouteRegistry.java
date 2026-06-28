package com.rcl.msrpg.bootstrap;

import com.rcl.msrpg.shared.configuration.AppContainer;

import io.javalin.Javalin;

public class RouteRegistry {

    private RouteRegistry() {}

    public static void register(Javalin app, AppContainer container) {
        container.rpgSystemModule().controller().registerRoutes(app);
    }

}
