package com.rcl.msrpg.system.application.exception;

import com.rcl.msrpg.core.exception.api.ConflictException;

public class RpgSystemAlreadyExistsException extends ConflictException {

    public RpgSystemAlreadyExistsException(String name) {
        super("RPG system already exists with name: " + name);
    }
}
