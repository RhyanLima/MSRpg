package com.rcl.msrpg.core.infrastructure.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.rcl.msrpg.core.valueobject.SemanticVersion;

class ClasspathEngineVersionProviderTest {

    @Test
    @DisplayName("Lê a versão do recurso de classpath")
    void shouldReadVersion() {
        var provider = ClasspathEngineVersionProvider.fromResource("engine/valid.properties");

        assertEquals(SemanticVersion.of("2.3.4"), provider.current());
    }

    @Test
    @DisplayName("Recurso real (filtrado pelo Maven) contém semver válido")
    void filteredResourceShouldBeValid() {
        ClasspathEngineVersionProvider.load().current();
    }

    @Test
    @DisplayName("Placeholder não filtrado impede a inicialização (fail-fast)")
    void unfilteredPlaceholderShouldFail() {
        assertThrows(IllegalStateException.class,
            () -> ClasspathEngineVersionProvider.fromResource("engine/unfiltered.properties"));
    }

    @Test
    @DisplayName("Recurso ausente impede a inicialização (fail-fast)")
    void missingResourceShouldFail() {
        assertThrows(IllegalStateException.class,
            () -> ClasspathEngineVersionProvider.fromResource("engine/missing.properties"));
    }
}
