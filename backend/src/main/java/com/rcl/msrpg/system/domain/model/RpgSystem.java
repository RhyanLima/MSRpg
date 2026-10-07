package com.rcl.msrpg.system.domain.model;

import java.time.Instant;
import java.util.Objects;

import com.rcl.msrpg.core.identifier.RpgSystemId;
import com.rcl.msrpg.core.valueobject.AuditTimestamps;
import com.rcl.msrpg.core.valueobject.SemanticVersion;
import com.rcl.msrpg.system.domain.enumeration.SyncPolicy;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemBehavior;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemProfile;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemVersioning;

public final class RpgSystem {

    private final RpgSystemId id;
    private RpgSystemProfile profile;
    private RpgSystemVersioning versioning;
    private SyncPolicy defaultSyncPolicy;
    private RpgSystemBehavior behavior;
    private AuditTimestamps timestamps;

    private RpgSystem(
        RpgSystemId id,
        RpgSystemProfile profile,
        RpgSystemVersioning versioning,
        SyncPolicy defaultSyncPolicy,
        RpgSystemBehavior behavior,
        AuditTimestamps timestamps
    ) {
        this.id = Objects.requireNonNull(id, "id");
        this.profile = Objects.requireNonNull(profile, "profile");
        this.versioning = Objects.requireNonNull(versioning, "versioning");
        this.defaultSyncPolicy = Objects.requireNonNull(defaultSyncPolicy, "defaultSyncPolicy");
        this.behavior = Objects.requireNonNull(behavior, "behavior");
        this.timestamps = Objects.requireNonNull(timestamps, "timestamps");
    }

    public static RpgSystem create(
        RpgSystemId id,
        RpgSystemProfile profile,
        RpgSystemVersioning versioning,
        SyncPolicy defaultSyncPolicy,
        RpgSystemBehavior behavior,
        Instant now
    ) {
        return new RpgSystem(id, profile, versioning, defaultSyncPolicy, behavior, AuditTimestamps.createdAt(now));
    }

    public static RpgSystem reconstruct(
        RpgSystemId id,
        RpgSystemProfile profile,
        RpgSystemVersioning versioning,
        SyncPolicy defaultSyncPolicy,
        RpgSystemBehavior behavior,
        AuditTimestamps timestamps
    ) {
        return new RpgSystem(id, profile, versioning, defaultSyncPolicy, behavior, timestamps);
    }

    public RpgSystemId id() {
        return id;
    }

    public RpgSystemProfile profile() {
        return profile;
    }

    public RpgSystemVersioning versioning() {
        return versioning;
    }

    public SyncPolicy defaultSyncPolicy() {
        return defaultSyncPolicy;
    }

    public RpgSystemBehavior behavior() {
        return behavior;
    }

    public AuditTimestamps timestamps() {
        return timestamps;
    }

    public void changeProfile(RpgSystemProfile newProfile, Instant now) {
        this.profile = Objects.requireNonNull(newProfile, "profile");
        touch(now);
    }

    public void changeContentVersion(SemanticVersion contentVersion, Instant now) {
        this.versioning = versioning.withContentVersion(Objects.requireNonNull(contentVersion, "contentVersion"));
        touch(now);
    }

    public void changeDefaultSyncPolicy(SyncPolicy syncPolicy, Instant now) {
        this.defaultSyncPolicy = Objects.requireNonNull(syncPolicy, "defaultSyncPolicy");
        touch(now);
    }

    public void changeBehavior(RpgSystemBehavior newBehavior, Instant now) {
        this.behavior = Objects.requireNonNull(newBehavior, "behavior");
        touch(now);
    }

    private void touch(Instant now) {
        this.timestamps = timestamps.touchedAt(now);
    }

}
