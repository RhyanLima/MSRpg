package com.rcl.msrpg.system.domain.valueobject;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public record RpgSystemSettings(RuntimeSettings runtime, SnapshotSettings snapshots, LogSettings logs, ImportExportSettings importExport) {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public RpgSystemSettings {
        runtime = Objects.requireNonNullElse(runtime, RuntimeSettings.defaults());
        snapshots = Objects.requireNonNullElse(snapshots, SnapshotSettings.defaults());
        logs = Objects.requireNonNullElse(logs, LogSettings.defaults());
        importExport = Objects.requireNonNullElse(importExport, ImportExportSettings.defaults());
    }

    public static RpgSystemSettings defaults() {
        return new RpgSystemSettings(
            RuntimeSettings.defaults(),
            SnapshotSettings.defaults(),
            LogSettings.defaults(),
            ImportExportSettings.defaults()
        );
    }

    /* Json Parsers */

    public static RpgSystemSettings fromJson(String json) {
        if (json == null || json.isBlank()) {
            return RpgSystemSettings.defaults();
        }

        try {
            return OBJECT_MAPPER.readValue(json, RpgSystemSettings.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Invalid JSON to RpgSystemSettings.", exception);
        }
    }

    @JsonIgnore
    public String toJson() {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize RpgSystemSettings.", exception);
        }
    }

    /* Records Internos */

    public record RuntimeSettings(
        ModifierCommitStrategy modifierCommitStrategy,
        CycleDetectionSettings cycleDetection,
        MissingComponentPolicy missingComponentPolicy
    ) {

        public RuntimeSettings {
            modifierCommitStrategy = Objects.requireNonNullElse(
                modifierCommitStrategy,
                ModifierCommitStrategy.BATCHED
            );

            cycleDetection = Objects.requireNonNullElse(
                cycleDetection,
                CycleDetectionSettings.defaults()
            );

            missingComponentPolicy = Objects.requireNonNullElse(
                missingComponentPolicy,
                MissingComponentPolicy.WARN_AND_SKIP_STEP
            );
        }

        public static RuntimeSettings defaults() {
            return new RuntimeSettings(
                ModifierCommitStrategy.BATCHED,
                CycleDetectionSettings.defaults(),
                MissingComponentPolicy.WARN_AND_SKIP_STEP
            );
        }
    }

    public record CycleDetectionSettings(
        boolean enabled,
        int maxDepth,
        CycleLimitBehavior onLimitReached
    ) {

        public CycleDetectionSettings {
            if (maxDepth < 1) {
                throw new IllegalArgumentException("maxDepth must be greater than zero.");
            }

            onLimitReached = Objects.requireNonNullElse(
                onLimitReached,
                CycleLimitBehavior.ABORT_AND_WARN
            );
        }

        public static CycleDetectionSettings defaults() {
            return new CycleDetectionSettings(
                true,
                20,
                CycleLimitBehavior.ABORT_AND_WARN
            );
        }
    }

    public record SnapshotSettings(
        boolean manualSnapshotsEnabled,
        boolean automaticSnapshotsEnabled,
        SnapshotFrequency automaticFrequency,
        int maxSnapshotsToKeep
    ) {

        public SnapshotSettings {
            automaticFrequency = Objects.requireNonNullElse(
                automaticFrequency,
                SnapshotFrequency.EVERY_SESSION_END
            );

            if (maxSnapshotsToKeep < 0) {
                throw new IllegalArgumentException("maxSnapshotsToKeep cannot be nagative.");
            }
        }

        public static SnapshotSettings defaults() {
            return new SnapshotSettings(
                true,
                true,
                SnapshotFrequency.EVERY_SESSION_END,
                10
            );
        }
    }

    public record LogSettings(
        SessionLogLevel minimumSessionLogLevel,
        int maxSessionLogsToKeep
    ) {

        public LogSettings {
            minimumSessionLogLevel = Objects.requireNonNullElse(
                minimumSessionLogLevel,
                SessionLogLevel.INFO
            );

            if (maxSessionLogsToKeep < 0) {
                throw new IllegalArgumentException("maxSessionLogsToKeep cannot be nagative.");
            }
        }

        public static LogSettings defaults() {
            return new LogSettings(
                SessionLogLevel.INFO,
                1000
            );
        }
    }

    public record ImportExportSettings(
        ConflictResolutionStrategy defaultConflictResolutionStrategy,
        boolean includeDependenciesByDefault
    ) {

        public ImportExportSettings {
            defaultConflictResolutionStrategy = Objects.requireNonNullElse(
                defaultConflictResolutionStrategy,
                ConflictResolutionStrategy.ASK_USER
            );
        }

        public static ImportExportSettings defaults() {
            return new ImportExportSettings(
                ConflictResolutionStrategy.ASK_USER,
                true
            );
        }
    }

    /* ENUMs Internos */

    public enum ModifierCommitStrategy {
        BATCHED,
        IMMEDIATE
    }

    public enum MissingComponentPolicy {
        WARN_AND_SKIP_STEP,
        FAIL_EVENT,
        IGNORE_SILENTLY
    }

    public enum CycleLimitBehavior {
        ABORT_AND_WARN,
        ABORT_AND_FAIL
    }

    public enum SnapshotFrequency {
        DISABLED,
        EVERY_TURN_END,
        EVERY_COMBAT_END,
        EVERY_SESSION_END
    }

    public enum SessionLogLevel {
        DEBUG,
        INFO,
        WARN,
        ERROR
    }

    public enum ConflictResolutionStrategy {
        ASK_USER,
        SKIP,
        OVERWRITE,
        CREATE_COPY
    }
}