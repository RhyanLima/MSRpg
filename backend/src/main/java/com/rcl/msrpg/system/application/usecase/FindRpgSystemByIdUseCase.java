package com.rcl.msrpg.system.application.usecase;

import com.rcl.msrpg.shared.identifier.RpgSystemId;
import com.rcl.msrpg.system.application.RpgSystemApplicationMapper;
import com.rcl.msrpg.system.application.dto.RpgSystemResult;
import com.rcl.msrpg.system.application.exception.RpgSystemNotFoundException;
import com.rcl.msrpg.system.application.exception.RpgSystemValidationException;
import com.rcl.msrpg.system.domain.model.RpgSystem;
import com.rcl.msrpg.system.domain.port.RpgSystemRepository;

public class FindRpgSystemByIdUseCase {

    private final RpgSystemRepository repository;

    public FindRpgSystemByIdUseCase(RpgSystemRepository repository) {
        this.repository = repository;
    }

    public RpgSystemResult execute(String id) {
        if (id == null || id.isBlank()) {
            throw new RpgSystemValidationException("RPG system id is required.");
        }

        RpgSystem rpgSystem = repository.findById(RpgSystemId.of(id))
            .orElseThrow(() -> new RpgSystemNotFoundException(id));

        return RpgSystemApplicationMapper.toResult(rpgSystem);
    }
}
