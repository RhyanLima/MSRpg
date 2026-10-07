package com.rcl.msrpg.system.infrastructure.configuration;

import java.time.Clock;

import org.jdbi.v3.core.Jdbi;

import com.rcl.msrpg.core.engine.EngineVersionProvider;
import com.rcl.msrpg.core.infrastructure.web.WebController;
import com.rcl.msrpg.system.application.usecase.CreateRpgSystemUseCase;
import com.rcl.msrpg.system.application.usecase.DeleteRpgSystemUseCase;
import com.rcl.msrpg.system.application.usecase.FindRpgSystemByIdUseCase;
import com.rcl.msrpg.system.application.usecase.ListRpgSystemsUseCase;
import com.rcl.msrpg.system.application.usecase.UpdateRpgSystemUseCase;
import com.rcl.msrpg.system.infrastructure.persistence.RpgSystemPersistenceMapper;
import com.rcl.msrpg.system.infrastructure.persistence.RpgSystemRepositoryAdapter;
import com.rcl.msrpg.system.infrastructure.web.RpgSystemController;
import com.rcl.msrpg.system.infrastructure.web.RpgSystemHttpMapper;

public class RpgSystemModule {

    private final RpgSystemController controller;

    public RpgSystemModule(Jdbi jdbi, EngineVersionProvider engineVersionProvider, Clock clock) {
        var repository = new RpgSystemRepositoryAdapter(jdbi, new RpgSystemPersistenceMapper());

        this.controller = new RpgSystemController(
            new CreateRpgSystemUseCase(repository, engineVersionProvider, clock),
            new FindRpgSystemByIdUseCase(repository),
            new ListRpgSystemsUseCase(repository),
            new UpdateRpgSystemUseCase(repository, clock),
            new DeleteRpgSystemUseCase(repository),
            new RpgSystemHttpMapper()
        );
    }

    public WebController controller() {
        return controller::registerRoutes;
    }

    public WebController controller() {
        return controller::registerRoutes;
    }

}
