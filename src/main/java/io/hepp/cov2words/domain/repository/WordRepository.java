package io.hepp.cov2words.domain.repository;

import io.hepp.cov2words.domain.entity.WordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository that is used to access words in the database.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@Repository
public interface WordRepository extends JpaRepository<WordEntity, UUID> {
    /**
     * Returns all words for a certain language.
     */
    List<WordEntity> findAllByLanguageAndDateInvalidatedIsNull(String language);

    /**
     * Deletes all words for a certain language.
     */
    void deleteAllByLanguage(String language);
}
