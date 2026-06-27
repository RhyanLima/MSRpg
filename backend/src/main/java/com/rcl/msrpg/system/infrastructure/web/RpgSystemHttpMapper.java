package com.rcl.msrpg.system.infrastructure.web;

import com.rcl.msrpg.system.application.dto.CreateRpgSystemCommand;
import com.rcl.msrpg.system.application.dto.RpgSystemFilterCommand;
import com.rcl.msrpg.system.application.dto.RpgSystemResult;
import com.rcl.msrpg.system.application.dto.RpgSystemSummaryResult;
import com.rcl.msrpg.system.application.dto.UpdateRpgSystemCommand;
import com.rcl.msrpg.system.infrastructure.web.dto.CreateRpgSystemRequest;
import com.rcl.msrpg.system.infrastructure.web.dto.RpgSystemFilterRequest;
import com.rcl.msrpg.system.infrastructure.web.dto.RpgSystemResponse;
import com.rcl.msrpg.system.infrastructure.web.dto.RpgSystemSummaryResponse;
import com.rcl.msrpg.system.infrastructure.web.dto.UpdateRpgSystemRequest;

public class RpgSystemHttpMapper {

    public CreateRpgSystemCommand toCommand(CreateRpgSystemRequest request) {
        return new CreateRpgSystemCommand(
            request.name(),
            request.description(),
            request.engineVersion(),
            request.contentVersion(),
            request.defaultResolutionPolicyId(),
            request.syncPolicy(),
            request.settingsJson()
        );
    }

    public UpdateRpgSystemCommand toCommand(UpdateRpgSystemRequest request) {
        return new UpdateRpgSystemCommand(
            request.name(),
            request.description(),
            request.engineVersion(),
            request.contentVersion(),
            request.defaultResolutionPolicyId(),
            request.syncPolicy(),
            request.settingsJson()
        );
    }

    public RpgSystemFilterCommand toCommand(RpgSystemFilterRequest request) {
        if (request == null) {
            return new RpgSystemFilterCommand(null, null, null, null, null);
        }

        return new RpgSystemFilterCommand(
            request.name(),
            request.engineVersion(),
            request.contentVersion(),
            request.syncPolicy(),
            request.defaultResolutionPolicyId()
        );
    }

    public RpgSystemResponse toResponse(RpgSystemResult result) {
        return new RpgSystemResponse(
            result.id(),
            result.name(),
            result.description(),
            result.engineVersion(),
            result.contentVersion(),
            result.defaultResolutionPolicyId(),
            result.syncPolicy(),
            result.settingsJson(),
            result.createdAt(),
            result.updatedAt()
        );
    }

    public RpgSystemSummaryResponse toResponse(RpgSystemSummaryResult result) {
        return new RpgSystemSummaryResponse(
            result.id(),
            result.name(),
            result.description(),
            result.engineVersion(),
            result.contentVersion(),
            result.syncPolicy(),
            result.createdAt(),
            result.updatedAt()
        );
    }

}
