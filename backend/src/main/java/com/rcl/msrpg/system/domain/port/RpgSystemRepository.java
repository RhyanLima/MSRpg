package com.rcl.msrpg.system.domain.port;

import java.util.Optional;

import com.rcl.msrpg.core.identifier.RpgSystemId;
import com.rcl.msrpg.system.domain.model.RpgSystem;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemName;

public interface RpgSystemRepository {

    void add(RpgSystem system);

    void update(RpgSystem system);

    Optional<RpgSystem> findById(RpgSystemId id);

    boolean existsById(RpgSystemId id);

    /** Comparação case-insensitive. */
    boolean existsByName(RpgSystemName name);

    /** Comparação case-insensitive ignorando o próprio sistema (rename). */
    boolean existsByNameExcept(RpgSystemName name, RpgSystemId exceptId);

    /**
     * Remove o sistema. Deve falhar com {@code RpgSystemInUseException} se
     * houver qualquer conteúdo dependente (políticas, definitions, campanhas,
     * roles...) — o sistema não apaga conteúdo do usuário em cascata.
     */
    void delete(RpgSystemId id);

    // Em Planejamento
    // void deleteOnCascade(RpgSystemId id);
}
