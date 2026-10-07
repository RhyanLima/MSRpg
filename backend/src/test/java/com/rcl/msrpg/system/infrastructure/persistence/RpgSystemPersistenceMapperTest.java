package com.rcl.msrpg.system.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.rcl.msrpg.system.domain.model.RpgSystem;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemDescription;
import com.rcl.msrpg.system.support.RpgSystemFixtures;

class RpgSystemPersistenceMapperTest {

    private final RpgSystemPersistenceMapper mapper = new RpgSystemPersistenceMapper();

    @Test
    @DisplayName("Entity usa nomes de enum e timestamps ISO-8601")
    void toEntityShouldUseDatabaseRepresentation() {
        RpgSystemEntity entity = mapper.toEntity(RpgSystemFixtures.system("Tormenta"));

        assertEquals("0.1.0", entity.engineVersion());
        assertEquals("APPLY_TO_NEW_ONLY", entity.defaultSyncPolicy());
        assertEquals("WARN_AND_SKIP_STEP", entity.missingComponentPolicy());
        assertEquals("ASK_USER", entity.conflictResolutionStrategy());
        assertEquals("2026-10-01T12:00:00Z", entity.createdAt());
        assertNull(entity.updatedAt());
    }

    @Test
    @DisplayName("Ida e volta preserva todos os campos")
    void roundTripShouldPreserveState() {
        RpgSystem original = RpgSystemFixtures.system("Tormenta");
        original.changeProfile(original.profile().describedAs(RpgSystemDescription.empty()),
            RpgSystemFixtures.T0.plusSeconds(5));

        RpgSystem restored = mapper.toDomain(mapper.toEntity(original));

        assertEquals(original.id(), restored.id());
        assertEquals(original.profile(), restored.profile());
        assertEquals(original.versioning(), restored.versioning());
        assertEquals(original.defaultSyncPolicy(), restored.defaultSyncPolicy());
        assertEquals(original.behavior(), restored.behavior());
        assertEquals(original.timestamps(), restored.timestamps());
        assertTrue(restored.profile().description().text().isEmpty());
    }

    @Test
    @DisplayName("Summary é montado a partir da projeção enxuta")
    void toSummaryShouldMapRow() {
        var row = new RpgSystemSummaryRow("6f1c5f0e-7a3e-4f6a-9d7a-1c2b3d4e5f60", "Tormenta", null,
            "0.1.0", "1.0.0", "2026-10-01T12:00:00Z", "2026-10-02T12:00:00Z");

        var summary = mapper.toSummary(row);

        assertEquals("Tormenta", summary.profile().name().value());
        assertEquals("2026-10-02T12:00:00Z", summary.timestamps().updatedAt().toString());
    }
}
