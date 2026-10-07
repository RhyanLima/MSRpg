package com.rcl.msrpg.system.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.rcl.msrpg.bootstrap.DatabaseBootstrap;
import com.rcl.msrpg.bootstrap.MigrationBootstrap;
import com.rcl.msrpg.core.valueobject.SemanticVersion;
import com.rcl.msrpg.system.application.exception.RpgSystemInUseException;
import com.rcl.msrpg.system.domain.enumeration.SyncPolicy;
import com.rcl.msrpg.system.domain.model.RpgSystem;
import com.rcl.msrpg.system.domain.model.RpgSystemSearchCriteria;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemName;
import com.rcl.msrpg.system.support.RpgSystemFixtures;

/**
 * Executa as migrations reais num SQLite temporário e valida o adapter contra
 * o schema mandatório, incluindo is_default nas políticas e o bloqueio de
 * exclusão de sistemas com conteúdo.
 */
@Tag("integration")
class RpgSystemRepositoryAdapterIntegrationTest {

    @TempDir
    Path tempDir;

    private Jdbi jdbi;
    private RpgSystemRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        String databasePath = tempDir.resolve("msrpg-test.db").toString();
        MigrationBootstrap.migrate(databasePath);
        jdbi = DatabaseBootstrap.createJdbi(databasePath);
        adapter = new RpgSystemRepositoryAdapter(jdbi, new RpgSystemPersistenceMapper());
    }

    private void insertResolutionPolicy(RpgSystem system, String id, int isDefault) {
        jdbi.useHandle(h -> h.execute("""
            INSERT INTO resolution_policies (id, system_id, key, name, default_order, is_default, created_at)
            VALUES (?, ?, ?, 'Política', '["DAMAGE"]', ?, '2026-10-01T12:00:00Z')
            """, id, system.id().toString(), "key-" + id, isDefault));
    }

    @Test
    @DisplayName("Sistema existe sem nenhuma política e faz ida e volta completa")
    void systemShouldExistWithoutPolicies() {
        RpgSystem system = RpgSystemFixtures.system("Tormenta");

        adapter.add(system);

        RpgSystem loaded = adapter.findById(system.id()).orElseThrow();
        assertEquals(system.profile(), loaded.profile());
        assertEquals(system.versioning(), loaded.versioning());
        assertEquals(system.defaultSyncPolicy(), loaded.defaultSyncPolicy());
        assertEquals(system.behavior(), loaded.behavior());
    }

    @Test
    @DisplayName("No máximo uma política padrão por sistema (índice parcial)")
    void atMostOneDefaultPolicyPerSystem() {
        RpgSystem system = RpgSystemFixtures.system("Tormenta");
        adapter.add(system);

        insertResolutionPolicy(system, "p1", 1);
        insertResolutionPolicy(system, "p2", 0);

        assertThrows(RuntimeException.class, () -> insertResolutionPolicy(system, "p3", 1));
    }

    @Test
    @DisplayName("Integridade referencial está ativa em TODA conexão")
    void foreignKeysShouldBeEnforcedOnEveryConnection() {
        for (int attempt = 0; attempt < 3; attempt++) {
            int enabled = jdbi.withHandle(h -> h.createQuery("PRAGMA foreign_keys").mapTo(Integer.class).one());
            assertEquals(1, enabled);
        }
    }

    @Test
    @DisplayName("update persiste campos editáveis e preserva engine_version")
    void updateShouldPersist() {
        RpgSystem system = RpgSystemFixtures.system("Tormenta");
        adapter.add(system);

        system.changeContentVersion(SemanticVersion.of("2.0.0"), RpgSystemFixtures.T0.plusSeconds(60));
        adapter.update(system);

        RpgSystem loaded = adapter.findById(system.id()).orElseThrow();
        assertEquals(SemanticVersion.of("2.0.0"), loaded.versioning().contentVersion());
        assertEquals(RpgSystemFixtures.ENGINE, loaded.versioning().engineVersion());
    }

    @Test
    @DisplayName("search combina filtros e trata % e _ como literais")
    void searchShouldFilterSafely() {
        adapter.add(RpgSystemFixtures.system("100% Sombrio", RpgSystemFixtures.T0));
        adapter.add(RpgSystemFixtures.system("Sombrio Total", RpgSystemFixtures.T0.plusSeconds(1)));

        assertEquals(2, adapter.search(RpgSystemSearchCriteria.any()).size());
        assertEquals(1, adapter.search(RpgSystemSearchCriteria.any().withNameContaining("%")).size());
        assertEquals(0, adapter.search(RpgSystemSearchCriteria.any().withNameContaining("_")).size());
        assertEquals(0, adapter.search(RpgSystemSearchCriteria.any()
            .withNameContaining("sombrio").withSyncPolicy(SyncPolicy.APPLY_TO_NEXT_SESSION)).size());
        assertTrue(adapter.existsByName(RpgSystemName.of("SOMBRIO TOTAL")));
    }

    @Test
    @DisplayName("delete remove sistema vazio; com conteúdo (ex.: política) vira 409 sem apagar nada")
    void deleteShouldNeverCascadeUserContent() {
        RpgSystem empty = RpgSystemFixtures.system("Vazio");
        RpgSystem withContent = RpgSystemFixtures.system("Com Conteúdo");
        adapter.add(empty);
        adapter.add(withContent);
        insertResolutionPolicy(withContent, "p1", 1);

        adapter.delete(empty.id());
        assertFalse(adapter.existsById(empty.id()));

        assertThrows(RpgSystemInUseException.class, () -> adapter.delete(withContent.id()));
        assertTrue(adapter.existsById(withContent.id()));
    }
}
