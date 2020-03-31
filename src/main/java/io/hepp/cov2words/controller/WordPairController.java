package io.hepp.cov2words.controller;

import io.hepp.cov2words.common.constant.Paths;
import io.hepp.cov2words.common.dto.*;
import io.hepp.cov2words.common.exceptions.answer.*;
import io.hepp.cov2words.common.exceptions.language.UnknownLanguageException;
import io.hepp.cov2words.common.exceptions.word.InvalidWordOrderException;
import io.hepp.cov2words.common.exceptions.word.UnknownWordIndexException;
import io.hepp.cov2words.service.LanguageService;
import io.hepp.cov2words.service.PairService;
import io.hepp.cov2words.service.WordService;
import io.hepp.cov2words.service.originstamp.OriginStampService;
import io.micrometer.core.annotation.Timed;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;

/**
 * Controller that handles incoming requests for generating word information.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@Controller
@Slf4j(topic = "WordPairController")
@RestController
@RequestMapping(value = Paths.BASE_PATH)
@Api(
        value = "WordPairController",
        description = "Controller that handles incoming requests for generating word information.",
        tags = {"Word Pair Combinations"}
)
public class WordPairController {

    private final PairService pairService;
    private final LanguageService languageService;
    private final WordService wordService;
    private final OriginStampService originStampService;
    private final CombinationCountResponseDTO countResponseDTO;

    @Autowired
    public WordPairController(
            PairService pairService,
            LanguageService languageService,
            WordService wordService,
            OriginStampService originStampService,
            @Value("${cov2words.word_length}") int combinations
    ) {
        this.pairService = pairService;
        this.languageService = languageService;
        this.wordService = wordService;
        this.originStampService = originStampService;
        this.countResponseDTO = new CombinationCountResponseDTO(combinations);
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
    @ApiOperation(
            value = "WordPairResponse",
            notes = "Gets or creates a new pair for a answers & language.",
            nickname = "getOrCreateWordPair"
    )
    public WrappedResponseDTO<WordPairResponseDTO> getOrCreateWordPair(
            @RequestBody WordPairRequestDTO request
    ) throws
            UnknownLanguageException,
            InvalidAnswerException,
            UnknownWordIndexException,
            NoSuchAlgorithmException,
            HashNotTimestampedException {
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
    @ApiOperation(
            value = "WordPairResponse",
            notes = "Gets the answers for a certain word pair.",
            nickname = "getAnswerForWordPair"
    )
    public WrappedResponseDTO<WordPairResponseDTO> getAnswerForWordPair(
            @RequestBody AnswerRequestDTO request
    ) throws
            NoAnswerForWordPairException,
            InvalidWordOrderException,
            UnknownLanguageException,
            NoSuchAlgorithmException,
            HashNotTimestampedException {
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
    @ApiOperation(
            value = "LanguageResponse",
            notes = "Gets the available languages from the API.",
            nickname = "getAvailableLanguages"
    )
    public WrappedResponseDTO<LanguageResponseDTO> getAvailableLanguages() {
        return WrappedResponseDTO.fromData(this.languageService.getAvailableLanguages());
    }

    /**
     * Returns the wordlist for a certain language.
     */
    @RequestMapping(
            value = Paths.PairPaths.GET_WORDS,
            method = RequestMethod.POST
    )
    @ResponseBody
    @CrossOrigin(origins = "*")
    @Timed(value = "word.get", description = "Returns the wordlist for a certain language.")
    @ApiOperation(
            value = "WordResponse",
            notes = "Returns the wordlist for a certain language.",
            nickname = "getAvailableWordsForLanguage"
    )
    public WrappedResponseDTO<WordResponseDTO> getAvailableWordsForLanguage(
            @RequestBody WordRequestDTO request
    ) throws UnknownLanguageException {
        return WrappedResponseDTO.fromData(this.wordService.getWordsForLanguage(request.getLanguage()));
    }

    /**
     * Returns the word length of a combination.
     */
    @RequestMapping(
            value = Paths.PairPaths.COMBINATIONS,
            method = RequestMethod.POST
    )
    @ResponseBody
    @CrossOrigin(origins = "*")
    @Timed(value = "word.combination.get", description = "Returns the word length of a combination.")
    @ApiOperation(
            value = "CombinationCountResponse",
            notes = "Returns the word length of a combination.",
            nickname = "getCombinationCounts"
    )
    public WrappedResponseDTO<CombinationCountResponseDTO> getCombinationCounts(
            @RequestBody WordRequestDTO request
    ) {
        // TODO make language dependent
        return WrappedResponseDTO.fromData(this.countResponseDTO);
    }

    @GetMapping(
            value = Paths.PairPaths.GET_PROOF_FOR_ANSWER,
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    @ResponseBody
    @CrossOrigin(origins = "*")
    @Timed(value = "answer.proof.get", description = "Returns the certificate for the word pair.")
    @ApiOperation(
            value = "Certificate",
            notes = "Returns the certificate for the word pair.",
            nickname = "getCertificateForAnswer"
    )
    public byte[] getCertificateForAnswer(@RequestParam("hash") String hash) throws
            HashNotTimestampedException,
            AnswerHashNotExistException,
            InvalidHashException {
        return this.originStampService.getProofForHash(hash);
    }
}
