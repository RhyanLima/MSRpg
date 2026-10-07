package com.rcl.msrpg.core.configuration;

import java.time.Clock;
import java.util.List;

import org.jdbi.v3.core.Jdbi;

import com.rcl.msrpg.core.engine.EngineVersionProvider;
import com.rcl.msrpg.core.infrastructure.engine.ClasspathEngineVersionProvider;
import com.rcl.msrpg.core.infrastructure.web.WebController;
import com.rcl.msrpg.system.infrastructure.configuration.RpgSystemModule;

public class AppContainer {

    private final Jdbi jdbi;
    private final EngineVersionProvider engineVersionProvider;
    private final RpgSystemModule rpgSystemModule;

    private AppContainer(Jdbi jdbi) {
        this.jdbi = jdbi;
        this.engineVersionProvider = ClasspathEngineVersionProvider.load();
        this.rpgSystemModule = new RpgSystemModule(jdbi, engineVersionProvider, Clock.systemUTC());
    }

    public static AppContainer create(Jdbi jdbi) {
        return new AppContainer(jdbi);
    }

    public RpgSystemModule rpgSystemModule() {
        return rpgSystemModule;
    }

    public EngineVersionProvider engineVersionProvider() {
        return engineVersionProvider;
    }

    // Lista de todos os controladores da aplicação
    public List<WebController> webControllers() {
        
        List<WebController> controllers = List.of(
            rpgSystemModule.controller()
        );

        System.out.println("Registered Web Controllers:");
        controllers.forEach(controller -> {
            System.out.println(" - " + controller.getClass().getSimpleName());
        });
        
        return controllers;
    }

    public Jdbi jdbi() {
        return jdbi;
    }

}
