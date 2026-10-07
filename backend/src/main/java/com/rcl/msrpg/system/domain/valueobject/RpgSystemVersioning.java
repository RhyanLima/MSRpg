package com.rcl.msrpg.system.domain.valueobject;

import java.util.Objects;

import com.rcl.msrpg.core.valueobject.SemanticVersion;

public record RpgSystemVersioning(SemanticVersion engineVersion, SemanticVersion contentVersion) {

    public RpgSystemVersioning {
        Objects.requireNonNull(engineVersion, "engineVersion");
        contentVersion = Objects.requireNonNullElse(contentVersion, SemanticVersion.initialContent());
    }

    public RpgSystemVersioning withContentVersion(SemanticVersion version) {
        return new RpgSystemVersioning(engineVersion, version);
    }

}