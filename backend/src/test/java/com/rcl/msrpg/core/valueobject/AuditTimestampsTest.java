package com.rcl.msrpg.core.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.rcl.msrpg.core.exception.DomainValidationException;

class AuditTimestampsTest {

    private static final Instant CREATED = Instant.parse("2026-10-01T10:00:00Z");

    @Test
    @DisplayName("Recém-criado não possui updatedAt")
    void newTimestampsHaveNoUpdate() {
        assertTrue(AuditTimestamps.createdAt(CREATED).lastUpdate().isEmpty());
    }

    @Test
    @DisplayName("touchedAt preserva createdAt e registra updatedAt")
    void touchShouldKeepCreatedAt() {
        Instant later = CREATED.plusSeconds(60);
        AuditTimestamps touched = AuditTimestamps.createdAt(CREATED).touchedAt(later);

        assertEquals(CREATED, touched.createdAt());
        assertEquals(later, touched.updatedAt());
    }

    @Test
    @DisplayName("updatedAt anterior a createdAt é inválido")
    void shouldRejectUpdateBeforeCreation() {
        assertThrows(DomainValidationException.class,
            () -> new AuditTimestamps(CREATED, CREATED.minusSeconds(1)));
    }
}
