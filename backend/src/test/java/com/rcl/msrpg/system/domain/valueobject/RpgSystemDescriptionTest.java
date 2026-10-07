package com.rcl.msrpg.system.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.rcl.msrpg.core.exception.DomainValidationException;

class RpgSystemDescriptionTest {

    @Test
    @DisplayName("Nulo ou em branco vira descrição vazia")
    void blankShouldBeEmpty() {
        assertTrue(RpgSystemDescription.of(null).text().isEmpty());
        assertTrue(RpgSystemDescription.of("   ").text().isEmpty());
        assertEquals(RpgSystemDescription.empty(), RpgSystemDescription.of(""));
    }

    @Test
    @DisplayName("Deve rejeitar descrição acima do limite")
    void shouldRejectTooLong() {
        assertThrows(DomainValidationException.class, () -> RpgSystemDescription.of("x".repeat(2001)));
    }

    @Test
    @DisplayName("Deve preservar texto válido sem espaços externos")
    void shouldKeepText() {
        assertEquals("Sistema sombrio", RpgSystemDescription.of(" Sistema sombrio ").text().orElseThrow());
    }
}

