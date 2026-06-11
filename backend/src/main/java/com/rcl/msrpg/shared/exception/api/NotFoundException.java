package com.rcl.msrpg.shared.exception.api;

public class NotFoundException extends ApiException {
    public NotFoundException(String message) {
        super(404, "NOT_FOUND", message);
    }

}
