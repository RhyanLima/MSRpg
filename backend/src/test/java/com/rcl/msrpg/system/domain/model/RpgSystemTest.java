package com.rcl.msrpg.system.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.rcl.msrpg.core.valueobject.SemanticVersion;
import com.rcl.msrpg.system.domain.enumeration.ConflictResolutionStrategy;
import com.rcl.msrpg.system.domain.enumeration.SyncPolicy;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemName;
import com.rcl.msrpg.system.support.RpgSystemFixtures;

class RpgSystemTest {

    private static final Instant LATER = RpgSystemFixtures.T0.plusSeconds(3600);

    @Test
    @DisplayName("Criação registra createdAt e nenhum updatedAt")
    void createShouldStampCreation() {
        RpgSystem system = RpgSystemFixtures.system("Tormenta");

        assertEquals(RpgSystemFixtures.T0, system.timestamps().createdAt());
        assertTrue(system.timestamps().lastUpdate().isEmpty());
    }

    @Test
    @DisplayName("Cada mudança substitui o grupo inteiro e registra updatedAt")
    void changesShouldTouchUpdatedAt() {
        RpgSystem system = RpgSystemFixtures.system("Tormenta");

        system.changeProfile(system.profile().renamedTo(RpgSystemName.of("Tormenta 20")), LATER);
        system.changeContentVersion(SemanticVersion.of("1.1.0"), LATER);
        system.changeDefaultSyncPolicy(SyncPolicy.APPLY_TO_NEXT_SESSION, LATER);
        system.changeBehavior(system.behavior().withConflictResolutionStrategy(ConflictResolutionStrategy.SKIP), LATER);

        assertEquals("Tormenta 20", system.profile().name().value());
        assertEquals(SemanticVersion.of("1.1.0"), system.versioning().contentVersion());
        assertEquals(SyncPolicy.APPLY_TO_NEXT_SESSION, system.defaultSyncPolicy());
        assertEquals(ConflictResolutionStrategy.SKIP, system.behavior().conflictResolutionStrategy());
        assertEquals(LATER, system.timestamps().updatedAt());
    }

    @Test
    @DisplayName("Mudar a versão de conteúdo nunca altera a versão do engine")
    void contentVersionChangeKeepsEngineVersion() {
        RpgSystem system = RpgSystemFixtures.system("Tormenta");

        system.changeContentVersion(SemanticVersion.of("9.0.0"), LATER);

        assertEquals(RpgSystemFixtures.ENGINE, system.versioning().engineVersion());
    }

    @Test
    @DisplayName("Grupos obrigatórios não aceitam nulo")
    void shouldRejectNulls() {
        RpgSystem system = RpgSystemFixtures.system("Tormenta");

        assertThrows(NullPointerException.class, () -> system.changeProfile(null, LATER));
        assertThrows(NullPointerException.class, () -> system.changeDefaultSyncPolicy(null, LATER));
        assertThrows(NullPointerException.class, () -> system.changeContentVersion(null, LATER));
    }
}