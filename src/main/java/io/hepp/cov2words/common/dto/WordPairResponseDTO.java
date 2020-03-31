package io.hepp.cov2words.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * Class that contains the response payload for the generated word combination.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@ApiModel(
        value = "WordPairResponse",
        description = "Response payload that contains the necessary matching information")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class WordPairResponseDTO implements Serializable {
    @JsonProperty(value = "answer")
    @ApiModelProperty(value = "Contains the XML answer of the word pair.")
    private String answer;

    /**
     * Contains the XML answer string.
     */
    @JsonProperty(value = "answer_detail")
    @ApiModelProperty(value = "Contains the answer information of the word pair.")
    private AnswerDTO answerDetail;

    /**
     * Contains the ISO language country code.
     */
    @ApiModelProperty(value = "Contains the ISO language country code.")
    @JsonProperty(value = "language")
    private String language;

    /**
     * Contains the word pair / combination for an answer of the questionnaire.
     */
    @JsonProperty(value = "words")
    @ApiModelProperty(value = "Contains the word pair / combination for an answer of the questionnaire.")
    private List<WordDTO> words;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class WordDTO implements Serializable {
        /**
         * Word as a String.
         */
        @JsonProperty(value = "word")
        @ApiModelProperty(value = "Word as a String.")
        private String word;

        /**
         * Indicates the order of words. Order must be preserved.
         */
        @JsonProperty(value = "order")
        @ApiModelProperty(value = "Indicates the order of words. Order must be preserved.")
        private int order;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    @ToString
    public static class AnswerDTO implements Serializable {
        @JsonProperty(value = "answer")
        @ApiModelProperty(value = "Contains the XML answer of the word pair.")
        private String answer;

        @JsonProperty(value = "source")
        @ApiModelProperty(value = "Currently: Always null")
        private String source = null;

        @JsonProperty(value = "answer_hash")
        @ApiModelProperty(value = "Contains the answer hash")
        private String answerHash;

        @JsonProperty(value = "is_proof_available")
        @ApiModelProperty(value = "Indicates whether a certificate for the answer is available or not.")
        private boolean isProofAvailable = false;
    }
}
