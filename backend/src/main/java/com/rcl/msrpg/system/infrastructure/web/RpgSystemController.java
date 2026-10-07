package com.rcl.msrpg.system.infrastructure.web;

import java.util.List;

import com.rcl.msrpg.system.application.exception.RpgSystemValidationException;
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
    private static final String ID_PATH = BASE_PATH + "/{id}";

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
        app.get(ID_PATH, this::findById);
        app.put(ID_PATH, this::update);
        app.delete(ID_PATH, this::delete);
    }

    // Handlers com visibilidade de pacote: testáveis com um Context mockado, sem subir o Javalin.

    void create(Context ctx) {
        CreateRpgSystemRequest request = readBody(ctx, CreateRpgSystemRequest.class);

        var result = createUseCase.execute(mapper.toCommand(request));

        ctx.status(HttpStatus.CREATED).json(mapper.toResponse(result));
    }

    void list(Context ctx) {
        var filterRequest = new RpgSystemFilterRequest(
            ctx.queryParam("name"),
            ctx.queryParam("engineVersion"),
            ctx.queryParam("contentVersion"),
            ctx.queryParam("defaultSyncPolicy")
        );

        List<RpgSystemSummaryResponse> response = listUseCase.execute(mapper.toCommand(filterRequest))
            .stream()
            .map(mapper::toResponse)
            .toList();

        ctx.status(HttpStatus.OK).json(response);
    }

    void findById(Context ctx) {
        var result = findByIdUseCase.execute(ctx.pathParam("id"));

        ctx.status(HttpStatus.OK).json(mapper.toResponse(result));
    }

    void update(Context ctx) {
        UpdateRpgSystemRequest request = readBody(ctx, UpdateRpgSystemRequest.class);

        var result = updateUseCase.execute(ctx.pathParam("id"), mapper.toCommand(request));

        ctx.status(HttpStatus.OK).json(mapper.toResponse(result));
    }

    void delete(Context ctx) {
        deleteUseCase.execute(ctx.pathParam("id"));

        ctx.status(HttpStatus.NO_CONTENT);
    }

    private static <T> T readBody(Context ctx, Class<T> type) {
        try {
            T body = ctx.bodyAsClass(type);
            if (body == null) {
                throw new RpgSystemValidationException("Request body is required.");
            }
            return body;
        } catch (RpgSystemValidationException error) {
            throw error;
        } catch (RuntimeException error) {
            throw new RpgSystemValidationException("Malformed request body.");
        }
    }

}
