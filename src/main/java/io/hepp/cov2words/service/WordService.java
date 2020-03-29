package io.hepp.cov2words.service;

import io.hepp.cov2words.common.dto.WordResponseDTO;
import io.hepp.cov2words.common.exceptions.language.UnknownLanguageException;
import io.hepp.cov2words.domain.entity.WordEntity;
import io.hepp.cov2words.domain.repository.WordRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service that manages the word lists in the database.
 *
 * @author Thomas Hepp, thomas@hepp.iov
 */
@Service
@Slf4j(topic = "WordService")
public class WordService {

    private final WordRepository wordRepository;
    private final LanguageService languageService;
    private final int combinations;

    @Autowired
    public WordService(
            WordRepository wordRepository,
            LanguageService languageService,
            @Value("${cov2words.word_length}") int combinations
    ) {
        this.wordRepository = wordRepository;
        this.languageService = languageService;
        this.combinations = combinations;
    }

    public void importWordsForLanguage(List<String> words, String language) throws UnknownLanguageException {
        log.info("Importing word list for language: {}", language);
        // Validating the language.
        this.languageService.validateLanguage(language);

        // Creating a list with distinct entries.
        Set<String> wordSet = new HashSet<>(words);
        words = new ArrayList<>(wordSet);

        // Generating date only once to reduce computational costs.
        DateTime now = DateTime.now();

        List<WordEntity> wordListEntities = new ArrayList<>();

        for (int i = 0; i < words.size(); i++) {
            wordListEntities.add(
                    new WordEntity(
                            UUID.randomUUID(),
                            now,
                            now,
                            null,
                            words.get(i),
                            language,
                            i
                    )
            );
        }

        this.wordRepository.saveAll(wordListEntities);
    }

    /**
     * Returns the word list for a certain language.
     */
    public WordResponseDTO getWordsForLanguage(String language) throws UnknownLanguageException {
        log.debug("Getting the word list for language: {}", language);
        this.languageService.validateLanguage(language);
        return new WordResponseDTO(
                language,
                this.combinations,
                this.wordRepository.findAllByLanguageAndDateInvalidatedIsNull(language)
                        .stream()
                        .map(WordEntity::getWord)
                        .collect(Collectors.toList())
        );
    }
}
