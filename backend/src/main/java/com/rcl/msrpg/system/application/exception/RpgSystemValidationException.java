package com.rcl.msrpg.system.application.exception;

import com.rcl.msrpg.shared.exception.BusinessException;

public class RpgSystemValidationException extends BusinessException {

    public RpgSystemValidationException(String message) {
        super(message);
    }
}
