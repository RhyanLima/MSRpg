package com.rcl.msrpg.system.infrastructure.persistence;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.jdbi.v3.core.Jdbi;

import com.rcl.msrpg.core.identifier.RpgSystemId;
import com.rcl.msrpg.core.valueobject.SemanticVersion;
import com.rcl.msrpg.system.application.exception.RpgSystemInUseException;
import com.rcl.msrpg.system.domain.enumeration.SyncPolicy;
import com.rcl.msrpg.system.domain.model.RpgSystem;
import com.rcl.msrpg.system.domain.model.RpgSystemSearchCriteria;
import com.rcl.msrpg.system.domain.model.RpgSystemSummary;
import com.rcl.msrpg.system.domain.port.RpgSystemQueryRepository;
import com.rcl.msrpg.system.domain.port.RpgSystemRepository;
import com.rcl.msrpg.system.domain.valueobject.RpgSystemName;

public class RpgSystemRepositoryAdapter implements RpgSystemRepository, RpgSystemQueryRepository {

    private final Jdbi jdbi;
    private final RpgSystemPersistenceMapper mapper;

    public RpgSystemRepositoryAdapter(Jdbi jdbi, RpgSystemPersistenceMapper mapper) {
        this.jdbi = Objects.requireNonNull(jdbi, "jdbi");
        this.mapper = Objects.requireNonNull(mapper, "mapper");
    }

    @Override
    public void add(RpgSystem system) {
        RpgSystemEntity entity = mapper.toEntity(system);
        jdbi.useExtension(JdbiRpgSystemRepository.class, dao -> dao.insert(entity));
    }

    @Override
    public void update(RpgSystem system) {
        RpgSystemEntity entity = mapper.toEntity(system);
        int updatedRows = jdbi.withExtension(JdbiRpgSystemRepository.class, dao -> dao.update(entity));
        if (updatedRows != 1) {
            throw new IllegalStateException("Failed to update RPG system: " + entity.id());
        }
    }

    @Override
    public Optional<RpgSystem> findById(RpgSystemId id) {
        return jdbi.withExtension(JdbiRpgSystemRepository.class, dao ->
            dao.findById(id.toString()).map(mapper::toDomain)
        );
    }

    @Override
    public boolean existsById(RpgSystemId id) {
        return jdbi.withExtension(JdbiRpgSystemRepository.class, dao -> dao.existsById(id.toString()));
    }

    @Override
    public boolean existsByName(RpgSystemName name) {
        return jdbi.withExtension(JdbiRpgSystemRepository.class, dao -> dao.existsByName(name.value()));
    }

    @Override
    public boolean existsByNameExcept(RpgSystemName name, RpgSystemId exceptId) {
        return jdbi.withExtension(JdbiRpgSystemRepository.class, dao ->
            dao.existsByNameExcept(name.value(), exceptId.toString())
        );
    }

    @Override
    public void delete(RpgSystemId id) {
        String systemId = id.toString();
        try {
            jdbi.useExtension(JdbiRpgSystemRepository.class, dao -> dao.deleteById(systemId));
        } catch (RuntimeException error) {
            if (SqliteConstraintViolations.isForeignKeyViolation(error)) {
                throw new RpgSystemInUseException(systemId);
            }
            throw error;
        }
    }

    @Override
    public List<RpgSystemSummary> search(RpgSystemSearchCriteria criteria) {
        String nameTerm = criteria.nameTerm().map(SqlLikeEscaper::escape).orElse(null);
        String engineVersion = criteria.engineVersion().map(SemanticVersion::value).orElse(null);
        String contentVersion = criteria.contentVersion().map(SemanticVersion::value).orElse(null);
        String syncPolicy = criteria.syncPolicy().map(SyncPolicy::name).orElse(null);

        return jdbi.withExtension(JdbiRpgSystemRepository.class, dao ->
            dao.search(nameTerm, engineVersion, contentVersion, syncPolicy)
                .stream()
                .map(mapper::toSummary)
                .toList()
        );
    }

    @Override
    public long count() {
        return jdbi.withExtension(JdbiRpgSystemRepository.class, JdbiRpgSystemRepository::count);
    }

}
