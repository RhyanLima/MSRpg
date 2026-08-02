package com.rcl.msrpg.system.infrastructure.configuration;

import org.jdbi.v3.core.Jdbi;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rcl.msrpg.shared.infrastructure.web.WebController;
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

    public RpgSystemModule(Jdbi jdbi) {

        RpgSystemPersistenceMapper persistenceMapper = new RpgSystemPersistenceMapper(new ObjectMapper());

        RpgSystemRepositoryAdapter repository = new RpgSystemRepositoryAdapter(jdbi, persistenceMapper);

        CreateRpgSystemUseCase createUseCase = new CreateRpgSystemUseCase(repository);

        FindRpgSystemByIdUseCase findByIdUseCase = new FindRpgSystemByIdUseCase(repository);

        ListRpgSystemsUseCase listUseCase = new ListRpgSystemsUseCase(repository);

        UpdateRpgSystemUseCase updateUseCase = new UpdateRpgSystemUseCase(repository);

        DeleteRpgSystemUseCase deleteUseCase = new DeleteRpgSystemUseCase(repository);

        RpgSystemHttpMapper mapper = new RpgSystemHttpMapper();

        this.controller = new RpgSystemController(
            createUseCase,
            findByIdUseCase,
            listUseCase,
            updateUseCase,
            deleteUseCase,
            mapper
        );
    }

    public WebController controller() {
        return controller::registerRoutes;
    }

}
