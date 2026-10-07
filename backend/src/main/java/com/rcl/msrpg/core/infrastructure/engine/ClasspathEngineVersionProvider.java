package com.rcl.msrpg.core.infrastructure.engine;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import com.rcl.msrpg.core.engine.EngineVersionProvider;
import com.rcl.msrpg.core.exception.DomainValidationException;
import com.rcl.msrpg.core.valueobject.SemanticVersion;

public final class ClasspathEngineVersionProvider implements EngineVersionProvider {

    static final String DEFAULT_RESOURCE = "msrpg-engine.properties";
    static final String VERSION_KEY = "msrpg.engine.version";

    private final SemanticVersion version;

    private ClasspathEngineVersionProvider(SemanticVersion version) {
        this.version = version;
    }

    public static ClasspathEngineVersionProvider load() {
        return fromResource(DEFAULT_RESOURCE);
    }

    static ClasspathEngineVersionProvider fromResource(String resourceName) {
        Properties properties = readProperties(resourceName);
        String raw = properties.getProperty(VERSION_KEY);
        try {
            return new ClasspathEngineVersionProvider(SemanticVersion.of(raw));
        } catch (DomainValidationException error) {
            throw new IllegalStateException(
                "Invalid engine version in " + resourceName + " (" + VERSION_KEY + ").", error);
        }
    }

    private static Properties readProperties(String resourceName) {
        ClassLoader loader = ClasspathEngineVersionProvider.class.getClassLoader();
        try (InputStream input = loader.getResourceAsStream(resourceName)) {
            Objects.requireNonNull(input, () -> "Resource not found: " + resourceName);
            Properties properties = new Properties();
            properties.load(input);
            return properties;
        } catch (IOException | NullPointerException error) {
            throw new IllegalStateException("Unable to read engine version from " + resourceName + ".", error);
        }
    }

    @Override
    public SemanticVersion current() {
        return version;
    }

}
