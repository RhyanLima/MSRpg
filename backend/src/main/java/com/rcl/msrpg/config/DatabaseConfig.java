package com.rcl.msrpg.config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.sqlite.SQLiteDataSource;

public class DatabaseConfig {

    private DatabaseConfig() {

    } 

    public static Jdbi create(String databasePath) {
        
        SQLiteDataSource dataSource = new SQLiteDataSource();
        dataSource.setUrl("jdbc:sqlite: " + databasePath);

        Jdbi jdbi = Jdbi.create(dataSource);
        jdbi.installPlugin(new SqlObjectPlugin());

        jdbi.useHandle(handle -> {
            handle.execute("PRAGMA journal_mode = WAL");
            handle.execute("PRAGMA foreign_keys = ON");
            handle.execute("PRAGMA synchronous = NORMAL");
            handle.execute("PRAGMA cache_size = -64000");
            handle.execute("PRAGMA temp_store = MEMORY");
        });

        return jdbi;
    }

    public static String resolveDatabasePath() {

        String envPath = System.getenv("MSRPG_DATABASE_PATH");

        if (envPath != null && !envPath.isBlank()) {
            createParentDirectories(Path.of(envPath));
            return envPath;
        }

        String os = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");
        
        Path databasePath;

        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            databasePath = Path.of(appData, "msrpg", "database.db");
        } else if (os.contains("mac")) {
            databasePath = Path.of(home, "Library", "Application Support", "msrpg", "database.db");
        } else {
            databasePath = Path.of(home, ".config", "msrpg", "database.db");
        }

        createParentDirectories(databasePath);
        return databasePath.toString();
    }

    private static void createParentDirectories(Path filePath) {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
        } 
        catch (Exception exception) {
            throw new IllegalStateException("Não foi possível criar o diretório do banco de dados: " + filePath, exception);
        }
    }

}
