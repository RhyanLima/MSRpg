package com.rcl.msrpg.system.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rcl.msrpg.core.identifier.RpgSystemId;
import com.rcl.msrpg.system.application.exception.RpgSystemNotFoundException;
import com.rcl.msrpg.system.application.exception.RpgSystemValidationException;
import com.rcl.msrpg.system.domain.port.RpgSystemRepository;
import com.rcl.msrpg.system.support.RpgSystemFixtures;

@ExtendWith(MockitoExtension.class)
class FindRpgSystemByIdUseCaseTest {

    @Mock
    private RpgSystemRepository repository;

    @InjectMocks
    private FindRpgSystemByIdUseCase useCase;

    @Test
    @DisplayName("Retorna o detalhe completo")
    void shouldReturnDetail() {
        var system = RpgSystemFixtures.system("Tormenta");
        when(repository.findById(system.id())).thenReturn(Optional.of(system));

        var result = useCase.execute(system.id().toString());

        assertEquals("Tormenta", result.name());
        assertEquals("0.1.0", result.engineVersion());
    }

    @Test
    @DisplayName("ID em branco ou malformado vira 400 sem consultar o repositório")
    void shouldValidateIdFormat() {
        assertThrows(RpgSystemValidationException.class, () -> useCase.execute(" "));
        assertThrows(RpgSystemValidationException.class, () -> useCase.execute("123"));
        verifyNoInteractions(repository);
    }

    @Test
    @DisplayName("Inexistente vira 404")
    void shouldReportNotFound() {
        String missing = "00000000-0000-0000-0000-000000000000";
        when(repository.findById(RpgSystemId.of(missing))).thenReturn(Optional.empty());

        assertThrows(RpgSystemNotFoundException.class, () -> useCase.execute(missing));
    }
}
