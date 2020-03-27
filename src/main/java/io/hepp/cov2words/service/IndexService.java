package io.hepp.cov2words.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hepp.cov2words.common.dto.WordListDTO;
import io.hepp.cov2words.common.exceptions.language.UnknownLanguageException;
import io.hepp.cov2words.common.util.ResourcesUtils;
import io.hepp.cov2words.domain.entity.IndexEntity;
import io.hepp.cov2words.domain.repository.IndexRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigInteger;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j(topic = "IndexService")
public class IndexService {
    private final static String WORDLIST_FILE_PATTERN = "wordlist/%s.json";
    private final IndexRepository indexRepository;
    private final LanguageService languageService;
    private final WordService wordService;
    private ConcurrentHashMap<String, BigInteger> concurrentHashMap = new ConcurrentHashMap<>();

    @Autowired
    public IndexService(
            IndexRepository indexRepository,
            LanguageService languageService,
            WordService wordService
    ) {
        this.indexRepository = indexRepository;
        this.languageService = languageService;
        this.wordService = wordService;
    }

    /**
     * Inits the data in the database and performs an initial setup of the application.
     */
    @PostConstruct
    public void initIndex() {
        log.info("Creating index for all available languages");
        // Getting the available languages.
        this.languageService.getLanguages()
                .stream()
                // Importing the data if necessary.
                .map(this::initDataRepository)
                // Skipping failed imports.
                .filter(Objects::nonNull)
                // Building memory index.
                .forEach(this::buildMemoryIndex);

        log.info("Indices created.");
    }

    private void buildMemoryIndex(IndexEntity indexEntity) {
        this.concurrentHashMap.put(
                indexEntity.getLanguage(),
                indexEntity.getPosition()
        );
    }

    private IndexEntity initDataRepository(String language) {
        log.info("Checking if data was imported for language: {}", language);

        // Checking if data was imported for language.
        Optional<IndexEntity> index = this.indexRepository.findFirstByLanguageAndDateInvalidatedIsNull(language);

        if (index.isPresent()) {
            return index.get();
        } else {
            try {
                return this.importWordsForLanguage(language);
            } catch (JsonProcessingException | UnknownLanguageException e) {
                log.error("An unexpected error occurred when importing data for language: {}", language, e);
            }
        }
        return null;
    }

    private IndexEntity importWordsForLanguage(String language) throws JsonProcessingException, UnknownLanguageException {
        // Loading the file from resources.
        String resource = ResourcesUtils.getResourceFileAsString(String.format(WORDLIST_FILE_PATTERN, language));
        WordListDTO wordList = this.getWordListDTO(resource);

        this.wordService.importWordsForLanguage(
                wordList.getTerms(),
                language
        );

        // Creating index entry.
        return this.indexRepository.save(
                new IndexEntity(
                        UUID.randomUUID(),
                        DateTime.now(),
                        DateTime.now(),
                        null,
                        BigInteger.ZERO,
                        wordList.getTerms().size(),
                        language
                )
        );
    }

    private WordListDTO getWordListDTO(String content) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(content, WordListDTO.class);
    }
}
