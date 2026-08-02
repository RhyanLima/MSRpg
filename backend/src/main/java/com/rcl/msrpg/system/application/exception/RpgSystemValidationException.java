package com.rcl.msrpg.system.application.exception;

import com.rcl.msrpg.shared.exception.api.BadRequestException;

public class RpgSystemValidationException extends BadRequestException {

    public RpgSystemValidationException(String message) {
        super(message);
    }
}
