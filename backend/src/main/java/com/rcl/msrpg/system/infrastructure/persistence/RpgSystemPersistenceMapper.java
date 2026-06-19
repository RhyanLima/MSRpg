package com.rcl.msrpg.system.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rcl.msrpg.shared.identifier.ResolutionPolicyId;
import com.rcl.msrpg.shared.identifier.RpgSystemId;
import com.rcl.msrpg.system.domain.model.RpgSystem;
import com.rcl.msrpg.system.domain.model.RpgSystem.SyncPolicy;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemSettings;

public class RpgSystemPersistenceMapper {

    private final ObjectMapper objectMapper;


    public RpgSystemPersistenceMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public RpgSystemEntity toEntity(RpgSystem system) {
        return new RpgSystemEntity(
            system.id().toString(),
            system.name(),
            system.description(),
            system.engineVersion(),
            system.contentVersion(),
            system.defaultResolutionPolicyId() != null
                ? system.defaultResolutionPolicyId().toString()
                : null,
            system.syncPolicy().name(),
            toJson(system.settings()),
            system.createdAt(),
            system.updatedAt()
        );
    }

    public RpgSystem toDomain(RpgSystemEntity entity) {
        return RpgSystem.reconstruct(
            RpgSystemId.of(entity.id()),
            entity.name(),
            entity.description(),
            entity.engineVersion(),
            entity.contentVersion(),
            entity.defaultResolutionPolicyId() != null
                ? ResolutionPolicyId.of(entity.defaultResolutionPolicyId())
                : null,
            SyncPolicy.valueOf(entity.syncPolicy()),
            fromJson(entity.settingsJson()),
            entity.createdAt(),
            entity.updatedAt()
        );
    }

    private String toJson(RpgSystemSettings settings) {
        try {
            return objectMapper.writeValueAsString(
                settings != null ? settings : RpgSystemSettings.defaults()
            );
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize RPG system settings.", e);
        }
    }

    private RpgSystemSettings fromJson(String json) {
        if (json == null || json.isBlank()) {
            return RpgSystemSettings.defaults();
        }

        try {
            return objectMapper.readValue(json, RpgSystemSettings.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize RPG system settings.", e);
        }
    }

}
