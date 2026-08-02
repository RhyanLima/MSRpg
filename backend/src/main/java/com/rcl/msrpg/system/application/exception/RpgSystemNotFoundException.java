package com.rcl.msrpg.system.application.exception;

import com.rcl.msrpg.shared.exception.api.NotFoundException;

public class RpgSystemNotFoundException extends NotFoundException {
    
    public RpgSystemNotFoundException(String id) {
        super("RPG system not found: " + id);
    }
}
