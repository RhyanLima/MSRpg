package com.rcl.msrpg.shared.configuration;

import org.jdbi.v3.core.Jdbi;

import com.rcl.msrpg.system.infrastructure.configuration.RpgSystemModule;

public class AppContainer {

    private final Jdbi jdbi;
    private final RpgSystemModule rpgSystemModule;

    private AppContainer(Jdbi jdbi) {
        this.jdbi = jdbi;
        this.rpgSystemModule = new RpgSystemModule(jdbi);
    }

    public static AppContainer create(Jdbi jdbi) {
        return new AppContainer(jdbi);
    }

    public RpgSystemModule rpgSystemModule() {
        return rpgSystemModule;
    }

    public Jdbi jdbi() {
        return jdbi;
    }

}
