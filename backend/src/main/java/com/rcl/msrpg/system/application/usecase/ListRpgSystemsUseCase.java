package com.rcl.msrpg.system.application.usecase;

import java.util.List;

import com.rcl.msrpg.shared.identifier.ResolutionPolicyId;
import com.rcl.msrpg.system.application.RpgSystemApplicationMapper;
import com.rcl.msrpg.system.application.dto.RpgSystemFilterCommand;
import com.rcl.msrpg.system.application.dto.RpgSystemSummaryResult;
import com.rcl.msrpg.system.domain.model.RpgSystemSummary;
import com.rcl.msrpg.system.domain.model.RpgSystem.SyncPolicy;
import com.rcl.msrpg.system.domain.port.RpgSystemQueryRepository;

// Nota: Aplica um filtro por vez 
public class ListRpgSystemsUseCase {

    private final RpgSystemQueryRepository queryRepository;

    public ListRpgSystemsUseCase(RpgSystemQueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    public List<RpgSystemSummaryResult> execute(RpgSystemFilterCommand filter) {
        if (filter == null || filter.isEmpty()) {
            return map(queryRepository.findAll());
        }

        if (filter.hasName()) {
            return map(queryRepository.findByNameContaining(filter.name()));
        }

        if (filter.hasEngineVersion()) {
            return map(queryRepository.findByEngineVersion(filter.engineVersion()));
        }

        if (filter.hasContentVersion()) {
            return map(queryRepository.findByContentVersion(filter.contentVersion()));
        }

        if (filter.hasSyncPolicy()) {
            SyncPolicy syncPolicy = SyncPolicy.valueOf(filter.syncPolicy());
            return map(queryRepository.findBySyncPolicy(syncPolicy));
        }

        if (filter.hasDefaultResolutionPolicyId()) {
            ResolutionPolicyId resolutionPolicyId = ResolutionPolicyId.of(
                filter.defaultResolutionPolicyId()
            );

            return map(queryRepository.findByDefaultResolutionPolicyId(resolutionPolicyId));
        }

        return map(queryRepository.findAll());
    }

    private List<RpgSystemSummaryResult> map(List<RpgSystemSummary> systems) {
        return systems.stream()
            .map(RpgSystemApplicationMapper::toSummaryResult)
            .toList();
    }

}
