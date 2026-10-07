package com.rcl.msrpg.core.valueobject;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import com.rcl.msrpg.core.exception.DomainValidationException;

/** Par created_at / updated_at. Imutável: cada alteração gera nova instância. */
public record AuditTimestamps(Instant createdAt, Instant updatedAt) {

    public AuditTimestamps {
        Objects.requireNonNull(createdAt, "createdAt");
        if (updatedAt != null && updatedAt.isBefore(createdAt)) {
            throw new DomainValidationException("updatedAt cannot be before createdAt.");
        }
    }

    public static AuditTimestamps createdAt(Instant now) {
        return new AuditTimestamps(now, null);
    }

    public AuditTimestamps touchedAt(Instant now) {
        return new AuditTimestamps(createdAt, now);
    }

    public Optional<Instant> lastUpdate() {
        return Optional.ofNullable(updatedAt);
    }

}
