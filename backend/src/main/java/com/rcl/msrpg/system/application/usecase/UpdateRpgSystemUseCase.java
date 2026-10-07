package com.rcl.msrpg.system.application.usecase;


import static com.rcl.msrpg.system.application.RpgSystemApplicationMapper.hasText;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

import com.rcl.msrpg.core.identifier.RpgSystemId;
import com.rcl.msrpg.core.valueobject.SemanticVersion;
import com.rcl.msrpg.system.application.RpgSystemApplicationMapper;
import com.rcl.msrpg.system.application.dto.RpgSystemResult;
import com.rcl.msrpg.system.application.dto.UpdateRpgSystemCommand;
import com.rcl.msrpg.system.application.exception.RpgSystemAlreadyExistsException;
import com.rcl.msrpg.system.application.exception.RpgSystemNotFoundException;
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

/** Atualização parcial. Cada grupo só é tocado se algo de fato mudou, então updated_at é preservado quando o comando não altera nada. */
public class UpdateRpgSystemUseCase {

    private final RpgSystemRepository repository;
    private final Clock clock;

    public UpdateRpgSystemUseCase(RpgSystemRepository repository, Clock clock) {
        this.repository = Objects.requireNonNull(repository, "repository");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public RpgSystemResult execute(String id, UpdateRpgSystemCommand command) {
        RpgSystemId systemId = RpgSystemApplicationMapper.toRpgSystemId(id);
        if (command == null) {
            throw new RpgSystemValidationException("Request cannot be null.");
        }

        RpgSystem system = repository.findById(systemId)
            .orElseThrow(() -> new RpgSystemNotFoundException(systemId.toString()));

        Instant now = clock.instant();
        boolean changed = applyProfile(system, command, now)
            | applyContentVersion(system, command, now)
            | applySyncPolicy(system, command, now)
            | applyBehavior(system, command, now);

        if (changed) {
            repository.update(system);
        }

        return RpgSystemApplicationMapper.toResult(system);
    }

    private boolean applyProfile(RpgSystem system, UpdateRpgSystemCommand command, Instant now) {
        RpgSystemProfile profile = system.profile();
        if (hasText(command.name())) {
            RpgSystemName newName = RpgSystemName.of(command.name());
            ensureNameAvailable(system, newName);
            profile = profile.renamedTo(newName);
        }
        if (command.description() != null) {
            profile = profile.describedAs(RpgSystemDescription.of(command.description()));
        }
        if (profile.equals(system.profile())) {
            return false;
        }
        system.changeProfile(profile, now);
        return true;
    }

    private void ensureNameAvailable(RpgSystem system, RpgSystemName newName) {
        if (newName.sameAs(system.profile().name())) {
            return;
        }
        if (repository.existsByNameExcept(newName, system.id())) {
            throw new RpgSystemAlreadyExistsException(newName.value());
        }
    }

    private boolean applyContentVersion(RpgSystem system, UpdateRpgSystemCommand command, Instant now) {
        if (!hasText(command.contentVersion())) {
            return false;
        }
        SemanticVersion version = SemanticVersion.of(command.contentVersion());
        if (version.equals(system.versioning().contentVersion())) {
            return false;
        }
        system.changeContentVersion(version, now);
        return true;
    }

    private boolean applySyncPolicy(RpgSystem system, UpdateRpgSystemCommand command, Instant now) {
        if (!hasText(command.defaultSyncPolicy())) {
            return false;
        }
        SyncPolicy policy = SyncPolicy.parse(command.defaultSyncPolicy());
        if (policy == system.defaultSyncPolicy()) {
            return false;
        }
        system.changeDefaultSyncPolicy(policy, now);
        return true;
    }

    private boolean applyBehavior(RpgSystem system, UpdateRpgSystemCommand command, Instant now) {
        RpgSystemBehavior behavior = system.behavior();
        if (hasText(command.missingComponentPolicy())) {
            behavior = behavior.withMissingComponentPolicy(
                MissingComponentPolicy.parse(command.missingComponentPolicy()));
        }
        if (hasText(command.conflictResolutionStrategy())) {
            behavior = behavior.withConflictResolutionStrategy(
                ConflictResolutionStrategy.parse(command.conflictResolutionStrategy()));
        }
        if (behavior.equals(system.behavior())) {
            return false;
        }
        system.changeBehavior(behavior, now);
        return true;
    }

}
