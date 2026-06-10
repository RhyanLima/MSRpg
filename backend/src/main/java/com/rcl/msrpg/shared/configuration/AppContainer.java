package com.rcl.msrpg.shared.configuration;

import org.jdbi.v3.core.Jdbi;

public class AppContainer {


    private final Jdbi jdbi;

    private AppContainer(Jdbi jdbi) {
        this.jdbi = jdbi;
    }

    public static AppContainer create(Jdbi jdbi) {
        return new AppContainer(jdbi);
    }

    public Jdbi jdbi() {
        return jdbi;
    }

}
