package com.rcl.msrpg.system.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

@RegisterConstructorMapper(RpgSystemEntity.class)
public interface JdbiRpgSystemRepository {

    @SqlUpdate("""
        INSERT INTO rpg_systems (
            id,
            name,
            description,
            engine_version,
            content_version,
            default_resolution_policy_id,
            sync_policy,
            settings,
            created_at,
            updated_at
        ) VALUES (
            :id,
            :name,
            :description,
            :engineVersion,
            :contentVersion,
            :defaultResolutionPolicyId,
            :syncPolicy,
            :settingsJson,
            :createdAt,
            :updatedAt
        )      
    """)
    void save(@BindBean RpgSystemEntity entity);

    @SqlUpdate("""
        UPDATE rpg_systems
        SET
            name = :name,
            description = :description,
            engine_version = :engineVersion,
            content_version = :contentVersion,
            default_resolution_policy_id = :defaultResolutionPolicyId,
            sync_policy = :syncPolicy,
            settings = :settingsJson,
            updated_at = :updatedAt
        WHERE id = :id
        """)
    int update(@BindBean RpgSystemEntity entity);

    @SqlQuery("""
        SELECT
            id,
            name,
            description,
            engine_version AS engineVersion,
            content_version AS contentVersion,
            default_resolution_policy_id AS defaultResolutionPolicyId,
            sync_policy AS syncPolicy,
            settings AS settingsJson,
            created_at AS createdAt,
            updated_at AS updatedAt
        FROM rpg_systems
        WHERE id = :id
        """)
    Optional<RpgSystemEntity> findById(@Bind("id") String id);

    @SqlQuery("""
        SELECT
            id,
            name,
            description,
            engine_version AS engineVersion,
            content_version AS contentVersion,
            default_resolution_policy_id AS defaultResolutionPolicyId,
            sync_policy AS syncPolicy,
            settings AS settingsJson,
            created_at AS createdAt,
            updated_at AS updatedAt
        FROM rpg_systems
        WHERE name = :name
        """)
    Optional<RpgSystemEntity> findByName(@Bind("id") String id);


    @SqlQuery("""
        SELECT
            id,
            name,
            description,
            engine_version AS engineVersion,
            content_version AS contentVersion,
            default_resolution_policy_id AS defaultResolutionPolicyId,
            sync_policy AS syncPolicy,
            settings AS settingsJson,
            created_at AS createdAt,
            updated_at AS updatedAt
        FROM rpg_systems
        ORDER BY created_at DESC
        """)
    List<RpgSystemEntity> findAll();

    @SqlUpdate("""
        DELETE FROM rpg_systems
        WHERE id = :id
        """)
    int deleteById(@Bind("id") String id);

    @SqlQuery("""
        SELECT EXISTS (
            SELECT 1
            FROM rpg_systems
            WHERE id = :id
        )
        """)
    boolean existsById(@Bind("id") String id);

}
