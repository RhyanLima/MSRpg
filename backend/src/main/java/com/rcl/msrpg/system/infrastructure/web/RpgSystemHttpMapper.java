package com.rcl.msrpg.system.infrastructure.web;

import java.time.Instant;

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
            request.contentVersion(),
            request.defaultSyncPolicy(),
            request.missingComponentPolicy(),
            request.conflictResolutionStrategy()
        );
    }

    public UpdateRpgSystemCommand toCommand(UpdateRpgSystemRequest request) {
        return new UpdateRpgSystemCommand(
            request.name(),
            request.description(),
            request.contentVersion(),
            request.defaultSyncPolicy(),
            request.missingComponentPolicy(),
            request.conflictResolutionStrategy()
        );
    }

    public RpgSystemFilterCommand toCommand(RpgSystemFilterRequest request) {
        if (request == null) {
            return RpgSystemFilterCommand.none();
        }
        return new RpgSystemFilterCommand(
            request.name(),
            request.engineVersion(),
            request.contentVersion(),
            request.defaultSyncPolicy()
        );
    }

    public RpgSystemResponse toResponse(RpgSystemResult result) {
        return new RpgSystemResponse(
            result.id(),
            result.name(),
            result.description(),
            result.engineVersion(),
            result.contentVersion(),
            result.defaultSyncPolicy(),
            result.missingComponentPolicy(),
            result.conflictResolutionStrategy(),
            iso(result.createdAt()),
            iso(result.updatedAt())
        );
    }

    public RpgSystemSummaryResponse toResponse(RpgSystemSummaryResult result) {
        return new RpgSystemSummaryResponse(
            result.id(),
            result.name(),
            result.description(),
            result.engineVersion(),
            result.contentVersion(),
            iso(result.createdAt()),
            iso(result.updatedAt())
        );
    }

    private static String iso(Instant instant) {
        return instant == null ? null : instant.toString();
    }

}
