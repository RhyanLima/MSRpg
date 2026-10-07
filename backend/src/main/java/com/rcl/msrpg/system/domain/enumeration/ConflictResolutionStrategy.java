package com.rcl.msrpg.system.domain.enumeration;

import com.rcl.msrpg.core.valueobject.EnumParser;

public enum ConflictResolutionStrategy {
    ASK_USER,
    SKIP,
    OVERWRITE,
    CREATE_COPY;

    public static ConflictResolutionStrategy defaultStrategy() {
        return ASK_USER;
    }

    public static ConflictResolutionStrategy parse(String raw) {
        return EnumParser.parse(ConflictResolutionStrategy.class, raw, "Conflict resolution strategy");
    }
}
