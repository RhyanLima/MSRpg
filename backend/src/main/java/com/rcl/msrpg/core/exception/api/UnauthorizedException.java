package com.rcl.msrpg.core.exception.api;

public class UnauthorizedException extends ApiException {
    public UnauthorizedException(String message) {
        super(401, "UNAUTHORIZED", message);
    }

}
