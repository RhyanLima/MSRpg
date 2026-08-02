package com.rcl.msrpg.shared.configuration;

import java.io.InputStream;
import java.util.Properties;

public class AppConfig {

    private static final Properties PROPERTIES = new Properties();
    private static boolean isLoaded = false;

    private AppConfig() {}

    static {
        loadProperties();
    }

    private static synchronized void loadProperties() {
        if (isLoaded) return;

        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                PROPERTIES.load(input);
                System.out.println("[Config] Configurações carregadas do application.properties com sucesso.");
            } else {
                System.out.println("[Aviso] application.properties não foi encontrado no classpath. Usando valores padrão.");
            }
        } catch (Exception ex) {
            System.err.println("[Erro] Falha ao ler application.properties: " + ex.getMessage());
        }
        isLoaded = true;

    }

    public static boolean isDevMode() {
        return Boolean.parseBoolean(get("msrpg.devMode", "false"));
    }

    public static String getDbPath() {
        return get("msrpg.dbPath", "./dev-data/database.db");
    }

    public static int getPort() {
        return Integer.parseInt(get("msrpg.port", "8080"));
    }

    /**
     * Busca uma propriedade genérica com valor padrão caso não exista.
     */
    public static String get(String key, String defaultValue) {
        return PROPERTIES.getProperty(key, defaultValue);
    }

}
