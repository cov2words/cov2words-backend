package io.hepp.cov2words.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.io.Serializable;

/**
 * Contains the number of combinations for a certain language.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@ApiModel(
        value = "Combination Response",
        description = "Contains the number of combinations for a certain language.")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class CombinationCountResponseDTO implements Serializable {
    @JsonProperty(value = "combinations")
    @ApiModelProperty(value = "Contains the number of combinations for a certain language.")
    private int combinations;
}
