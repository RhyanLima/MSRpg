package com.rcl.msrpg;

import com.rcl.msrpg.bootstrap.DatabaseBootstrap;
import com.rcl.msrpg.bootstrap.MigrationBootstrap;
import com.rcl.msrpg.bootstrap.ServerBootstrap;
import com.rcl.msrpg.bootstrap.ShutdownHook;
import com.rcl.msrpg.configuration.AppContainer;
import io.javalin.Javalin;
import org.jdbi.v3.core.Jdbi;

public class App {

    private App() {} 

    public static void main(String[] args) {
        
        String databasePath = DatabaseBootstrap.resolveDatabasePath();

        MigrationBootstrap.migrate(databasePath);

        Jdbi jdbi = DatabaseBootstrap.createJdbi(databasePath);

        AppContainer container = AppContainer.create(jdbi);

        String sessionToken = ServerBootstrap.generateSessionToken();

        Javalin app = ServerBootstrap.start(container, sessionToken);

        ShutdownHook.register(app);

        System.out.println("SERVER_PORT=" + app.port());
        System.out.println("SESSION_TOKEN=" + sessionToken);

}
