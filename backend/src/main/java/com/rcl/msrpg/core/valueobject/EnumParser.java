package com.rcl.msrpg.core.valueobject;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import com.rcl.msrpg.core.exception.DomainValidationException;

public final class EnumParser {

    private EnumParser() {}

    public static <E extends Enum<E>> E parse(Class<E> type, String raw, String fieldName) {
        if (raw == null || raw.isBlank()) {
            throw new DomainValidationException(fieldName + " is required.");
        }
        String normalized = raw.strip().toUpperCase(Locale.ROOT);
        return Arrays.stream(type.getEnumConstants())
            .filter(constant -> constant.name().equals(normalized))
            .findFirst()
            .orElseThrow(() -> new DomainValidationException(
                fieldName + " must be one of: " + allowedValues(type) + "."
            ));
    }

    private static <E extends Enum<E>> String allowedValues(Class<E> type) {
        return Arrays.stream(type.getEnumConstants())
            .map(Enum::name)
            .collect(Collectors.joining(", "));
    }

}
