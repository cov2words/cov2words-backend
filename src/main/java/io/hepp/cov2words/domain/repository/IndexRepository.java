package io.hepp.cov2words.domain.repository;

import io.hepp.cov2words.domain.entity.IndexEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository that is used to access the word index.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@Repository
public interface IndexRepository extends JpaRepository<IndexEntity, UUID> {
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<IndexEntity> findFirstByLanguageAndDateInvalidatedIsNull(String language);
}
