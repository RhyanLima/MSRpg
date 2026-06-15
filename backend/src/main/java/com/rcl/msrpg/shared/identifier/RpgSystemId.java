package com.rcl.msrpg.shared.identifier;

import java.util.UUID;

public class RpgSystemId extends Identifier<RpgSystemId> {

    private RpgSystemId(UUID value) {
        super(value);
    }

    public static RpgSystemId generate() {
        return new RpgSystemId(UUID.randomUUID());
    }

    public static RpgSystemId of(UUID value) {
        return new RpgSystemId(value);
    }

}
