package com.rcl.msrpg.system.domain.port;

import java.util.List;

import com.rcl.msrpg.system.domain.model.RpgSystemSearchCriteria;
import com.rcl.msrpg.system.domain.model.RpgSystemSummary;

public interface RpgSystemQueryRepository {

    List<RpgSystemSummary> search(RpgSystemSearchCriteria criteria);

    long count();

}
