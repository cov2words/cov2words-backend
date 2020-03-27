package io.hepp.cov2words.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

/**
 * Contains the word list for a certain language.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@ApiModel(
        value = "WordResponse",
        description = "Contains the word list for a certain language.")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class WordResponseDTO {
    /**
     * Contains the ISO language country code.
     */
    @ApiModelProperty(value = "Contains the ISO language country code.")
    @JsonProperty(value = "language")
    private String language;

    /**
     * List that contains the signal words.
     */
    @ApiModelProperty(value = "List that contains the signal words.")
    @JsonProperty(value = "words")
    private List<String> words;
}
