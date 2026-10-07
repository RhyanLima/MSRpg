package com.rcl.msrpg.system.infrastructure.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.rcl.msrpg.system.application.dto.RpgSystemResult;
import com.rcl.msrpg.system.infrastructure.web.dto.CreateRpgSystemRequest;
import com.rcl.msrpg.system.infrastructure.web.dto.RpgSystemFilterRequest;
import com.rcl.msrpg.system.infrastructure.web.dto.UpdateRpgSystemRequest;

class RpgSystemHttpMapperTest {

    private final RpgSystemHttpMapper mapper = new RpgSystemHttpMapper();

    @Test
    @DisplayName("Create request mapeia campo a campo")
    void createRequestToCommand() {
        var command = mapper.toCommand(new CreateRpgSystemRequest(
            "Tormenta", "desc", "1.0.0", "APPLY_TO_NEW_ONLY", "FAIL_EVENT", "SKIP"));

        assertEquals("Tormenta", command.name());
        assertEquals("1.0.0", command.contentVersion());
        assertEquals("APPLY_TO_NEW_ONLY", command.defaultSyncPolicy());
        assertEquals("FAIL_EVENT", command.missingComponentPolicy());
        assertEquals("SKIP", command.conflictResolutionStrategy());
    }

    @Test
    @DisplayName("Update request mapeia campo a campo")
    void updateRequestToCommand() {
        var command = mapper.toCommand(new UpdateRpgSystemRequest(
            "Nome", "", "2.0.0", null, null, "OVERWRITE"));

        assertEquals("Nome", command.name());
        assertEquals("", command.description());
        assertEquals("2.0.0", command.contentVersion());
        assertEquals("OVERWRITE", command.conflictResolutionStrategy());
    }

    @Test
    @DisplayName("Filtro nulo vira filtro vazio")
    void nullFilterIsNone() {
        var filter = mapper.toCommand((RpgSystemFilterRequest) null);

        assertNull(filter.name());
        assertNull(filter.defaultSyncPolicy());
    }

    @Test
    @DisplayName("Response serializa timestamps em ISO-8601 e mantém updatedAt nulo")
    void responseShouldUseIsoTimestamps() {
        var result = new RpgSystemResult("id", "Tormenta", null, "0.1.0", "1.0.0",
            "APPLY_TO_NEW_ONLY", "WARN_AND_SKIP_STEP", "ASK_USER",
            Instant.parse("2026-10-01T12:00:00Z"), null);

        var response = mapper.toResponse(result);

        assertEquals("2026-10-01T12:00:00Z", response.createdAt());
        assertNull(response.updatedAt());
        assertEquals("0.1.0", response.engineVersion());
    }
}
