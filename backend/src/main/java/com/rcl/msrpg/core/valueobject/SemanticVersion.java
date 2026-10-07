package com.rcl.msrpg.core.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

import com.rcl.msrpg.core.exception.DomainValidationException;

/**
 * Versão explícita MAJOR.MINOR.PATCH (convenção do projeto para versões
 * definidas pelo usuário/engine).
 */
public final class SemanticVersion {

    private static final Pattern FORMAT = Pattern.compile("^\\d{1,9}\\.\\d{1,9}\\.\\d{1,9}$");
    private static final SemanticVersion INITIAL_CONTENT = new SemanticVersion("1.0.0");

    private final String value;

    private SemanticVersion(String value) {
        this.value = value;
    }

    public static SemanticVersion of(String raw) {
        if (raw == null || !FORMAT.matcher(raw.strip()).matches()) {
            throw new DomainValidationException("Version must follow MAJOR.MINOR.PATCH (digits only).");
        }
        return new SemanticVersion(raw.strip());
    }

    /** Valor padrão de content_version no banco. */
    public static SemanticVersion initialContent() {
        return INITIAL_CONTENT;
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof SemanticVersion that)) return false;
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
