package com.rcl.msrpg.system.domain.valueobject;

import java.util.Objects;

public record RpgSystemProfile(RpgSystemName name, RpgSystemDescription description) {

    public RpgSystemProfile {
        Objects.requireNonNull(name, "name");
        description = Objects.requireNonNullElse(description, RpgSystemDescription.empty());
    }

    public RpgSystemProfile renamedTo(RpgSystemName newName) {
        return new RpgSystemProfile(newName, description);
    }

    public RpgSystemProfile describedAs(RpgSystemDescription newDescription) {
        return new RpgSystemProfile(name, newDescription);
    }

}

