package com.rcl.msrpg.system.domain.model;

import java.time.Instant;

import com.rcl.msrpg.shared.identifier.ResolutionPolicyId;
import com.rcl.msrpg.shared.identifier.RpgSystemId;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemSettings;

public class RpgSystem {

    private final RpgSystemId id;
    private String name;
    private String description;
    private String engineVersion;
    private String contentVersion;
    private ResolutionPolicyId defaultResolutionPolicyId;
    private SyncPolicy syncPolicy;
    private RpgSystemSettings settings;
    private final Instant createdAt;
    private Instant updatedAt;

    private RpgSystem(RpgSystemId id, String name, String description, String engineVersion, String contentVersion, ResolutionPolicyId defaultResolutionPolicyId, SyncPolicy syncPolicy, RpgSystemSettings settings, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.engineVersion = engineVersion;
        this.contentVersion = contentVersion;
        this.defaultResolutionPolicyId = defaultResolutionPolicyId;
        this.syncPolicy = syncPolicy;
        this.settings = settings;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static RpgSystem create(RpgSystemId id, String name, String description, String engineVersion, String contentVersion, ResolutionPolicyId defaultResolutionPolicyId, SyncPolicy syncPolicy, RpgSystemSettings settings) {
        return new RpgSystem(id, name, description, engineVersion, contentVersion, defaultResolutionPolicyId, syncPolicy, settings, Instant.now(), null);
    }

    public static RpgSystem reconstruct(RpgSystemId id, String name, String description, String engineVersion, String contentVersion, ResolutionPolicyId defaultResolutionPolicyId, SyncPolicy syncPolicy, RpgSystemSettings settings, Instant createdAt, Instant updatedAt) {
        return new RpgSystem(id, name, description, engineVersion, contentVersion, defaultResolutionPolicyId, syncPolicy, settings, createdAt, updatedAt);
    }

    public RpgSystemId id() {
        return id;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public String engineVersion() {
        return engineVersion;
    }

    public String contentVersion() {
        return contentVersion;
    }

    public ResolutionPolicyId defaultResolutionPolicyId() {
        return defaultResolutionPolicyId;
    }

    public SyncPolicy syncPolicy() {
        return syncPolicy;
    }

    public RpgSystemSettings settings() {
        return settings;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }

    public void updateName(String name) {
        this.name = name;
        this.updatedAt = Instant.now();
    }

    public void updateDescription(String description) {
        this.description = description;
        this.updatedAt = Instant.now();
    }

    public void updateEngineVersion(String engineVersion) {
        this.engineVersion = engineVersion;
        this.updatedAt = Instant.now();
    }

    public void updateContentVersion(String contentVersion) {
        this.contentVersion = contentVersion;
        this.updatedAt = Instant.now();
    }

    public void updateDefaultResolutionPolicyId(ResolutionPolicyId defaultResolutionPolicyId) {
        this.defaultResolutionPolicyId = defaultResolutionPolicyId;
        this.updatedAt = Instant.now();
    }

    public void updateSyncPolicy(SyncPolicy syncPolicy) {
        this.syncPolicy = syncPolicy;
        this.updatedAt = Instant.now();
    }

    public void updateSettings(RpgSystemSettings settings) {
        this.settings = settings;
        this.updatedAt = Instant.now();
    }

    public static enum SyncPolicy {
        APPLY_TO_NEW_ONLY,
        APPLY_TO_CAMPAIGN,
        APPLY_NEXT_CAMPAIGN
    }

}
