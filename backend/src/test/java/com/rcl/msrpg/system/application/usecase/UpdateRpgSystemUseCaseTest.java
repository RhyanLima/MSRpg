package com.rcl.msrpg.system.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rcl.msrpg.core.identifier.RpgSystemId;
import com.rcl.msrpg.system.application.dto.UpdateRpgSystemCommand;
import com.rcl.msrpg.system.application.exception.RpgSystemAlreadyExistsException;
import com.rcl.msrpg.system.application.exception.RpgSystemNotFoundException;
import com.rcl.msrpg.system.application.exception.RpgSystemValidationException;
import com.rcl.msrpg.system.domain.model.RpgSystem;
import com.rcl.msrpg.system.domain.port.RpgSystemRepository;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemName;
import com.rcl.msrpg.system.support.RpgSystemFixtures;

@ExtendWith(MockitoExtension.class)
class UpdateRpgSystemUseCaseTest {

    private static final Instant NOW = RpgSystemFixtures.T0.plusSeconds(3600);

    @Mock
    private RpgSystemRepository repository;

    private UpdateRpgSystemUseCase useCase;
    private RpgSystem existing;
    private String existingId;

    @BeforeEach
    void setUp() {
        useCase = new UpdateRpgSystemUseCase(repository, Clock.fixed(NOW, ZoneOffset.UTC));
        existing = RpgSystemFixtures.system("Tormenta");
        existingId = existing.id().toString();
    }

    private void givenExisting() {
        when(repository.findById(existing.id())).thenReturn(Optional.of(existing));
    }

    @Test
    @DisplayName("Comando sem mudanças não persiste nem toca updatedAt")
    void emptyCommandShouldNotPersist() {
        givenExisting();

        var result = useCase.execute(existingId, UpdateRpgSystemCommand.empty());

        assertNull(result.updatedAt());
        verify(repository, never()).update(any());
    }

    @Test
    @DisplayName("Valores iguais aos atuais contam como 'sem mudança'")
    void sameValuesShouldNotPersist() {
        givenExisting();

        useCase.execute(existingId, new UpdateRpgSystemCommand(
            null, null, "1.0.0", "APPLY_TO_NEW_ONLY", "WARN_AND_SKIP_STEP", "ASK_USER"));

        verify(repository, never()).update(any());
    }

    @Test
    @DisplayName("Atualiza campos informados, registra updatedAt e persiste uma vez")
    void shouldUpdateProvidedFields() {
        givenExisting();
        when(repository.existsByNameExcept(any(RpgSystemName.class), eq(existing.id()))).thenReturn(false);

        var result = useCase.execute(existingId, new UpdateRpgSystemCommand(
            "Tormenta 20", "", "1.2.0", "APPLY_TO_NEXT_CAMPAIGN", "IGNORE_SILENTLY", null));

        assertEquals("Tormenta 20", result.name());
        assertNull(result.description());
        assertEquals("1.2.0", result.contentVersion());
        assertEquals("0.1.0", result.engineVersion());
        assertEquals("APPLY_TO_NEXT_CAMPAIGN", result.defaultSyncPolicy());
        assertEquals("IGNORE_SILENTLY", result.missingComponentPolicy());
        assertEquals("ASK_USER", result.conflictResolutionStrategy());
        assertEquals(NOW, result.updatedAt());
        verify(repository).update(existing);
    }

    @Test
    @DisplayName("Mudar só a caixa do nome não consulta unicidade")
    void renameToSameNameDifferentCaseSkipsUniquenessCheck() {
        givenExisting();

        var result = useCase.execute(existingId,
            new UpdateRpgSystemCommand("TORMENTA", null, null, null, null, null));

        assertEquals("TORMENTA", result.name());
        verify(repository, never()).existsByNameExcept(any(), any());
    }

    @Test
    @DisplayName("Renomear para nome de outro sistema gera conflito")
    void renameToTakenNameShouldConflict() {
        givenExisting();
        when(repository.existsByNameExcept(any(RpgSystemName.class), eq(existing.id()))).thenReturn(true);

        assertThrows(RpgSystemAlreadyExistsException.class, () -> useCase.execute(existingId,
            new UpdateRpgSystemCommand("Dark Fantasy", null, null, null, null, null)));
        verify(repository, never()).update(any());
    }

    @Test
    @DisplayName("ID malformado vira 400 sem tocar o repositório; inexistente vira 404")
    void shouldValidateId() {
        assertThrows(RpgSystemValidationException.class,
            () -> useCase.execute("not-a-uuid", UpdateRpgSystemCommand.empty()));
        verifyNoInteractions(repository);

        String missing = "00000000-0000-0000-0000-000000000000";
        when(repository.findById(RpgSystemId.of(missing))).thenReturn(Optional.empty());
        assertThrows(RpgSystemNotFoundException.class,
            () -> useCase.execute(missing, UpdateRpgSystemCommand.empty()));
    }
}
