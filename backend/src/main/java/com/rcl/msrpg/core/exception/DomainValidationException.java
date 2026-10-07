package com.rcl.msrpg.core.exception;

/** Violação de invariante de domínio (Value Object ou Aggregate inválido). */
public class DomainValidationException extends RuntimeException {

    public DomainValidationException(String message) {
        super(message);
    }

}
