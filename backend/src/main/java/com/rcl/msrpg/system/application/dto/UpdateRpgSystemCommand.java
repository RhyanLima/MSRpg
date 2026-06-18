package com.rcl.msrpg.system.application.dto;

public record UpdateRpgSystemCommand(
    String name,
    String description,
    String engineVersion,
    String contentVersion,
    String defaultResolutionPolicyId,
    String syncPolicy,
    String settingsJson
) {

}
