package com.rcl.msrpg.system.infrastructure.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.RETURNS_SELF;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rcl.msrpg.system.application.dto.CreateRpgSystemCommand;
import com.rcl.msrpg.system.application.dto.RpgSystemFilterCommand;
import com.rcl.msrpg.system.application.dto.RpgSystemResult;
import com.rcl.msrpg.system.application.dto.RpgSystemSummaryResult;
import com.rcl.msrpg.system.application.dto.UpdateRpgSystemCommand;
import com.rcl.msrpg.system.application.exception.RpgSystemValidationException;
import com.rcl.msrpg.system.application.usecase.CreateRpgSystemUseCase;
import com.rcl.msrpg.system.application.usecase.DeleteRpgSystemUseCase;
import com.rcl.msrpg.system.application.usecase.FindRpgSystemByIdUseCase;
import com.rcl.msrpg.system.application.usecase.ListRpgSystemsUseCase;
import com.rcl.msrpg.system.application.usecase.UpdateRpgSystemUseCase;
import com.rcl.msrpg.system.infrastructure.web.dto.CreateRpgSystemRequest;
import com.rcl.msrpg.system.infrastructure.web.dto.RpgSystemResponse;
import com.rcl.msrpg.system.infrastructure.web.dto.UpdateRpgSystemRequest;

import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class RpgSystemControllerTest {

    private static final String ID = "6f1c5f0e-7a3e-4f6a-9d7a-1c2b3d4e5f60";
    private static final RpgSystemResult RESULT = new RpgSystemResult(ID, "Tormenta", null, "0.1.0", "1.0.0",
        "APPLY_TO_NEW_ONLY", "WARN_AND_SKIP_STEP", "ASK_USER", Instant.parse("2026-10-01T12:00:00Z"), null);

    @Mock private CreateRpgSystemUseCase createUseCase;
    @Mock private FindRpgSystemByIdUseCase findByIdUseCase;
    @Mock private ListRpgSystemsUseCase listUseCase;
    @Mock private UpdateRpgSystemUseCase updateUseCase;
    @Mock private DeleteRpgSystemUseCase deleteUseCase;

    private Context ctx;
    private RpgSystemController controller;

    @BeforeEach
    void setUp() {
        // RETURNS_SELF: status(...) devolve o próprio Context, permitindo o encadeamento .json(...)
        ctx = mock(Context.class, RETURNS_SELF);
        controller = new RpgSystemController(createUseCase, findByIdUseCase, listUseCase,
            updateUseCase, deleteUseCase, new RpgSystemHttpMapper());
    }

    @Test
    @DisplayName("POST responde 201 com o recurso criado")
    void createShouldReturn201() {
        var request = new CreateRpgSystemRequest("Tormenta", null, null, null, null, null);
        when(ctx.bodyAsClass(CreateRpgSystemRequest.class)).thenReturn(request);
        when(createUseCase.execute(any(CreateRpgSystemCommand.class))).thenReturn(RESULT);
        var body = ArgumentCaptor.forClass(Object.class);

        controller.create(ctx);

        verify(ctx).status(HttpStatus.CREATED);
        verify(ctx).json(body.capture());
        var response = (RpgSystemResponse) body.getValue();
        assertEquals(ID, response.id());
        assertEquals("2026-10-01T12:00:00Z", response.createdAt());
    }

    @Test
    @DisplayName("JSON malformado vira 400 genérico, sem detalhes do parser e sem chamar o caso de uso")
    void malformedBodyShouldBeBadRequest() {
        when(ctx.bodyAsClass(CreateRpgSystemRequest.class))
            .thenThrow(new IllegalArgumentException("Unrecognized field \"engineVersion\" at line 1"));

        var error = assertThrows(RpgSystemValidationException.class, () -> controller.create(ctx));

        assertEquals("Malformed request body.", error.getMessage());
        verifyNoInteractions(createUseCase);
    }

    @Test
    @DisplayName("GET lista repassa os query params como filtro")
    void listShouldForwardFilters() {
        // Strict stubs: todos os params lidos pelo controller precisam estar stubados.
        when(ctx.queryParam("name")).thenReturn("dark");
        when(ctx.queryParam("engineVersion")).thenReturn(null);
        when(ctx.queryParam("contentVersion")).thenReturn(null);
        when(ctx.queryParam("defaultSyncPolicy")).thenReturn("APPLY_TO_NEXT_SESSION");
        var filter = ArgumentCaptor.forClass(RpgSystemFilterCommand.class);
        when(listUseCase.execute(any())).thenReturn(List.of(
            new RpgSystemSummaryResult(ID, "Dark", null, "0.1.0", "1.0.0", Instant.EPOCH, null)));

        controller.list(ctx);

        verify(listUseCase).execute(filter.capture());
        assertEquals("dark", filter.getValue().name());
        assertEquals("APPLY_TO_NEXT_SESSION", filter.getValue().defaultSyncPolicy());
        verify(ctx).status(HttpStatus.OK);
    }

    @Test
    @DisplayName("PUT usa o id do path e responde 200")
    void updateShouldUsePathId() {
        var request = new UpdateRpgSystemRequest("Novo Nome", null, null, null, null, null);
        when(ctx.pathParam("id")).thenReturn(ID);
        when(ctx.bodyAsClass(UpdateRpgSystemRequest.class)).thenReturn(request);
        when(updateUseCase.execute(eq(ID), any(UpdateRpgSystemCommand.class))).thenReturn(RESULT);

        controller.update(ctx);

        verify(ctx).status(HttpStatus.OK);
    }

    @Test
    @DisplayName("GET por id responde 200")
    void findByIdShouldReturn200() {
        when(ctx.pathParam("id")).thenReturn(ID);
        when(findByIdUseCase.execute(ID)).thenReturn(RESULT);

        controller.findById(ctx);

        verify(ctx).status(HttpStatus.OK);
    }

    @Test
    @DisplayName("DELETE responde 204")
    void deleteShouldReturn204() {
        when(ctx.pathParam("id")).thenReturn(ID);

        controller.delete(ctx);

        verify(deleteUseCase).execute(ID);
        verify(ctx).status(HttpStatus.NO_CONTENT);
    }
}
