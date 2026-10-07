package com.rcl.msrpg.core.exception.api;

public class ConflictException extends ApiException {
    public ConflictException(String message) {
        super(409, "CONFLICT", message);
    }

}
