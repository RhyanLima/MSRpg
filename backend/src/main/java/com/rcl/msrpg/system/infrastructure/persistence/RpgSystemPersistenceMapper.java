package com.rcl.msrpg.system.infrastructure.persistence;

import java.time.Instant;

import com.rcl.msrpg.core.identifier.RpgSystemId;
import com.rcl.msrpg.core.valueobject.AuditTimestamps;
import com.rcl.msrpg.core.valueobject.SemanticVersion;
import com.rcl.msrpg.system.domain.enumeration.ConflictResolutionStrategy;
import com.rcl.msrpg.system.domain.enumeration.MissingComponentPolicy;
import com.rcl.msrpg.system.domain.enumeration.SyncPolicy;
import com.rcl.msrpg.system.domain.model.RpgSystem;
import com.rcl.msrpg.system.domain.model.RpgSystemSummary;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemBehavior;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemDescription;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemName;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemProfile;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemVersioning;

public class RpgSystemPersistenceMapper {

    public RpgSystemEntity toEntity(RpgSystem system) {
        var profile = system.profile();
        var versioning = system.versioning();
        var behavior = system.behavior();

        return new RpgSystemEntity(
            system.id().toString(),
            profile.name().value(),
            profile.description().text().orElse(null),
            versioning.engineVersion().value(),
            versioning.contentVersion().value(),
            system.defaultSyncPolicy().name(),
            behavior.missingComponentPolicy().name(),
            behavior.conflictResolutionStrategy().name(),
            system.timestamps().createdAt().toString(),
            system.timestamps().lastUpdate().map(Instant::toString).orElse(null)
        );
    }

    public RpgSystem toDomain(RpgSystemEntity entity) {
        return RpgSystem.reconstruct(
            RpgSystemId.of(entity.id()),
            toProfile(entity.name(), entity.description()),
            toVersioning(entity.engineVersion(), entity.contentVersion()),
            SyncPolicy.valueOf(entity.defaultSyncPolicy()),
            new RpgSystemBehavior(
                MissingComponentPolicy.valueOf(entity.missingComponentPolicy()),
                ConflictResolutionStrategy.valueOf(entity.conflictResolutionStrategy())
            ),
            toTimestamps(entity.createdAt(), entity.updatedAt())
        );
    }

    public RpgSystemSummary toSummary(RpgSystemSummaryRow row) {
        return new RpgSystemSummary(
            RpgSystemId.of(row.id()),
            toProfile(row.name(), row.description()),
            toVersioning(row.engineVersion(), row.contentVersion()),
            toTimestamps(row.createdAt(), row.updatedAt())
        );
    }

    private RpgSystemProfile toProfile(String name, String description) {
        return new RpgSystemProfile(RpgSystemName.of(name), RpgSystemDescription.of(description));
    }

    private RpgSystemVersioning toVersioning(String engineVersion, String contentVersion) {
        return new RpgSystemVersioning(SemanticVersion.of(engineVersion), SemanticVersion.of(contentVersion));
    }

    private AuditTimestamps toTimestamps(String createdAt, String updatedAt) {
        return new AuditTimestamps(
            Instant.parse(createdAt),
            updatedAt == null ? null : Instant.parse(updatedAt)
        );
    }

}
