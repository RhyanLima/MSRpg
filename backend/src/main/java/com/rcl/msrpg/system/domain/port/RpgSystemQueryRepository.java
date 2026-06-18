package com.rcl.msrpg.system.domain.port;

import java.util.List;

import com.rcl.msrpg.shared.identifier.ResolutionPolicyId;
import com.rcl.msrpg.system.domain.model.RpgSystem.SyncPolicy;
import com.rcl.msrpg.system.domain.model.RpgSystemSummary;

public interface RpgSystemQueryRepository {

    List<RpgSystemSummary> findAll();

    List<RpgSystemSummary> findByNameContaining(String term);

    List<RpgSystemSummary> findByEngineVersion(String engineVersion);

    List<RpgSystemSummary> findByContentVersion(String contentVersion);

    List<RpgSystemSummary> findBySyncPolicy(SyncPolicy syncPolicy);

    List<RpgSystemSummary> findByDefaultResolutionPolicyId(ResolutionPolicyId resolutionPolicyId);

    long count();

}
