package com.rcl.msrpg.system.application.dto;

import java.time.Instant;

public record RpgSystemResult(
    String id,
    String name,
    String description,
    String engineVersion,
    String contentVersion,
    String defaultSyncPolicy,
    String missingComponentPolicy,
    String conflictResolutionStrategy,
    Instant createdAt,
    Instant updatedAt
) {
}
