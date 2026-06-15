package com.rcl.msrpg.system.domain.port;

import com.rcl.msrpg.shared.identifier.RpgSystemId;
import com.rcl.msrpg.system.domain.model.RpgSystem;

public interface RpgSystemRepository {

    public RpgSystem create(RpgSystem rpgSystem);

    public RpgSystem findById(RpgSystemId id);

    public RpgSystem update(RpgSystem rpgSystem);

    public void delete(RpgSystemId id);
}
