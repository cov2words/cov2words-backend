package io.hepp.cov2words.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * Requesting the complete wordlist of a certain language.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@ApiModel(
        value = "WordRequest",
        description = "Request that is used to retrieve the word list for a certain language.")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class WordRequestDTO implements Serializable {
    /**
     * Contains the ISO language country code.
     */
    @ApiModelProperty(value = "Contains the ISO language country code.")
    @JsonProperty(value = "language")
    private String language;
}
