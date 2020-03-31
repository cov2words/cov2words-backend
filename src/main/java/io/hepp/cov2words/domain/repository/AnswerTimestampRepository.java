package io.hepp.cov2words.domain.repository;

import io.hepp.cov2words.domain.entity.AnswerTimestampMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @author Thomas Hepp, thomas@hepp.io
 */
@Repository
public interface AnswerTimestampRepository extends JpaRepository<AnswerTimestampMapping, UUID> {

}
