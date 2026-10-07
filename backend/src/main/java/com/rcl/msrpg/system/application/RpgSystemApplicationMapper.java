package com.rcl.msrpg.system.application;

import com.rcl.msrpg.core.identifier.RpgSystemId;
import com.rcl.msrpg.core.valueobject.SemanticVersion;
import com.rcl.msrpg.system.application.dto.RpgSystemFilterCommand;
import com.rcl.msrpg.system.application.dto.RpgSystemResult;
import com.rcl.msrpg.system.application.dto.RpgSystemSummaryResult;
import com.rcl.msrpg.system.application.exception.RpgSystemValidationException;
import com.rcl.msrpg.system.domain.enumeration.SyncPolicy;
import com.rcl.msrpg.system.domain.model.RpgSystem;
import com.rcl.msrpg.system.domain.model.RpgSystemSearchCriteria;
import com.rcl.msrpg.system.domain.model.RpgSystemSummary;

public final class RpgSystemApplicationMapper {

    private RpgSystemApplicationMapper() {}

    public static RpgSystemResult toResult(RpgSystem system) {
        var profile = system.profile();
        var versioning = system.versioning();
        var behavior = system.behavior();
        var timestamps = system.timestamps();

        return new RpgSystemResult(
            system.id().toString(),
            profile.name().value(),
            profile.description().text().orElse(null),
            versioning.engineVersion().value(),
            versioning.contentVersion().value(),
            system.defaultSyncPolicy().name(),
            behavior.missingComponentPolicy().name(),
            behavior.conflictResolutionStrategy().name(),
            timestamps.createdAt(),
            timestamps.updatedAt()
        );
    }

    public static RpgSystemSummaryResult toSummaryResult(RpgSystemSummary summary) {
        return new RpgSystemSummaryResult(
            summary.id().toString(),
            summary.profile().name().value(),
            summary.profile().description().text().orElse(null),
            summary.versioning().engineVersion().value(),
            summary.versioning().contentVersion().value(),
            summary.timestamps().createdAt(),
            summary.timestamps().updatedAt()
        );
    }

    public static RpgSystemSearchCriteria toCriteria(RpgSystemFilterCommand filter) {
        if (filter == null) {
            return RpgSystemSearchCriteria.any();
        }
        var criteria = RpgSystemSearchCriteria.any().withNameContaining(filter.name());
        if (hasText(filter.engineVersion())) {
            criteria = criteria.withEngineVersion(SemanticVersion.of(filter.engineVersion()));
        }
        if (hasText(filter.contentVersion())) {
            criteria = criteria.withContentVersion(SemanticVersion.of(filter.contentVersion()));
        }
        if (hasText(filter.defaultSyncPolicy())) {
            criteria = criteria.withSyncPolicy(SyncPolicy.parse(filter.defaultSyncPolicy()));
        }
        return criteria;
    }

    /** UUID malformado vira 400 (antes chegava como 500). */
    public static RpgSystemId toRpgSystemId(String raw) {
        if (!hasText(raw)) {
            throw new RpgSystemValidationException("RPG system id is required.");
        }
        try {
            return RpgSystemId.of(raw.strip());
        } catch (IllegalArgumentException exception) {
            throw new RpgSystemValidationException("RPG system id must be a valid UUID.");
        }
    }

    public static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

}
