package io.hepp.cov2words.domain.repository;

import io.hepp.cov2words.domain.entity.AnswerWordMappingEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository that is used to access words in the database.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@Repository
public interface AnswerWordRepository extends CrudRepository<AnswerWordMappingEntity, UUID> {
    /**
     * Returns the mapping for a certain answer and language.
     */
    List<AnswerWordMappingEntity> findAllByWord_LanguageAndAnswerEntity_AnswerAndDateInvalidatedIsNullOrderByOrder(
            String language,
            String answer
    );

    List<AnswerWordMappingEntity> findAllByWord_LanguageAndDateInvalidatedIsNullAndWord_WordInOrderByOrder(
            String language,
            String[] words
    );
}
