package io.hepp.cov2words.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * Contains a list with all available languages.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@ApiModel(
        value = "Languages Response",
        description = "Contains a list with all available languages.")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class LanguageResponseDTO implements Serializable {

    @ApiModelProperty(value = "Contains a list with available language codes.")
    @JsonProperty(value = "languages")
    private List<String> languageCodes;
}
