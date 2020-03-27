package io.hepp.cov2words.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * Request that is used for generation of word pairs.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@ApiModel(
        value = "WordPairRequest",
        description = "Payload used for the generation of new / or existing word pair")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class WordPairRequestDTO implements Serializable {
    /**
     * Contains the XML answer string.
     */
    @JsonProperty(value = "answer")
    @ApiModelProperty(value = "Contains the XML answer of the word pair.")
    private String answer;

    /**
     * Contains the ISO language country code.
     */
    @ApiModelProperty(value = "Contains the ISO language country code.")
    @JsonProperty(value = "language")
    private String language;
}
