package com.rcl.msrpg.core.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.rcl.msrpg.core.exception.DomainValidationException;

class SemanticVersionTest {

    @Test
    @DisplayName("Deve aceitar MAJOR.MINOR.PATCH e remover espaços externos")
    void shouldAcceptValidVersion() {
        assertEquals("1.20.3", SemanticVersion.of(" 1.20.3 ").value());
    }

    @Test
    @DisplayName("Deve rejeitar formatos que o CHECK do banco também rejeita")
    void shouldRejectInvalidFormats() {
        for (String invalid : new String[] {null, "", "1.0", "1.0.0.0", "1..0", "a.b.c", "1.0.0-beta", "-1.0.0", ".1.0"}) {
            assertThrows(DomainValidationException.class, () -> SemanticVersion.of(invalid), "valor: " + invalid);
        }
    }

    @Test
    @DisplayName("Deve rejeitar partes numéricas excessivamente longas")
    void shouldRejectOversizedParts() {
        assertThrows(DomainValidationException.class, () -> SemanticVersion.of("1234567890.0.0"));
    }

    @Test
    @DisplayName("Versão inicial de conteúdo deve ser 1.0.0 (DEFAULT do banco)")
    void initialContentShouldMatchDatabaseDefault() {
        assertEquals(SemanticVersion.of("1.0.0"), SemanticVersion.initialContent());
    }
}
