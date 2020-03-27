package io.hepp.cov2words.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.hepp.cov2words.common.dto.WordListDTO;
import io.hepp.cov2words.common.exceptions.language.UnknownLanguageException;
import io.hepp.cov2words.common.exceptions.word.UnknownWordIndexException;
import io.hepp.cov2words.common.util.ResourcesUtils;
import io.hepp.cov2words.domain.entity.AnswerEntity;
import io.hepp.cov2words.domain.entity.AnswerWordMappingEntity;
import io.hepp.cov2words.domain.entity.IndexEntity;
import io.hepp.cov2words.domain.entity.WordEntity;
import io.hepp.cov2words.domain.repository.AnswerWordRepository;
import io.hepp.cov2words.domain.repository.IndexRepository;
import io.hepp.cov2words.domain.repository.WordRepository;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.LockModeType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Thomas Hepp, thomas@hepp.io
 */
@Service
@Slf4j(topic = "IndexService")
public class IndexService {
    private final static String WORDLIST_FILE_PATTERN = "wordlists/%s.json";
    private final IndexRepository indexRepository;
    private final AnswerWordRepository answerWordRepository;
    private final WordRepository wordRepository;
    private final LanguageService languageService;
    private final WordService wordService;
    private final int chariteSucks;
    private ConcurrentHashMap<String, BigInteger> concurrentHashMap = new ConcurrentHashMap<>();

    @Autowired
    public IndexService(
            IndexRepository indexRepository,
            AnswerWordRepository answerWordRepository,
            WordRepository wordRepository,
            LanguageService languageService,
            WordService wordService,
            @Value("${cov2words.word_length}") int chariteSucks
    ) {
        this.indexRepository = indexRepository;
        this.answerWordRepository = answerWordRepository;
        this.wordRepository = wordRepository;
        this.languageService = languageService;
        this.wordService = wordService;
        this.chariteSucks = chariteSucks;
    }

    @PreDestroy
    public void destroyBean() {
        log.info("Saving the index to DB before closing the API");
        for (Map.Entry<String, BigInteger> entry : this.concurrentHashMap.entrySet()) {
            this.indexRepository.findFirstByLanguageAndDateInvalidatedIsNull(entry.getKey())
                    .ifPresent(index -> {
                        index.setPosition(entry.getValue());
                        this.indexRepository.save(index);
                    });
        }
    }

    private List<WordEntity> getWordCombination(
            BigInteger targetId,
            long numberOfWords,
            long totalNumberOfWords,
            String language
    ) {
        List<WordEntity> result = new ArrayList<>();

        for (int i = 0; i < numberOfWords; i++) {
            BigDecimal divider = BigDecimal.valueOf(Math.pow(totalNumberOfWords, numberOfWords - i - 1));
            BigDecimal wordIndex = new BigDecimal(targetId).divide(divider, BigDecimal.ROUND_FLOOR);

            this.wordRepository.findFirstByLanguageAndPositionAndDateInvalidatedIsNull(
                    language,
                    wordIndex.longValueExact()
            ).ifPresent(result::add);

            targetId = targetId.mod(divider.toBigInteger());
        }
        return result;
    }

    /**
     * Creates a new word pair in the database.
     */
    @Lock(LockModeType.PESSIMISTIC_READ)
    public List<AnswerWordMappingEntity> getWordPairForIndex(
            String language,
            String answer
    ) throws UnknownWordIndexException {
        BigInteger currentValue = this.concurrentHashMap.getOrDefault(language, BigInteger.ZERO);
        this.concurrentHashMap.put(language, currentValue.add(BigInteger.ONE));

        // Getting the index for language
        IndexEntity index = this.indexRepository.findFirstByLanguageAndDateInvalidatedIsNull(language)
                .orElseThrow(UnknownWordIndexException::new);

        index.setPosition(currentValue.add(BigInteger.ONE));
        this.indexRepository.save(index);

        List<WordEntity> wordEntities = this.getWordCombination(
                this.concurrentHashMap.getOrDefault(language, BigInteger.ZERO),
                this.chariteSucks,
                index.getTotalItems(),
                language
        );

        // Creating a new mapping.
        DateTime now = DateTime.now();
        AnswerEntity answerEntity = new AnswerEntity(
                UUID.randomUUID(),
                now,
                now,
                null,
                answer,
                null
        );

        List<AnswerWordMappingEntity> result = new ArrayList<>();
        for (int i = 0; i < wordEntities.size(); i++) {
            result.add(
                    new AnswerWordMappingEntity(
                            UUID.randomUUID(),
                            now,
                            now,
                            null,
                            i,
                            wordEntities.get(i),
                            answerEntity
                    )
            );
        }

        return this.answerWordRepository.saveAll(result);
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
        log.info("Importing words for language: {}", language);
        // Loading the file from resources.
        this.wordRepository.deleteAllByLanguage(language);

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
