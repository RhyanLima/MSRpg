package com.rcl.msrpg.system.domain.valueobject;

import java.util.Locale;
import java.util.Objects;

import com.rcl.msrpg.core.exception.DomainValidationException;

public final class RpgSystemName {

    private static final int MIN_LENGTH = 3;
    private static final int MAX_LENGTH = 100;

    private final String value;

    private RpgSystemName(String value) {
        this.value = value;
    }

    public static RpgSystemName of(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new DomainValidationException("RPG system name is required.");
        }
        String stripped = raw.strip();
        if (stripped.length() < MIN_LENGTH || stripped.length() > MAX_LENGTH) {
            throw new DomainValidationException(
                "RPG system name must have between " + MIN_LENGTH + " and " + MAX_LENGTH + " characters."
            );
        }
        if (stripped.chars().anyMatch(Character::isISOControl)) {
            throw new DomainValidationException("RPG system name cannot contain control characters.");
        }
        return new RpgSystemName(stripped);
    }

    public String value() {
        return value;
    }

    public boolean sameAs(RpgSystemName other) {
        return value.toLowerCase(Locale.ROOT).equals(other.value.toLowerCase(Locale.ROOT));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof RpgSystemName that)) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }

}

