package io.hepp.cov2words.service;

import io.hepp.cov2words.common.dto.AnswerRequestDTO;
import io.hepp.cov2words.common.dto.WordPairRequestDTO;
import io.hepp.cov2words.common.dto.WordPairResponseDTO;
import io.hepp.cov2words.common.exceptions.answer.InvalidAnswerException;
import io.hepp.cov2words.common.exceptions.answer.InvalidWordOrderException;
import io.hepp.cov2words.common.exceptions.answer.NoAnswerForWordPairException;
import io.hepp.cov2words.common.exceptions.language.UnknownLanguageException;
import io.hepp.cov2words.domain.entity.AnswerEntity;
import io.hepp.cov2words.domain.entity.AnswerWordMappingEntity;
import io.hepp.cov2words.domain.repository.AnswerRepository;
import io.hepp.cov2words.domain.repository.AnswerWordRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Pair service that is used to generate the word pairs.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@Service
@Slf4j(topic = "PairService")
public class PairService {

    private final AnswerWordRepository answerWordRepository;
    private final AnswerRepository answerRepository;
    private final LanguageService languageService;
    private final IndexService indexService;

    @Autowired
    public PairService(
            AnswerWordRepository answerWordRepository,
            AnswerRepository answerRepository,
            LanguageService languageService,
            IndexService indexService
    ) {
        this.answerWordRepository = answerWordRepository;
        this.answerRepository = answerRepository;
        this.languageService = languageService;
        this.indexService = indexService;
    }

    public void validateOrder(List<WordPairResponseDTO.WordDTO> words) throws InvalidWordOrderException {
        for (int i = 0; i < words.size(); i++) {

            int finalI = i;
            long found = words.stream()
                    .filter(word -> word.getOrder() == finalI)
                    .count();

            if (found != 1) {
                throw new InvalidWordOrderException();
            }
        }
    }

    /**
     * Returns the answer results for a word pair.
     */
    public WordPairResponseDTO getAnswer(AnswerRequestDTO request) throws
            UnknownLanguageException,
            NoAnswerForWordPairException,
            InvalidWordOrderException {
        log.info("Checking if there is an answer for the given keywords");
        this.languageService.validateLanguage(request.getLanguage());
        this.validateOrder(request.getWords());

        List<AnswerWordMappingEntity> result = this.answerWordRepository.findAllByWord_LanguageAndDateInvalidatedIsNullAndWord_WordInOrderByOrder(
                request.getLanguage(),
                request.getWords().stream()
                        .map(WordPairResponseDTO.WordDTO::getWord)
                        .toArray(String[]::new)
        );

        if (result.isEmpty()) {
            throw new NoAnswerForWordPairException();
        } else {
            HashMap<AnswerEntity, List<AnswerWordMappingEntity>> mapping = this.getMap(result);

            for (Map.Entry<AnswerEntity, List<AnswerWordMappingEntity>> entry : mapping.entrySet()) {
                AnswerEntity answer = entry.getKey();
                if (this.compareOrder(request.getWords(), entry.getValue())) {
                    log.info("Found corresponding answer: {}", answer);
                    return new WordPairResponseDTO(
                            answer.getAnswer(),
                            request.getLanguage(),
                            this.getWords(entry.getValue())
                    );
                }
            }

            // Throwing exception if no matching was found.
            throw new NoAnswerForWordPairException();
        }
    }

    private boolean compareOrder(
            List<WordPairResponseDTO.WordDTO> words,
            List<AnswerWordMappingEntity> mapping
    ) {
        for (int i = 0; i < words.size(); i++) {
            int finalI = i;
            Optional<WordPairResponseDTO.WordDTO> inputWord = words.stream()
                    .filter(word -> word.getOrder() == finalI)
                    .findFirst();

            Optional<AnswerWordMappingEntity> referenceWord = mapping.stream()
                    .filter(word -> word.getOrder() == finalI)
                    .findFirst();

            if (!inputWord.isPresent() || !referenceWord.isPresent()) {
                return false;
            } else if (inputWord.get().getWord().equals(referenceWord.get().getWord().getWord())) {
                return false;
            }
        }

        return true;
    }

    private HashMap<AnswerEntity, List<AnswerWordMappingEntity>> getMap(List<AnswerWordMappingEntity> result) {
        HashMap<AnswerEntity, List<AnswerWordMappingEntity>> mapping = new HashMap<>();
        result.stream().forEach(element -> {
            List<AnswerWordMappingEntity> answer = mapping.get(element.getAnswerEntity());

            if (CollectionUtils.isEmpty(answer)) {
                answer = new ArrayList<>();
            }
            answer.add(element);

            mapping.put(element.getAnswerEntity(), answer);
        });

        return mapping;
    }

    private Optional<AnswerEntity> getAnswer(String input) throws InvalidAnswerException {
        this.validateAnswer(input);

        return this.answerRepository
                .findFirstByAnswerAndDateInvalidatedIsNull(input);
    }

    /**
     * Get or creates a word pair for an answer.
     */
    public WordPairResponseDTO getOrCreatePair(WordPairRequestDTO request) throws
            UnknownLanguageException, InvalidAnswerException {
        log.info("Get or create word pair for {}", request);

        this.languageService.validateLanguage(request.getLanguage());

        Optional<AnswerEntity> answer = this.getAnswer(request.getAnswer());

        if (answer.isPresent()) {
            log.info("Answer already exists, returning result");
            return new WordPairResponseDTO(
                    request.getAnswer(),
                    request.getLanguage(),
                    this.getWords(
                            this.answerWordRepository.findAllByWord_LanguageAndAnswerEntity_AnswerAndDateInvalidatedIsNullOrderByOrder(
                                    request.getLanguage(),
                                    request.getAnswer()
                            )
                    )
            );
        } else {
            log.info("Creating a new word-pair for {}", request);
            return new WordPairResponseDTO(
                    request.getAnswer(),
                    request.getLanguage(),
                    this.getWords(this.createWordPair(request))
            );
        }
    }

    private List<AnswerWordMappingEntity> createWordPair(WordPairRequestDTO request) {
        return this.indexService.getWordPairForIndex(
                request.getLanguage(),
                request.getAnswer()
        );
    }

    private List<WordPairResponseDTO.WordDTO> getWords(List<AnswerWordMappingEntity> mapping) {
        return mapping.stream()
                .map(map -> new WordPairResponseDTO.WordDTO(
                        map.getWord().getWord(),
                        map.getOrder()
                ))
                .collect(Collectors.toList());
    }

    /**
     * Validates the answer result and throws an exception if invalid.
     */
    public void validateAnswer(String answer) throws InvalidAnswerException {
        if (StringUtils.isEmpty(answer)) {
            throw new InvalidAnswerException();
        }
        // TODO improve check
    }
}
