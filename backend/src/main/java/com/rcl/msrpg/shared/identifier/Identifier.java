package com.rcl.msrpg.shared.identifier;

import java.util.Objects;
import java.util.UUID;

public abstract class Identifier<T extends Identifier<T>> {

    private final UUID value;


    protected Identifier(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("ID cannot be null.");
        }
        this.value = value;
    }

    public UUID value() {
        return value;
    }


    @Override
    public String toString() {
        return value.toString();
    }

    public static UUID parseUUID(String value) {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ID: " + value, e);
        }
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Identifier<?> entityId = (Identifier<?>) o;
        return Objects.equals(value, entityId.value);
    }

    @Override
    public final int hashCode() {
        return Objects.hash(value);
    }


}
