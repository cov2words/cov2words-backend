package io.hepp.cov2words.domain.repository;

import io.hepp.cov2words.domain.entity.IndexEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository that is used to access the word index.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@Repository
public interface IndexRepository extends CrudRepository<IndexEntity, UUID> {
    Optional<IndexEntity> findFirstByLanguageAndDateInvalidatedIsNull(String language);
}
