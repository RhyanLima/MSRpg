package com.rcl.msrpg.system.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SqlLikeEscaperTest {

    @Test
    @DisplayName("Curingas de LIKE são escapados como literais")
    void likeWildcardsShouldBeEscaped() {
        assertEquals("100\\%", SqlLikeEscaper.escape("100%"));
        assertEquals("dark\\_souls", SqlLikeEscaper.escape("dark_souls"));
        assertEquals("a\\\\b", SqlLikeEscaper.escape("a\\b"));
        assertEquals("plain", SqlLikeEscaper.escape("plain"));
    }
}
