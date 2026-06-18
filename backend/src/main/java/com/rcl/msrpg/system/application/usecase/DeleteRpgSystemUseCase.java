package com.rcl.msrpg.system.application.usecase;

import com.rcl.msrpg.shared.identifier.RpgSystemId;
import com.rcl.msrpg.system.application.exception.RpgSystemNotFoundException;
import com.rcl.msrpg.system.application.exception.RpgSystemValidationException;
import com.rcl.msrpg.system.domain.port.RpgSystemRepository;

public class DeleteRpgSystemUseCase {


    private final RpgSystemRepository repository;

    public DeleteRpgSystemUseCase(RpgSystemRepository repository) {
        this.repository = repository;
    }

    public void execute(String id) {
        if (id == null || id.isBlank()) {
            throw new RpgSystemValidationException("RPG system id is required.");
        }

        RpgSystemId rpgSystemId = RpgSystemId.of(id);

        if (!repository.existsById(rpgSystemId)) {
            throw new RpgSystemNotFoundException(id);
        }

        repository.delete(rpgSystemId);
    }

}
