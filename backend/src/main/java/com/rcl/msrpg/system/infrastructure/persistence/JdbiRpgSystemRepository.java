package com.rcl.msrpg.system.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindMethods;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

@RegisterConstructorMapper(RpgSystemEntity.class)
@RegisterConstructorMapper(RpgSystemSummaryRow.class)
public interface JdbiRpgSystemRepository {

    String DETAIL_COLUMNS = """
            id,
            name,
            description,
            engine_version AS engineVersion,
            content_version AS contentVersion,
            default_sync_policy AS defaultSyncPolicy,
            missing_component_policy AS missingComponentPolicy,
            conflict_resolution_strategy AS conflictResolutionStrategy,
            created_at AS createdAt,
            updated_at AS updatedAt
        """;

    String SUMMARY_COLUMNS = """
            id,
            name,
            description,
            engine_version AS engineVersion,
            content_version AS contentVersion,
            created_at AS createdAt,
            updated_at AS updatedAt
        """;

    @SqlUpdate("""
        INSERT INTO rpg_systems (
            id, name, description, engine_version, content_version,
            default_sync_policy, missing_component_policy, conflict_resolution_strategy,
            created_at, updated_at
        ) VALUES (
            :id, :name, :description, :engineVersion, :contentVersion,
            :defaultSyncPolicy, :missingComponentPolicy, :conflictResolutionStrategy,
            :createdAt, :updatedAt
        )
    """)
    void insert(@BindMethods RpgSystemEntity entity);

    /** engine_version e created_at são imutáveis: ficam fora do UPDATE. */
    @SqlUpdate("""
        UPDATE rpg_systems
        SET
            name = :name,
            description = :description,
            content_version = :contentVersion,
            default_sync_policy = :defaultSyncPolicy,
            missing_component_policy = :missingComponentPolicy,
            conflict_resolution_strategy = :conflictResolutionStrategy,
            updated_at = :updatedAt
        WHERE id = :id
    """)
    int update(@BindMethods RpgSystemEntity entity);

    @SqlQuery("SELECT " + DETAIL_COLUMNS + " FROM rpg_systems WHERE id = :id")
    Optional<RpgSystemEntity> findById(@Bind("id") String id);

    @SqlQuery("SELECT EXISTS (SELECT 1 FROM rpg_systems WHERE id = :id)")
    boolean existsById(@Bind("id") String id);

    @SqlQuery("SELECT EXISTS (SELECT 1 FROM rpg_systems WHERE lower(name) = lower(:name))")
    boolean existsByName(@Bind("name") String name);

    @SqlQuery("""
        SELECT EXISTS (
            SELECT 1 FROM rpg_systems
            WHERE lower(name) = lower(:name) AND id <> :exceptId
        )
    """)
    boolean existsByNameExcept(@Bind("name") String name, @Bind("exceptId") String exceptId);

    @SqlUpdate("DELETE FROM rpg_systems WHERE id = :id")
    int deleteById(@Bind("id") String id);

    /**
     * Filtros opcionais combinados por AND. Parâmetro nulo desliga o filtro.
     * nameTerm já chega com %, _ e \ escapados (SqlLikeEscaper).
     */
    @SqlQuery("SELECT " + SUMMARY_COLUMNS + """
        FROM rpg_systems
        WHERE (:nameTerm IS NULL OR lower(name) LIKE '%' || lower(:nameTerm) || '%' ESCAPE '\\')
          AND (:engineVersion IS NULL OR engine_version = :engineVersion)
          AND (:contentVersion IS NULL OR content_version = :contentVersion)
          AND (:syncPolicy IS NULL OR default_sync_policy = :syncPolicy)
        ORDER BY created_at DESC
    """)
    List<RpgSystemSummaryRow> search(
        @Bind("nameTerm") String nameTerm,
        @Bind("engineVersion") String engineVersion,
        @Bind("contentVersion") String contentVersion,
        @Bind("syncPolicy") String syncPolicy
    );

    @SqlQuery("SELECT COUNT(*) FROM rpg_systems")
    long count();

}
