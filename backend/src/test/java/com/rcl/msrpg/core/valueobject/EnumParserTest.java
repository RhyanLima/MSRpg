package com.rcl.msrpg.core.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.rcl.msrpg.core.exception.DomainValidationException;

class EnumParserTest {

    enum Sample { FIRST, SECOND }

    @Test
    @DisplayName("Deve converter ignorando caixa e espaços")
    void shouldParseCaseInsensitive() {
        assertEquals(Sample.SECOND, EnumParser.parse(Sample.class, "  second ", "Field"));
    }

    @Test
    @DisplayName("Erro lista valores aceitos e não ecoa a entrada")
    void errorShouldNotEchoInput() {
        var error = assertThrows(DomainValidationException.class,
            () -> EnumParser.parse(Sample.class, "<script>", "Field"));

        assertTrue(error.getMessage().contains("FIRST, SECOND"));
        assertFalse(error.getMessage().contains("<script>"));
    }

    @Test
    @DisplayName("Valor vazio é rejeitado")
    void shouldRejectBlank() {
        assertThrows(DomainValidationException.class, () -> EnumParser.parse(Sample.class, " ", "Field"));
    }
}
