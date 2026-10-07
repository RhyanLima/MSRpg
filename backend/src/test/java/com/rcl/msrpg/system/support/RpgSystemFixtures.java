package com.rcl.msrpg.system.support;

import java.time.Instant;

import com.rcl.msrpg.core.identifier.RpgSystemId;
import com.rcl.msrpg.core.valueobject.SemanticVersion;
import com.rcl.msrpg.system.domain.enumeration.SyncPolicy;
import com.rcl.msrpg.system.domain.model.RpgSystem;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemBehavior;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemDescription;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemName;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemProfile;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemVersioning;

public final class RpgSystemFixtures {

    public static final Instant T0 = Instant.parse("2026-10-01T12:00:00Z");
    public static final SemanticVersion ENGINE = SemanticVersion.of("0.1.0");

    private RpgSystemFixtures() {}

    public static RpgSystem system(String name) {
        return system(name, T0);
    }

    public static RpgSystem system(String name, Instant createdAt) {
        return RpgSystem.create(
            RpgSystemId.generate(),
            new RpgSystemProfile(RpgSystemName.of(name), RpgSystemDescription.of("Descrição de " + name)),
            new RpgSystemVersioning(ENGINE, SemanticVersion.initialContent()),
            SyncPolicy.defaultPolicy(),
            RpgSystemBehavior.defaults(),
            createdAt
        );
    }
}
