package com.rcl.msrpg.system.infrastructure.web;

import java.util.List;

import com.rcl.msrpg.system.application.usecase.CreateRpgSystemUseCase;
import com.rcl.msrpg.system.application.usecase.DeleteRpgSystemUseCase;
import com.rcl.msrpg.system.application.usecase.FindRpgSystemByIdUseCase;
import com.rcl.msrpg.system.application.usecase.ListRpgSystemsUseCase;
import com.rcl.msrpg.system.application.usecase.UpdateRpgSystemUseCase;
import com.rcl.msrpg.system.infrastructure.web.dto.CreateRpgSystemRequest;
import com.rcl.msrpg.system.infrastructure.web.dto.RpgSystemFilterRequest;
import com.rcl.msrpg.system.infrastructure.web.dto.RpgSystemSummaryResponse;
import com.rcl.msrpg.system.infrastructure.web.dto.UpdateRpgSystemRequest;

import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class RpgSystemController {

    private static final String BASE_PATH = "/api/v1/rpg-systems";

    private final CreateRpgSystemUseCase createUseCase;
    private final FindRpgSystemByIdUseCase findByIdUseCase;
    private final ListRpgSystemsUseCase listUseCase;
    private final UpdateRpgSystemUseCase updateUseCase;
    private final DeleteRpgSystemUseCase deleteUseCase;
    private final RpgSystemHttpMapper mapper;

    public RpgSystemController(
        CreateRpgSystemUseCase createUseCase,
        FindRpgSystemByIdUseCase findByIdUseCase,
        ListRpgSystemsUseCase listUseCase,
        UpdateRpgSystemUseCase updateUseCase,
        DeleteRpgSystemUseCase deleteUseCase,
        RpgSystemHttpMapper mapper
    ) {
        this.createUseCase = createUseCase;
        this.findByIdUseCase = findByIdUseCase;
        this.listUseCase = listUseCase;
        this.updateUseCase = updateUseCase;
        this.deleteUseCase = deleteUseCase;
        this.mapper = mapper;
    }

    public void registerRoutes(Javalin app) {
        app.post(BASE_PATH, this::create);
        app.get(BASE_PATH, this::list);
        app.get(BASE_PATH + "/{id}", this::findById);
        app.put(BASE_PATH + "/{id}", this::update);
        app.delete(BASE_PATH + "/{id}", this::delete);
    }

    private void create(Context ctx) {
        CreateRpgSystemRequest request = ctx.bodyAsClass(CreateRpgSystemRequest.class);

        var command = mapper.toCommand(request);
        var result = createUseCase.execute(command);
        var response = mapper.toResponse(result);

        ctx.status(HttpStatus.CREATED).json(response);
    }

    private void list(Context ctx) {
        var filterRequest = new RpgSystemFilterRequest(
            ctx.queryParam("name"),
            ctx.queryParam("engineVersion"),
            ctx.queryParam("contentVersion"),
            ctx.queryParam("syncPolicy"),
            ctx.queryParam("defaultResolutionPolicyId")
        );

        var filter = mapper.toCommand(filterRequest);

        List<RpgSystemSummaryResponse> response = listUseCase.execute(filter)
            .stream()
            .map(mapper::toResponse)
            .toList();

        ctx.status(HttpStatus.OK).json(response);
    }

    private void findById(Context ctx) {
        String id = ctx.pathParam("id");

        var result = findByIdUseCase.execute(id);
        var response = mapper.toResponse(result);

        ctx.status(HttpStatus.OK).json(response);
    }

    private void update(Context ctx) {
        String id = ctx.pathParam("id");
        UpdateRpgSystemRequest request = ctx.bodyAsClass(UpdateRpgSystemRequest.class);

        var command = mapper.toCommand(request);
        var result = updateUseCase.execute(id, command);
        var response = mapper.toResponse(result);

        ctx.status(HttpStatus.OK).json(response);
    }

    private void delete(Context ctx) {
        String id = ctx.pathParam("id");

        deleteUseCase.execute(id);

        ctx.status(HttpStatus.NO_CONTENT);
    }

}
