package com.rcl.msrpg.system.application.exception;

import com.rcl.msrpg.shared.exception.BusinessException;

public class RpgSystemNotFoundException extends BusinessException {
    
    public RpgSystemNotFoundException(String id) {
        super("RPG system not found: " + id);
    }
}
