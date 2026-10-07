package com.rcl.msrpg.system.domain.model;

import java.util.Objects;

import com.rcl.msrpg.core.identifier.RpgSystemId;
import com.rcl.msrpg.core.valueobject.AuditTimestamps;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemProfile;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemVersioning;

/** Modelo simplificado de RpgSystem. */
public record RpgSystemSummary(
    RpgSystemId id,
    RpgSystemProfile profile,
    RpgSystemVersioning versioning,
    AuditTimestamps timestamps
) {

    public RpgSystemSummary {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(profile, "profile");
        Objects.requireNonNull(versioning, "versioning");
        Objects.requireNonNull(timestamps, "timestamps");
    }

}
