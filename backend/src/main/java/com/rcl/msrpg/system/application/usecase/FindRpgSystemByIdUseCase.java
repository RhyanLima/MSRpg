package com.rcl.msrpg.system.application.usecase;

import java.util.Objects;

import com.rcl.msrpg.core.identifier.RpgSystemId;
import com.rcl.msrpg.system.application.RpgSystemApplicationMapper;
import com.rcl.msrpg.system.application.dto.RpgSystemResult;
import com.rcl.msrpg.system.application.exception.RpgSystemNotFoundException;
import com.rcl.msrpg.system.domain.port.RpgSystemRepository;

public class FindRpgSystemByIdUseCase {

    private final RpgSystemRepository repository;

    public FindRpgSystemByIdUseCase(RpgSystemRepository repository) {
        this.repository = Objects.requireNonNull(repository, "repository");
    }

    public RpgSystemResult execute(String id) {
        RpgSystemId systemId = RpgSystemApplicationMapper.toRpgSystemId(id);

        return repository.findById(systemId)
            .map(RpgSystemApplicationMapper::toResult)
            .orElseThrow(() -> new RpgSystemNotFoundException(systemId.toString()));
    }
}
