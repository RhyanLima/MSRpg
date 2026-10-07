package com.rcl.msrpg.system.infrastructure.web.dto;

public record CreateRpgSystemRequest(
    String name,
    String description,
    String contentVersion,
    String defaultSyncPolicy,
    String missingComponentPolicy,
    String conflictResolutionStrategy
) {
}
