package com.rcl.msrpg.shared.identifier;

import java.util.UUID;

public class LocalUserId extends Identifier<LocalUserId> {

    private LocalUserId(UUID value) {
        super(value);
    }

    public static LocalUserId generate() {
        return new LocalUserId(UUID.randomUUID());
    }

    public static LocalUserId of(UUID value) {
        return new LocalUserId(value);
    }

    public static LocalUserId of(String value) {
        return new LocalUserId(parseUUID(value));
    }


}
