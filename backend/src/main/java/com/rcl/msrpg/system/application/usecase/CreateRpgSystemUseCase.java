package com.rcl.msrpg.system.application.usecase;

import static com.rcl.msrpg.system.application.RpgSystemApplicationMapper.hasText;

import java.time.Clock;
import java.util.Objects;

import com.rcl.msrpg.core.engine.EngineVersionProvider;
import com.rcl.msrpg.core.identifier.RpgSystemId;
import com.rcl.msrpg.core.valueobject.SemanticVersion;
import com.rcl.msrpg.system.application.RpgSystemApplicationMapper;
import com.rcl.msrpg.system.application.dto.CreateRpgSystemCommand;
import com.rcl.msrpg.system.application.dto.RpgSystemResult;
import com.rcl.msrpg.system.application.exception.RpgSystemAlreadyExistsException;
import com.rcl.msrpg.system.application.exception.RpgSystemValidationException;
import com.rcl.msrpg.system.domain.enumeration.ConflictResolutionStrategy;
import com.rcl.msrpg.system.domain.enumeration.MissingComponentPolicy;
import com.rcl.msrpg.system.domain.enumeration.SyncPolicy;
import com.rcl.msrpg.system.domain.model.RpgSystem;
import com.rcl.msrpg.system.domain.port.RpgSystemRepository;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemBehavior;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemDescription;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemName;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemProfile;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemVersioning;

public class CreateRpgSystemUseCase {

    private final RpgSystemRepository repository;
    private final EngineVersionProvider engineVersion;
    private final Clock clock;

    public CreateRpgSystemUseCase(RpgSystemRepository repository, EngineVersionProvider engineVersion, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.engineVersion = Objects.requireNonNull(engineVersion, "engineVersion");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RpgSystemResult execute(CreateRpgSystemCommand command) {
        if (command == null) {
            throw new RpgSystemValidationException("Command cannot be null.");
        }

        RpgSystemName name = RpgSystemName.of(command.name());
        RpgSystemProfile profile = new RpgSystemProfile(name, RpgSystemDescription.of(command.description()));
        SemanticVersion contentVersion = toContentVersion(command);
        SyncPolicy syncPolicy = toSyncPolicy(command);
        RpgSystemBehavior behavior = toBehavior(command);

        if (repository.existsByName(name)) {
            throw new RpgSystemAlreadyExistsException(name.value());
        }

        RpgSystem system = RpgSystem.create(
            RpgSystemId.generate(),
            profile,
            new RpgSystemVersioning(engineVersion.current(), contentVersion),
            syncPolicy,
            behavior,
            clock.instant()
        );

        repository.add(system);

        return RpgSystemApplicationMapper.toResult(system);
    }

    private SemanticVersion toContentVersion(CreateRpgSystemCommand command) {
        return hasText(command.contentVersion())
            ? SemanticVersion.of(command.contentVersion())
            : SemanticVersion.initialContent();
    }

    private SyncPolicy toSyncPolicy(CreateRpgSystemCommand command) {
        return hasText(command.defaultSyncPolicy())
            ? SyncPolicy.parse(command.defaultSyncPolicy())
            : SyncPolicy.defaultPolicy();
    }

    private RpgSystemBehavior toBehavior(CreateRpgSystemCommand command) {
        RpgSystemBehavior behavior = RpgSystemBehavior.defaults();
        if (hasText(command.missingComponentPolicy())) {
            behavior = behavior.withMissingComponentPolicy(
                MissingComponentPolicy.parse(command.missingComponentPolicy()));
        }
        if (hasText(command.conflictResolutionStrategy())) {
            behavior = behavior.withConflictResolutionStrategy(
                ConflictResolutionStrategy.parse(command.conflictResolutionStrategy()));
        }
        return behavior;
    }

}
