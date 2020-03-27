package io.hepp.cov2words.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * Request that uses the word combination to retrieve the answer data.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@ApiModel(
        value = "AnswerRequest",
        description = "Request that uses the word combination to retrieve the answer data.")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class AnswerRequestDTO implements Serializable {
    /**
     * Contains the ISO language country code.
     */
    @ApiModelProperty(value = "Contains the ISO language country code.")
    @JsonProperty(value = "language")
    private String language;

    /**
     * Contains the word pair / combination for an answer of the questionaire.
     */
    @JsonProperty(value = "words")
    @ApiModelProperty(value = "Contains the word pair / combination for an answer of the questionaire.")
    private List<WordPairResponseDTO.WordDTO> words;
}
