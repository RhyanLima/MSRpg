package com.rcl.msrpg.system.domain.valueobject;

import java.util.Objects;
import java.util.Optional;

import com.rcl.msrpg.core.exception.DomainValidationException;

public final class RpgSystemDescription {

    private static final int MAX_LENGTH = 2000;
    private static final RpgSystemDescription EMPTY = new RpgSystemDescription(null);

    private final String value;

    private RpgSystemDescription(String value) {
        this.value = value;
    }

    public static RpgSystemDescription empty() {
        return EMPTY;
    }

    public static RpgSystemDescription of(String raw) {
        if (raw == null || raw.isBlank()) {
            return EMPTY;
        }
        String stripped = raw.strip();
        if (stripped.length() > MAX_LENGTH) {
            throw new DomainValidationException(
                "RPG system description must have at most " + MAX_LENGTH + " characters."
            );
        }
        return new RpgSystemDescription(stripped);
    }

    public Optional<String> text() {
        return Optional.ofNullable(value);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof RpgSystemDescription that)) return false;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

}