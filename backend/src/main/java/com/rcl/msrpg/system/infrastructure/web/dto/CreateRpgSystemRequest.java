package com.rcl.msrpg.system.infrastructure.web.dto;

public record CreateRpgSystemRequest(
    String name,
    String description,
    String engineVersion,
    String contentVersion,
    String defaultResolutionPolicyId,
    String syncPolicy,
    String settingsJson
) {

}
