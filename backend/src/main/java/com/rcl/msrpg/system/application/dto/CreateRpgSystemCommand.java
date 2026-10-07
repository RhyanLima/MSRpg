package com.rcl.msrpg.system.application.dto;

public record CreateRpgSystemCommand(
    String name,
    String description,
    String contentVersion,
    String defaultSyncPolicy,
    String missingComponentPolicy,
    String conflictResolutionStrategy
) {
}
