package com.rcl.msrpg.system.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.rcl.msrpg.core.exception.DomainValidationException;

class RpgSystemNameTest {

    @Test
    @DisplayName("Deve normalizar espaços externos")
    void shouldStrip() {
        assertEquals("Tormenta", RpgSystemName.of("  Tormenta ").value());
    }

    @Test
    @DisplayName("Deve rejeitar nulo, vazio, curto demais e longo demais")
    void shouldRejectInvalidLength() {
        assertThrows(DomainValidationException.class, () -> RpgSystemName.of(null));
        assertThrows(DomainValidationException.class, () -> RpgSystemName.of("   "));
        assertThrows(DomainValidationException.class, () -> RpgSystemName.of("ab"));
        assertThrows(DomainValidationException.class, () -> RpgSystemName.of("x".repeat(101)));
    }

    @Test
    @DisplayName("Deve rejeitar caracteres de controle")
    void shouldRejectControlCharacters() {
        assertThrows(DomainValidationException.class, () -> RpgSystemName.of("Nome\u0000Oculto"));
        assertThrows(DomainValidationException.class, () -> RpgSystemName.of("Linha\nQuebrada"));
    }

    @Test
    @DisplayName("sameAs compara sem diferenciar maiúsculas")
    void sameAsShouldIgnoreCase() {
        assertTrue(RpgSystemName.of("Dark Fantasy").sameAs(RpgSystemName.of("DARK fantasy")));
    }
}
