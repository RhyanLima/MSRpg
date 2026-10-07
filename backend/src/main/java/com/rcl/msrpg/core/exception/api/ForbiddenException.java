package com.rcl.msrpg.core.exception.api;

public class ForbiddenException extends ApiException {
    public ForbiddenException(String message) {
        super(403, "FORBIDDEN", message);
    }

}
