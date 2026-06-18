package com.rcl.msrpg.system.domain.port;

import java.util.Optional;

import com.rcl.msrpg.shared.identifier.RpgSystemId;
import com.rcl.msrpg.system.domain.model.RpgSystem;

public interface RpgSystemRepository {

    RpgSystem save(RpgSystem rpgSystem);

    Optional<RpgSystem> findById(RpgSystemId id);

    boolean existsById(RpgSystemId id);

    boolean existsByName(String name);

    RpgSystem update(RpgSystem rpgSystem);

    void delete(RpgSystemId id);
}
