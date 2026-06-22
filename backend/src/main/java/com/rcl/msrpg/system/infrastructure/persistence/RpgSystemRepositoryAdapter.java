package com.rcl.msrpg.system.infrastructure.persistence;

import java.util.Optional;

import org.jdbi.v3.core.Jdbi;

import com.rcl.msrpg.shared.identifier.RpgSystemId;
import com.rcl.msrpg.system.domain.model.RpgSystem;
import com.rcl.msrpg.system.domain.port.RpgSystemRepository;

public class RpgSystemRepositoryAdapter implements RpgSystemRepository {

    private final Jdbi jdbi;
    private final RpgSystemPersistenceMapper mapper;

    public RpgSystemRepositoryAdapter(Jdbi jdbi, RpgSystemPersistenceMapper mapper) {
        this.jdbi = jdbi;
        this.mapper = mapper;
    }

    @Override
    public void save(RpgSystem rpgSystem) {
        var entity = mapper.toEntity(rpgSystem);

        jdbi.useTransaction(handle -> {
            var jdbiRepository = handle.attach(JdbiRpgSystemRepository.class);

            if (jdbiRepository.existsById(entity.id())) {
                int updatedRows = jdbiRepository.update(entity);

                if (updatedRows != 1) {
                    throw new IllegalStateException(
                        "Failed to update RPG system: " + entity.id()
                    );
                }
                return;
            }
            jdbiRepository.save(entity);
        });
    }

    @Override
    public Optional<RpgSystem> findById(RpgSystemId id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findById'");
    }

    @Override
    public boolean existsById(RpgSystemId id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'existsById'");
    }

    @Override
    public boolean existsByName(String name) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'existsByName'");
    }

    @Override
    public RpgSystem update(RpgSystem rpgSystem) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'update'");
    }

    @Override
    public void delete(RpgSystemId id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

}
