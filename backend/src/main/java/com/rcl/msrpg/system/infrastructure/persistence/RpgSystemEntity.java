package com.rcl.msrpg.system.infrastructure.persistence;

import java.time.Instant;

public record RpgSystemEntity(
    String id,
    String name,
    String description,
    String engineVersion,
    String contentVersion,
    String defaultResolutionPolicyId,
    String syncPolicy,
    String settingsJson,
    Instant createdAt,
    Instant updatedAt

) {
}
