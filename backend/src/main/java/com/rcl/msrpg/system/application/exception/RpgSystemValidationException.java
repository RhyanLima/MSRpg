package com.rcl.msrpg.system.application.exception;

import com.rcl.msrpg.core.exception.api.BadRequestException;

public class RpgSystemValidationException extends BadRequestException {

    public RpgSystemValidationException(String message) {
        super(message);
    }
}
