package com.rcl.msrpg.bootstrap;

import org.flywaydb.core.Flyway;

public class MigrationBootstrap {

    private MigrationBootstrap() {}

    public static void migrate(String databasePath) {
        Flyway flyway = Flyway.configure()
            .dataSource("jdbc:sqlite:" + databasePath, "", "")
            .locations("classpath:db/migration")
            .baselineOnMigrate(true)
            .load();

        flyway.migrate();
    }

}
