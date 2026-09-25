package com.rcl.msrpg.auth.domain.model;

import java.time.Instant;

import com.rcl.msrpg.shared.identifier.AvatarAssetId;
import com.rcl.msrpg.shared.identifier.LocalUserId;

public class LocalUser {

    private final LocalUserId id;
    private String displayName;
    private UserKind kind;
    private AvatarAssetId avatarAssetId;
    private final Instant createdAt;
    private Instant updatedAt;

    private LocalUser(LocalUserId id, String displayName, UserKind kind, AvatarAssetId avatarAssetId, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.displayName = displayName;
        this.kind = kind;
        this.avatarAssetId = avatarAssetId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static LocalUser create(LocalUserId id, String displayName, UserKind kind, AvatarAssetId avatarAssetId) {
        return new LocalUser(id, displayName, kind, avatarAssetId, Instant.now(), null);
    }

    public static LocalUser reconstruct(LocalUserId id, String displayName, UserKind kind, AvatarAssetId avatarAssetId, Instant createdAt, Instant updatedAt) {
        return new LocalUser(id, displayName, kind, avatarAssetId, createdAt, updatedAt);
    }
    
    public static enum UserKind {
        LOCAL,
        REMOTE,
    }

}
