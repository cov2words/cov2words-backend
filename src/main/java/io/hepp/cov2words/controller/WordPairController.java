package io.hepp.cov2words.controller;

import io.hepp.cov2words.common.constant.Paths;
import io.hepp.cov2words.common.dto.*;
import io.hepp.cov2words.common.exceptions.answer.InvalidAnswerException;
import io.hepp.cov2words.common.exceptions.answer.NoAnswerForWordPairException;
import io.hepp.cov2words.common.exceptions.language.UnknownLanguageException;
import io.hepp.cov2words.common.exceptions.word.InvalidWordOrderException;
import io.hepp.cov2words.common.exceptions.word.UnknownWordIndexException;
import io.hepp.cov2words.service.LanguageService;
import io.hepp.cov2words.service.PairService;
import io.hepp.cov2words.service.WordService;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Controller that handles incoming requests for generating word information.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@Controller
@Slf4j(topic = "WordPairController")
@RestController
@RequestMapping(value = Paths.BASE_PATH)
@Api(value = "WordPairController", description = "Controller that handles incoming requests for generating word information.", tags = {"Word Pair Combinations"})
public class WordPairController {

    private final PairService pairService;
    private final LanguageService languageService;
    private final WordService wordService;

    @Autowired
    public WordPairController(
            PairService pairService,
            LanguageService languageService,
            WordService wordService
    ) {
        this.pairService = pairService;
        this.languageService = languageService;
        this.wordService = wordService;
    }

    /**
     * Gets or creates a new pair for a answers & language.
     */
    @RequestMapping(
            value = Paths.PairPaths.CREATE_PAIR,
            method = RequestMethod.POST
    )
    @ResponseBody
    @CrossOrigin(origins = "*")
    @Timed(value = "word.pair.create", description = "Gets or creates a new pair for a answers & language.")
    public WrappedResponseDTO<WordPairResponseDTO> getOrCreateWordPair(
            @RequestBody WordPairRequestDTO request
    ) throws
            UnknownLanguageException,
            InvalidAnswerException,
            UnknownWordIndexException {
        log.debug("Receiving request for generating a word pair: {}", request.toString());
        return WrappedResponseDTO.fromData(this.pairService.getOrCreatePair(request));
    }

    /**
     * Gets the answers for a certain word pair.
     */
    @RequestMapping(
            value = Paths.PairPaths.GET_ANSWER,
            method = RequestMethod.POST
    )
    @ResponseBody
    @CrossOrigin(origins = "*")
    @Timed(value = "word.pair.answer", description = "Gets the answers for a certain word pair.")
    public WrappedResponseDTO<WordPairResponseDTO> getAnswerForWordPair(
            @RequestBody AnswerRequestDTO request
    ) throws
            NoAnswerForWordPairException,
            InvalidWordOrderException,
            UnknownLanguageException {
        log.info("Receiving request for getting an answer from a word pair: {}", request.toString());
        return WrappedResponseDTO.fromData(this.pairService.getAnswer(request));
    }

    /**
     * Gets the available languages from the API.
     */
    @RequestMapping(
            value = Paths.PairPaths.GET_LANGUAGES,
            method = RequestMethod.GET
    )
    @ResponseBody
    @CrossOrigin(origins = "*")
    @Timed(value = "word.language.get", description = "Gets the answers for a certain word pair.")
    public WrappedResponseDTO<LanguageResponseDTO> getAvailableLanguages() {
        return WrappedResponseDTO.fromData(this.languageService.getAvailableLanguages());
    }

    /**
     * Gets the available languages from the API.
     */
    @RequestMapping(
            value = Paths.PairPaths.GET_WORDS,
            method = RequestMethod.POST
    )
    @ResponseBody
    @CrossOrigin(origins = "*")
    @Timed(value = "word.get", description = "Gets the answers for a certain word pair.")
    public WrappedResponseDTO<WordResponseDTO> getAvailableWordsForLanguage(
            @RequestBody WordRequestDTO request
    ) throws UnknownLanguageException {
        return WrappedResponseDTO.fromData(this.wordService.getWordsForLanguage(request.getLanguage()));
    }
}
