package com.rcl.msrpg.system.application.dto;

public record RpgSystemFilterCommand(
    String name,
    String engineVersion,
    String contentVersion,
    String syncPolicy,
    String defaultResolutionPolicyId
) {

    public boolean hasName() {
        return name != null && !name.isBlank();
    }

    public boolean hasEngineVersion() {
        return engineVersion != null && !engineVersion.isBlank();
    }

    public boolean hasContentVersion() {
        return contentVersion != null && !contentVersion.isBlank();
    }

    public boolean hasSyncPolicy() {
        return syncPolicy != null && !syncPolicy.isBlank();
    }

    public boolean hasDefaultResolutionPolicyId() {
        return defaultResolutionPolicyId != null && !defaultResolutionPolicyId.isBlank();
    }

    public boolean isEmpty() {
        return !hasName()
            && !hasEngineVersion()
            && !hasContentVersion()
            && !hasSyncPolicy()
            && !hasDefaultResolutionPolicyId();
    }
}
