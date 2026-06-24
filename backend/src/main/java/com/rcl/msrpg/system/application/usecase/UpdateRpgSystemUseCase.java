package com.rcl.msrpg.system.application.usecase;

import com.rcl.msrpg.shared.identifier.RpgSystemId;
import com.rcl.msrpg.system.application.RpgSystemApplicationMapper;
import com.rcl.msrpg.system.application.dto.RpgSystemResult;
import com.rcl.msrpg.system.application.dto.UpdateRpgSystemCommand;
import com.rcl.msrpg.system.application.exception.RpgSystemNotFoundException;
import com.rcl.msrpg.system.application.exception.RpgSystemValidationException;
import com.rcl.msrpg.system.domain.model.RpgSystem;
import com.rcl.msrpg.system.domain.port.RpgSystemRepository;

public class UpdateRpgSystemUseCase {


    private final RpgSystemRepository repository;

    public UpdateRpgSystemUseCase(RpgSystemRepository repository) {
        this.repository = repository;
    }

    public RpgSystemResult execute(String id, UpdateRpgSystemCommand command) {
        if (id == null || id.isBlank()) {
            throw new RpgSystemValidationException("RPG system id is required.");
        }

        if (command == null) {
            throw new RpgSystemValidationException("Request cannot be null.");
        }

        RpgSystem rpgSystem = repository.findById(RpgSystemId.of(id))
            .orElseThrow(() -> new RpgSystemNotFoundException(id));

        applyChanges(rpgSystem, command);

        repository.save(rpgSystem);

        return RpgSystemApplicationMapper.toResult(rpgSystem);
    }

    private void applyChanges(RpgSystem rpgSystem, UpdateRpgSystemCommand command) {
        if (hasText(command.name())) {
            rpgSystem.updateName(command.name());
        }

        if (command.description() != null) {
            rpgSystem.updateDescription(command.description());
        }

        if (hasText(command.engineVersion())) {
            rpgSystem.updateEngineVersion(command.engineVersion());
        }

        if (hasText(command.contentVersion())) {
            rpgSystem.updateContentVersion(command.contentVersion());
        }

        if (hasText(command.defaultResolutionPolicyId())) {
            rpgSystem.updateDefaultResolutionPolicyId(
                RpgSystemApplicationMapper.toResolutionPolicyId(command.defaultResolutionPolicyId())
            );
        }

        if (hasText(command.syncPolicy())) {
            rpgSystem.updateSyncPolicy(
                RpgSystemApplicationMapper.toSyncPolicy(command.syncPolicy())
            );
        }

        if (hasText(command.settingsJson())) {
            rpgSystem.updateSettings(
                RpgSystemApplicationMapper.toSettings(command.settingsJson())
            );
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

}
