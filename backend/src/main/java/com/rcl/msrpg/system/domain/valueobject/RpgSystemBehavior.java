package com.rcl.msrpg.system.domain.valueobject;

import java.util.Objects;

import com.rcl.msrpg.system.domain.enumeration.ConflictResolutionStrategy;
import com.rcl.msrpg.system.domain.enumeration.MissingComponentPolicy;

public record RpgSystemBehavior(MissingComponentPolicy missingComponentPolicy, ConflictResolutionStrategy conflictResolutionStrategy) {

    public RpgSystemBehavior {
        missingComponentPolicy = Objects.requireNonNullElse(missingComponentPolicy, MissingComponentPolicy.defaultPolicy());
        conflictResolutionStrategy = Objects.requireNonNullElse(conflictResolutionStrategy, ConflictResolutionStrategy.defaultStrategy());
    }

    public static RpgSystemBehavior defaults() {
        return new RpgSystemBehavior(null, null);
    }

    public RpgSystemBehavior withMissingComponentPolicy(MissingComponentPolicy policy) {
        return new RpgSystemBehavior(policy, conflictResolutionStrategy);
    }

    public RpgSystemBehavior withConflictResolutionStrategy(ConflictResolutionStrategy strategy) {
        return new RpgSystemBehavior(missingComponentPolicy, strategy);
    }

}
