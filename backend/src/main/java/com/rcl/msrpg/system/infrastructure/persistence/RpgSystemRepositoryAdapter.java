package com.rcl.msrpg.system.infrastructure.persistence;

import java.util.List;
import java.util.Optional;

import org.jdbi.v3.core.Jdbi;

import com.rcl.msrpg.shared.identifier.ResolutionPolicyId;
import com.rcl.msrpg.shared.identifier.RpgSystemId;
import com.rcl.msrpg.system.domain.model.RpgSystem;
import com.rcl.msrpg.system.domain.model.RpgSystem.SyncPolicy;
import com.rcl.msrpg.system.domain.model.RpgSystemSummary;
import com.rcl.msrpg.system.domain.port.RpgSystemQueryRepository;
import com.rcl.msrpg.system.domain.port.RpgSystemRepository;

public class RpgSystemRepositoryAdapter implements RpgSystemRepository, RpgSystemQueryRepository {

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
        return jdbi.withExtension(JdbiRpgSystemRepository.class, jdbiRepository -> 
            jdbiRepository.findById(id.toString()).map(mapper::toDomain)
        );
    }

    @Override
    public boolean existsById(RpgSystemId id) {
        return jdbi.withExtension(JdbiRpgSystemRepository.class, jdbiRepository -> jdbiRepository.existsById(id.toString()));
    }

    @Override
    public boolean existsByName(String name) {
        return jdbi.withExtension(JdbiRpgSystemRepository.class, jdbiRepository -> jdbiRepository.existsById(name));
    }

    @Override
    public void delete(RpgSystemId id) {
        jdbi.useExtension(JdbiRpgSystemRepository.class, jdbiRepository -> jdbiRepository.deleteById(id.toString()));
    }

    @Override
    public List<RpgSystemSummary> findAll() {
        return jdbi.withExtension(JdbiRpgSystemRepository.class, jdbiRepository -> 
            jdbiRepository.findAll()
            .stream()
            .map(mapper::toSummary)
            .toList()
        );
    }

    @Override
    public List<RpgSystemSummary> findByNameContaining(String term) {
        return jdbi.withExtension(JdbiRpgSystemRepository.class, jdbiRepository -> 
            jdbiRepository.findByNameContaining(term)
            .stream()
            .map(mapper::toSummary)
            .toList()
        );
    }

    @Override
    public List<RpgSystemSummary> findByEngineVersion(String engineVersion) {
        return jdbi.withExtension(JdbiRpgSystemRepository.class, jdbiRepository -> 
            jdbiRepository.findByEngineVersion(engineVersion)
            .stream()
            .map(mapper::toSummary)
            .toList()
        );
    }

    @Override
    public List<RpgSystemSummary> findByContentVersion(String contentVersion) {
        return jdbi.withExtension(JdbiRpgSystemRepository.class, jdbiRepository -> 
            jdbiRepository.findByContentVersion(contentVersion)
            .stream()
            .map(mapper::toSummary)
            .toList()
        );
    }

    @Override
    public List<RpgSystemSummary> findBySyncPolicy(SyncPolicy syncPolicy) {
        return jdbi.withExtension(JdbiRpgSystemRepository.class, jdbiRepository -> 
            jdbiRepository.findBySyncPolicy(syncPolicy.name())
            .stream()
            .map(mapper::toSummary)
            .toList()
        );
    }

    @Override
    public List<RpgSystemSummary> findByDefaultResolutionPolicyId(ResolutionPolicyId resolutionPolicyId) {
        return jdbi.withExtension(JdbiRpgSystemRepository.class, jdbiRepository -> 
            jdbiRepository.findByDefaultResolutionPolicyId(resolutionPolicyId.toString())
            .stream()
            .map(mapper::toSummary)
            .toList()
        );
    }

    @Override
    public long count() {
        return jdbi.withExtension(JdbiRpgSystemRepository.class, jdbiRepository -> jdbiRepository.count());
    }

}
