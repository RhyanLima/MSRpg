package com.rcl.msrpg.shared.identifier;

import java.util.Objects;
import java.util.UUID;

public abstract class Identifier<T extends Identifier<T>> {

    private final UUID value;
    private final Type type;


    protected Identifier(UUID value, Type type) {
        if (value == null) {
            throw new IllegalArgumentException("ID cannot be null.");
        }
        if (type == null) {
            throw new IllegalArgumentException("IdentifierType cannot be null.");
        }

        this.value = value;
        this.type = type;
    }

    public UUID getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

    public String asCompositeKey() {
        return type.name() + ":" + value;
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

    // Centralizar os tipos dos Ids aqui
    protected enum Type {
        RPG_SYSTEM,
        RESOLUTION_POLICY
    }

}
