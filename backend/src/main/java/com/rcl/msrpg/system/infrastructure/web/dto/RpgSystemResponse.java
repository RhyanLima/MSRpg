package com.rcl.msrpg.system.infrastructure.web.dto;

public record RpgSystemResponse(
    String id,
    String name,
    String description,
    String engineVersion,
    String contentVersion,
    String defaultSyncPolicy,
    String missingComponentPolicy,
    String conflictResolutionStrategy,
    String createdAt,
    String updatedAt
) {
}
