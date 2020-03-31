package io.hepp.cov2words.domain.repository;

import io.hepp.cov2words.domain.entity.TimestampEntity;
import org.joda.time.DateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository that is used to create timestamps.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@Repository
public interface TimestampRepository extends JpaRepository<TimestampEntity, UUID> {
    Page<TimestampEntity> findAllByDateInvalidatedIsNullAndStatusLessThanAndDateModifiedLessThan(
            int status,
            DateTime dateModified,
            Pageable pageable
    );

    Optional<TimestampEntity> findFirstByHashAndDateInvalidatedIsNull(String hash);
}
