package com.rcl.msrpg.system.domain.enumeration;

import com.rcl.msrpg.core.valueobject.EnumParser;

public enum MissingComponentPolicy {
    WARN_AND_SKIP_STEP,
    FAIL_EVENT,
    IGNORE_SILENTLY;

    public static MissingComponentPolicy defaultPolicy() {
        return WARN_AND_SKIP_STEP;
    }

    public static MissingComponentPolicy parse(String raw) {
        return EnumParser.parse(MissingComponentPolicy.class, raw, "Missing component policy");
    }
}
