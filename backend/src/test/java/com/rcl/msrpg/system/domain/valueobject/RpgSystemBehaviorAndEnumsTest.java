package com.rcl.msrpg.system.domain.valueobject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.rcl.msrpg.core.exception.DomainValidationException;
import com.rcl.msrpg.system.domain.enumeration.ConflictResolutionStrategy;
import com.rcl.msrpg.system.domain.enumeration.MissingComponentPolicy;
import com.rcl.msrpg.system.domain.enumeration.SyncPolicy;

class RpgSystemBehaviorAndEnumsTest {

    @Test
    @DisplayName("Defaults espelham os DEFAULTs do banco")
    void defaultsShouldMatchDatabase() {
        var behavior = RpgSystemBehavior.defaults();

        assertEquals(MissingComponentPolicy.WARN_AND_SKIP_STEP, behavior.missingComponentPolicy());
        assertEquals(ConflictResolutionStrategy.ASK_USER, behavior.conflictResolutionStrategy());
        assertEquals(SyncPolicy.APPLY_TO_NEW_ONLY, SyncPolicy.defaultPolicy());
    }

    @Test
    @DisplayName("Enums aceitam somente valores permitidos pelos CHECKs")
    void enumsShouldParseOnlyAllowedValues() {
        assertEquals(SyncPolicy.APPLY_TO_NEXT_CAMPAIGN, SyncPolicy.parse("apply_to_next_campaign"));
        assertThrows(DomainValidationException.class, () -> SyncPolicy.parse("APPLY_TO_CAMPAIGN"));
        assertThrows(DomainValidationException.class, () -> MissingComponentPolicy.parse("CRASH"));
        assertThrows(DomainValidationException.class, () -> ConflictResolutionStrategy.parse("MERGE"));
    }
}

