package com.rcl.msrpg.system.domain.port;

import java.util.Optional;

import com.rcl.msrpg.core.identifier.RpgSystemId;
import com.rcl.msrpg.system.domain.model.RpgSystem;

public interface RpgSystemRepository {

    void save(RpgSystem rpgSystem);

    Optional<RpgSystem> findById(RpgSystemId id);

    boolean existsById(RpgSystemId id);

    boolean existsByName(String name);

    void delete(RpgSystemId id);
}
