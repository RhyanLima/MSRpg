package com.rcl.msrpg.system.application.usecase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.rcl.msrpg.core.exception.DomainValidationException;
import com.rcl.msrpg.core.valueobject.SemanticVersion;
import com.rcl.msrpg.system.application.dto.RpgSystemFilterCommand;
import com.rcl.msrpg.system.domain.enumeration.SyncPolicy;
import com.rcl.msrpg.system.domain.model.RpgSystemSearchCriteria;
import com.rcl.msrpg.system.domain.model.RpgSystemSummary;
import com.rcl.msrpg.system.domain.port.RpgSystemQueryRepository;
import com.rcl.msrpg.system.support.RpgSystemFixtures;

@ExtendWith(MockitoExtension.class)
class ListRpgSystemsUseCaseTest {

    @Mock
    private RpgSystemQueryRepository queryRepository;

    @InjectMocks
    private ListRpgSystemsUseCase useCase;

    @Test
    @DisplayName("Filtro nulo busca sem critérios e mapeia os resumos")
    void nullFilterSearchesEverything() {
        var system = RpgSystemFixtures.system("Tormenta");
        var summary = new RpgSystemSummary(system.id(), system.profile(), system.versioning(), system.timestamps());
        var captor = ArgumentCaptor.forClass(RpgSystemSearchCriteria.class);
        when(queryRepository.search(any())).thenReturn(List.of(summary));

        var results = useCase.execute(null);

        verify(queryRepository).search(captor.capture());
        assertTrue(captor.getValue().nameTerm().isEmpty());
        assertEquals(1, results.size());
        assertEquals("Tormenta", results.get(0).name());
    }

    @Test
    @DisplayName("Todos os filtros informados chegam combinados ao repositório")
    void filtersShouldBeCombined() {
        var captor = ArgumentCaptor.forClass(RpgSystemSearchCriteria.class);
        when(queryRepository.search(any())).thenReturn(List.of());

        useCase.execute(new RpgSystemFilterCommand(" dark ", "0.1.0", "2.0.0", "APPLY_TO_NEXT_SESSION"));

        verify(queryRepository).search(captor.capture());
        var criteria = captor.getValue();
        assertEquals("dark", criteria.nameTerm().orElseThrow());
        assertEquals(SemanticVersion.of("0.1.0"), criteria.engineVersion().orElseThrow());
        assertEquals(SemanticVersion.of("2.0.0"), criteria.contentVersion().orElseThrow());
        assertEquals(SyncPolicy.APPLY_TO_NEXT_SESSION, criteria.syncPolicy().orElseThrow());
    }

    @Test
    @DisplayName("Filtro inválido é rejeitado antes da consulta")
    void invalidFilterShouldFailFast() {
        assertThrows(DomainValidationException.class,
            () -> useCase.execute(new RpgSystemFilterCommand(null, "x.y", null, null)));
        verifyNoInteractions(queryRepository);
    }
}
