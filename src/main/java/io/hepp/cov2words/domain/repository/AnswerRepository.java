package io.hepp.cov2words.domain.repository;

import io.hepp.cov2words.domain.entity.AnswerEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository that is used to access words in the database.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@Repository
public interface AnswerRepository extends CrudRepository<AnswerEntity, UUID> {
    Optional<AnswerEntity> findFirstByAnswerAndDateInvalidatedIsNull(String answer);
}
