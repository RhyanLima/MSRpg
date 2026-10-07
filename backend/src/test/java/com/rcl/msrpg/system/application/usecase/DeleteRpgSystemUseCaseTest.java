package com.rcl.msrpg.system.application.usecase;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rcl.msrpg.core.identifier.RpgSystemId;
import com.rcl.msrpg.system.application.exception.RpgSystemInUseException;
import com.rcl.msrpg.system.application.exception.RpgSystemNotFoundException;
import com.rcl.msrpg.system.application.exception.RpgSystemValidationException;
import com.rcl.msrpg.system.domain.port.RpgSystemRepository;

@ExtendWith(MockitoExtension.class)
class DeleteRpgSystemUseCaseTest {

    private static final String ID = "6f1c5f0e-7a3e-4f6a-9d7a-1c2b3d4e5f60";

    @Mock
    private RpgSystemRepository repository;

    @InjectMocks
    private DeleteRpgSystemUseCase useCase;

    @Test
    @DisplayName("Remove sistema existente")
    void shouldDelete() {
        when(repository.existsById(RpgSystemId.of(ID))).thenReturn(true);

        useCase.execute(ID);

        verify(repository).delete(RpgSystemId.of(ID));
    }

    @Test
    @DisplayName("Inexistente vira 404 sem tentar remover")
    void missingShouldBeNotFound() {
        when(repository.existsById(RpgSystemId.of(ID))).thenReturn(false);

        assertThrows(RpgSystemNotFoundException.class, () -> useCase.execute(ID));
        verify(repository, never()).delete(any());
    }

    @Test
    @DisplayName("Sistema com conteúdo dependente propaga 409")
    void inUseShouldPropagateConflict() {
        when(repository.existsById(RpgSystemId.of(ID))).thenReturn(true);
        doThrow(new RpgSystemInUseException(ID)).when(repository).delete(RpgSystemId.of(ID));

        assertThrows(RpgSystemInUseException.class, () -> useCase.execute(ID));
    }

    @Test
    @DisplayName("ID malformado vira 400")
    void malformedIdShouldBeBadRequest() {
        assertThrows(RpgSystemValidationException.class, () -> useCase.execute("../etc/passwd"));
        verifyNoInteractions(repository);
    }
}
