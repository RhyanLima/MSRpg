package com.rcl.msrpg.system.application.usecase;

import java.util.UUID;

import com.rcl.msrpg.shared.identifier.RpgSystemId;
import com.rcl.msrpg.system.application.RpgSystemApplicationMapper;
import com.rcl.msrpg.system.application.dto.CreateRpgSystemCommand;
import com.rcl.msrpg.system.application.dto.RpgSystemResult;
import com.rcl.msrpg.system.application.exception.RpgSystemAlreadyExistsException;
import com.rcl.msrpg.system.application.exception.RpgSystemValidationException;
import com.rcl.msrpg.system.domain.model.RpgSystem;
import com.rcl.msrpg.system.domain.port.RpgSystemRepository;

public class CreateRpgSystemUseCase {

    private final RpgSystemRepository repository;

    public CreateRpgSystemUseCase(RpgSystemRepository repository) {
        this.repository = repository;
    }

    public RpgSystemResult execute(CreateRpgSystemCommand command) {
        validate(command);

        if (repository.existsByName(command.name())) {
            throw new RpgSystemAlreadyExistsException(command.name());
        }

        RpgSystemId id = RpgSystemId.generate();

        RpgSystem rpgSystem = RpgSystemApplicationMapper.toDomain(command, id);

        repository.save(rpgSystem);

        return RpgSystemApplicationMapper.toResult(rpgSystem);
    }

    private void validate(CreateRpgSystemCommand command) {
        if (command == null) {
            throw new RpgSystemValidationException("Command cannot be null.");
        }

        if (isBlank(command.name())) {
            throw new RpgSystemValidationException("RPG system name is required.");
        }

        if (isBlank(command.engineVersion())) {
            throw new RpgSystemValidationException("Engine version is required.");
        }

        if (isBlank(command.contentVersion())) {
            throw new RpgSystemValidationException("Content version is required.");
        }

        if (isBlank(command.defaultResolutionPolicyId())) {
            throw new RpgSystemValidationException("Default resolution policy id is required.");
        }

        if (isBlank(command.syncPolicy())) {
            throw new RpgSystemValidationException("Sync policy is required.");
        }

        if (isBlank(command.settingsJson())) {
            throw new RpgSystemValidationException("Settings JSON is required.");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

}
