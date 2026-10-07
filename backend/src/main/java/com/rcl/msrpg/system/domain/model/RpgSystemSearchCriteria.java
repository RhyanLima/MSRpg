package com.rcl.msrpg.system.domain.model;

import java.util.Optional;

import com.rcl.msrpg.core.exception.DomainValidationException;
import com.rcl.msrpg.core.valueobject.SemanticVersion;
import com.rcl.msrpg.system.domain.enumeration.SyncPolicy;

/** Filtros combináveis (AND) da listagem. Ausência de filtro = lista tudo. */
public final class RpgSystemSearchCriteria {

    private static final int MAX_NAME_TERM_LENGTH = 100;
    private static final RpgSystemSearchCriteria ANY = new RpgSystemSearchCriteria(null, null, null, null);

    private final String nameTerm;
    private final SemanticVersion engineVersion;
    private final SemanticVersion contentVersion;
    private final SyncPolicy syncPolicy;

    private RpgSystemSearchCriteria(
        String nameTerm,
        SemanticVersion engineVersion,
        SemanticVersion contentVersion,
        SyncPolicy syncPolicy
    ) {
        this.nameTerm = nameTerm;
        this.engineVersion = engineVersion;
        this.contentVersion = contentVersion;
        this.syncPolicy = syncPolicy;
    }

    public static RpgSystemSearchCriteria any() {
        return ANY;
    }

    public RpgSystemSearchCriteria withNameContaining(String term) {
        if (term == null || term.isBlank()) {
            return this;
        }
        String stripped = term.strip();
        if (stripped.length() > MAX_NAME_TERM_LENGTH) {
            throw new DomainValidationException(
                "Name filter must have at most " + MAX_NAME_TERM_LENGTH + " characters.");
        }
        return new RpgSystemSearchCriteria(stripped, engineVersion, contentVersion, syncPolicy);
    }

    public RpgSystemSearchCriteria withEngineVersion(SemanticVersion version) {
        return new RpgSystemSearchCriteria(nameTerm, version, contentVersion, syncPolicy);
    }

    public RpgSystemSearchCriteria withContentVersion(SemanticVersion version) {
        return new RpgSystemSearchCriteria(nameTerm, engineVersion, version, syncPolicy);
    }

    public RpgSystemSearchCriteria withSyncPolicy(SyncPolicy policy) {
        return new RpgSystemSearchCriteria(nameTerm, engineVersion, contentVersion, policy);
    }

    public Optional<String> nameTerm() {
        return Optional.ofNullable(nameTerm);
    }

    public Optional<SemanticVersion> engineVersion() {
        return Optional.ofNullable(engineVersion);
    }

    public Optional<SemanticVersion> contentVersion() {
        return Optional.ofNullable(contentVersion);
    }

    public Optional<SyncPolicy> syncPolicy() {
        return Optional.ofNullable(syncPolicy);
    }

}