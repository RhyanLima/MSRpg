package com.rcl.msrpg.system.application.usecase;

import java.util.Objects;

import com.rcl.msrpg.core.identifier.RpgSystemId;
import com.rcl.msrpg.system.application.RpgSystemApplicationMapper;
import com.rcl.msrpg.system.application.exception.RpgSystemNotFoundException;
import com.rcl.msrpg.system.domain.port.RpgSystemRepository;

public class DeleteRpgSystemUseCase {

    private final RpgSystemRepository repository;

    public DeleteRpgSystemUseCase(RpgSystemRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public void execute(String id) {
        RpgSystemId systemId = RpgSystemApplicationMapper.toRpgSystemId(id);

        if (!repository.existsById(systemId)) {
            throw new RpgSystemNotFoundException(systemId.toString());
        }

        repository.delete(systemId);
    }

}
