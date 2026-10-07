package com.rcl.msrpg.system.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.rcl.msrpg.core.exception.DomainValidationException;
import com.rcl.msrpg.core.valueobject.SemanticVersion;
import com.rcl.msrpg.system.domain.enumeration.SyncPolicy;

class RpgSystemSearchCriteriaTest {

    @Test
    @DisplayName("any() não aplica filtros")
    void anyShouldHaveNoFilters() {
        var criteria = RpgSystemSearchCriteria.any();

        assertTrue(criteria.nameTerm().isEmpty());
        assertTrue(criteria.engineVersion().isEmpty());
        assertTrue(criteria.contentVersion().isEmpty());
        assertTrue(criteria.syncPolicy().isEmpty());
    }

    @Test
    @DisplayName("Termo em branco é ignorado")
    void blankNameShouldBeIgnored() {
        var criteria = RpgSystemSearchCriteria.any();
        assertSame(criteria, criteria.withNameContaining("  "));
    }

    @Test
    @DisplayName("Filtros são combináveis e imutáveis")
    void filtersShouldCombine() {
        var criteria = RpgSystemSearchCriteria.any()
            .withNameContaining(" dark ")
            .withEngineVersion(SemanticVersion.of("0.1.0"))
            .withSyncPolicy(SyncPolicy.APPLY_TO_NEXT_SESSION);

        assertEquals("dark", criteria.nameTerm().orElseThrow());
        assertEquals(SemanticVersion.of("0.1.0"), criteria.engineVersion().orElseThrow());
        assertEquals(SyncPolicy.APPLY_TO_NEXT_SESSION, criteria.syncPolicy().orElseThrow());
        assertTrue(RpgSystemSearchCriteria.any().nameTerm().isEmpty());
    }

    @Test
    @DisplayName("Termo de busca tem tamanho limitado")
    void nameTermShouldBeBounded() {
        assertThrows(DomainValidationException.class,
            () -> RpgSystemSearchCriteria.any().withNameContaining("x".repeat(101)));
    }
}
