package com.rcl.msrpg.system.application;

import com.rcl.msrpg.shared.identifier.ResolutionPolicyId;
import com.rcl.msrpg.shared.identifier.RpgSystemId;
import com.rcl.msrpg.system.application.dto.CreateRpgSystemCommand;
import com.rcl.msrpg.system.application.dto.RpgSystemResult;
import com.rcl.msrpg.system.application.dto.RpgSystemSummaryResult;
import com.rcl.msrpg.system.domain.model.RpgSystem;
import com.rcl.msrpg.system.domain.model.RpgSystem.SyncPolicy;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemSettings;

public class RpgSystemApplicationMapper {

    private RpgSystemApplicationMapper() {}

    public static RpgSystem toDomain(CreateRpgSystemCommand request, RpgSystemId id) {
        return RpgSystem.create(
            id,
            request.name(),
            request.description(),
            request.engineVersion(),
            request.contentVersion(),
            toResolutionPolicyId(request.defaultResolutionPolicyId()),
            toSyncPolicy(request.syncPolicy()),
            toSettings(request.settingsJson())
        );
    }

    public static RpgSystemResult toResult(RpgSystem rpgSystem) {
        return new RpgSystemResult(
            rpgSystem.id().toString(),
            rpgSystem.name(),
            rpgSystem.description(),
            rpgSystem.engineVersion(),
            rpgSystem.contentVersion(),
            rpgSystem.defaultResolutionPolicyId().toString(),
            rpgSystem.syncPolicy().name(),
            rpgSystem.settings().toJson(),
            rpgSystem.createdAt(),
            rpgSystem.updatedAt()
        );
    }

    public static RpgSystemSummaryResult toSummaryResult(RpgSystem rpgSystem) {
        return new RpgSystemSummaryResult(
            rpgSystem.id().toString(),
            rpgSystem.name(),
            rpgSystem.description(),
            rpgSystem.engineVersion(),
            rpgSystem.contentVersion(),
            rpgSystem.syncPolicy().name(),
            rpgSystem.createdAt(),
            rpgSystem.updatedAt()
        );
    }

    public static ResolutionPolicyId toResolutionPolicyId(String value) {
        return ResolutionPolicyId.of(value);
    }

    public static SyncPolicy toSyncPolicy(String value) {
        return SyncPolicy.valueOf(value);
    }

    public static RpgSystemSettings toSettings(String settingsJson) {
        return RpgSystemSettings.fromJson(settingsJson);
    }


}
