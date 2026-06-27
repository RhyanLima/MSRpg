package com.rcl.msrpg.system.infrastructure.web.dto;

public record RpgSystemFilterRequest(
    String name,
    String engineVersion,
    String contentVersion,
    String syncPolicy,
    String defaultResolutionPolicyId
) {

}
