package com.rcl.msrpg.system.application.usecase;

import java.util.List;
import java.util.Objects;

import com.rcl.msrpg.system.application.RpgSystemApplicationMapper;
import com.rcl.msrpg.system.application.dto.RpgSystemFilterCommand;
import com.rcl.msrpg.system.application.dto.RpgSystemSummaryResult;
import com.rcl.msrpg.system.domain.port.RpgSystemQueryRepository;

/** Lista resumos aplicando todos os filtros informados em conjunto (AND). */ 
public class ListRpgSystemsUseCase {

    private final RpgSystemQueryRepository queryRepository;

    public ListRpgSystemsUseCase(RpgSystemQueryRepository queryRepository) {
        this.queryRepository = Objects.requireNonNull(queryRepository, "queryRepository");
    }

    public List<RpgSystemSummaryResult> execute(RpgSystemFilterCommand filter) {
        return queryRepository.search(RpgSystemApplicationMapper.toCriteria(filter))
            .stream()
            .map(RpgSystemApplicationMapper::toSummaryResult)
            .toList();
    }

}
