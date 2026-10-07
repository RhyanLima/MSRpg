package com.rcl.msrpg.system.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rcl.msrpg.core.engine.EngineVersionProvider;
import com.rcl.msrpg.core.exception.DomainValidationException;
import com.rcl.msrpg.core.valueobject.SemanticVersion;
import com.rcl.msrpg.system.application.dto.CreateRpgSystemCommand;
import com.rcl.msrpg.system.application.exception.RpgSystemAlreadyExistsException;
import com.rcl.msrpg.system.application.exception.RpgSystemValidationException;
import com.rcl.msrpg.system.domain.enumeration.SyncPolicy;
import com.rcl.msrpg.system.domain.model.RpgSystem;
import com.rcl.msrpg.system.domain.port.RpgSystemRepository;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemName;
import com.rcl.msrpg.system.support.RpgSystemFixtures;

@ExtendWith(MockitoExtension.class)
class CreateRpgSystemUseCaseTest {

    private static final SemanticVersion SERVER_ENGINE = SemanticVersion.of("0.4.2");

    @Mock
    private RpgSystemRepository repository;

    @Mock
    private EngineVersionProvider engineVersion;

    private CreateRpgSystemUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateRpgSystemUseCase(repository, engineVersion,
            Clock.fixed(RpgSystemFixtures.T0, ZoneOffset.UTC));
    }

    private static CreateRpgSystemCommand minimal(String name) {
        return new CreateRpgSystemCommand(name, null, null, null, null, null);
    }

    @Test
    @DisplayName("Cria com defaults do banco e carimba a versão do engine do servidor")
    void shouldCreateWithDefaultsAndServerEngineVersion() {
        when(repository.existsByName(any(RpgSystemName.class))).thenReturn(false);
        when(engineVersion.current()).thenReturn(SERVER_ENGINE);

        var result = useCase.execute(minimal("Tormenta"));

        assertEquals("0.4.2", result.engineVersion());
        assertEquals("1.0.0", result.contentVersion());
        assertEquals("APPLY_TO_NEW_ONLY", result.defaultSyncPolicy());
        assertEquals("WARN_AND_SKIP_STEP", result.missingComponentPolicy());
        assertEquals("ASK_USER", result.conflictResolutionStrategy());
        assertEquals(RpgSystemFixtures.T0, result.createdAt());
        assertNull(result.updatedAt());
        assertNull(result.description());
    }

    @Test
    @DisplayName("Persiste exatamente o aggregate devolvido, com escolhas do usuário")
    void shouldPersistAggregateWithUserChoices() {
        when(repository.existsByName(any(RpgSystemName.class))).thenReturn(false);
        when(engineVersion.current()).thenReturn(SERVER_ENGINE);
        var captor = ArgumentCaptor.forClass(RpgSystem.class);

        var result = useCase.execute(new CreateRpgSystemCommand(
            "Tormenta", "Sombrio", "2.0.0", "APPLY_TO_NEXT_SESSION", "FAIL_EVENT", "OVERWRITE"));

        verify(repository).add(captor.capture());
        RpgSystem saved = captor.getValue();
        assertEquals(result.id(), saved.id().toString());
        assertEquals(SyncPolicy.APPLY_TO_NEXT_SESSION, saved.defaultSyncPolicy());
        assertEquals(SemanticVersion.of("2.0.0"), saved.versioning().contentVersion());
        assertEquals(SERVER_ENGINE, saved.versioning().engineVersion());
        assertEquals("FAIL_EVENT", result.missingComponentPolicy());
        assertEquals("OVERWRITE", result.conflictResolutionStrategy());
    }

    @Test
    @DisplayName("Nome duplicado gera conflito e nada é persistido")
    void shouldRejectDuplicatedName() {
        when(repository.existsByName(any(RpgSystemName.class))).thenReturn(true);

        assertThrows(RpgSystemAlreadyExistsException.class, () -> useCase.execute(minimal("Tormenta")));
        verify(repository, never()).add(any());
    }

    @Test
    @DisplayName("Entradas inválidas são rejeitadas antes de consultar o repositório")
    void shouldRejectInvalidInput() {
        assertThrows(RpgSystemValidationException.class, () -> useCase.execute(null));
        assertThrows(DomainValidationException.class, () -> useCase.execute(minimal("ab")));

        verifyNoInteractions(repository, engineVersion);
    }

    @Test
    @DisplayName("Versão de conteúdo ou enum inválidos falham antes de qualquer I/O")
    void shouldRejectInvalidOptionalFields() {
        assertThrows(DomainValidationException.class, () -> useCase.execute(
            new CreateRpgSystemCommand("Tormenta", null, "1.0", null, null, null)));
        assertThrows(DomainValidationException.class, () -> useCase.execute(
            new CreateRpgSystemCommand("Tormenta", null, null, "APPLY_TO_CAMPAIGN", null, null)));

        verifyNoInteractions(repository, engineVersion);
    }
}
