package com.rcl.msrpg.bootstrap;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

import com.rcl.msrpg.shared.configuration.AppConfig;

import java.nio.file.Files;
import java.nio.file.Path;

public final class DatabaseBootstrap {

    private static final int CACHE_SIZE_KIB = -64000;

    private DatabaseBootstrap() {}

    public static String resolveDatabasePath() {
        
        if(AppConfig.isDevMode()) {
            Path devDatabasePath = Path.of(AppConfig.getDbPath());
            createParentDirectories(devDatabasePath);
            return devDatabasePath.toString();
        }

        // Fallback para o caminho do banco de dados baseado no sistema operacional
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

    public static Jdbi createJdbi(String databasePath) {
        SQLiteDataSource dataSource = new SQLiteDataSource(connectionConfig());
        dataSource.setUrl("jdbc:sqlite:" + databasePath);

        Jdbi jdbi = Jdbi.create(dataSource);
        jdbi.installPlugin(new SqlObjectPlugin());

        return jdbi;
    }

    static SQLiteConfig connectionConfig() {
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);
        config.setJournalMode(SQLiteConfig.JournalMode.WAL);       // persistente no arquivo
        config.setSynchronous(SQLiteConfig.SynchronousMode.NORMAL);
        config.setCacheSize(CACHE_SIZE_KIB);
        config.setTempStore(SQLiteConfig.TempStore.MEMORY);
        return config;
    }

    private static void createParentDirectories(Path databasePath) {
        try {
            Path parent = databasePath.toAbsolutePath().getParent();

            if (parent != null) {
                Files.createDirectories(parent);
            }
        } catch (Exception exception) {
            throw new IllegalStateException(
                "Não foi possível criar o diretório do banco de dados: " + databasePath,
                exception
            );
        }
    }

}
