package com.rcl.msrpg.shared.identifier;

import java.util.UUID;

public class AvatarAssetId extends Identifier<AvatarAssetId>{

    private AvatarAssetId(UUID value) {
        super(value);
    }

    public static AvatarAssetId generate() {
        return new AvatarAssetId(UUID.randomUUID());
    }

    public static AvatarAssetId of(UUID value) {
        return new AvatarAssetId(value);
    }

    public static AvatarAssetId of(String value) {
        return new AvatarAssetId(parseUUID(value));
    }

}
