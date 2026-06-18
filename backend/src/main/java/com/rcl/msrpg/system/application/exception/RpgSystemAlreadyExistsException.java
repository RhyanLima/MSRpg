package com.rcl.msrpg.system.application.exception;

import com.rcl.msrpg.shared.exception.BusinessException;

public class RpgSystemAlreadyExistsException extends BusinessException {

    public RpgSystemAlreadyExistsException(String name) {
        super("RPG system already exists with name: " + name);
    }
}
