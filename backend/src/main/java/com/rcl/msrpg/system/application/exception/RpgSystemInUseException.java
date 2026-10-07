package com.rcl.msrpg.system.application.exception;

import com.rcl.msrpg.core.exception.api.ConflictException;

// WIP: Trazer componente dependente
public class RpgSystemInUseException extends ConflictException {

    public RpgSystemInUseException(String id) {
        super("RPG system " + id + " still has dependent content (definitions, campaigns or roles).");
    }
}
