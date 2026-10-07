package com.rcl.msrpg.system.domain.enumeration;

import com.rcl.msrpg.core.valueobject.EnumParser;

public enum SyncPolicy {
    APPLY_TO_NEW_ONLY,
    APPLY_TO_NEXT_CAMPAIGN,
    APPLY_TO_NEXT_SESSION;

    public static SyncPolicy defaultPolicy() {
        return APPLY_TO_NEW_ONLY;
    }

    public static SyncPolicy parse(String raw) {
        return EnumParser.parse(SyncPolicy.class, raw, "Default sync policy");
    }
}
