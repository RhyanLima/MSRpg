package com.rcl.msrpg.system.application.dto;

public record UpdateRpgSystemCommand(
    String name,
    String description,
    String contentVersion,
    String defaultSyncPolicy,
    String missingComponentPolicy,
    String conflictResolutionStrategy
) {

    public static UpdateRpgSystemCommand empty() {
        return new UpdateRpgSystemCommand(null, null, null, null, null, null);
    }
}
