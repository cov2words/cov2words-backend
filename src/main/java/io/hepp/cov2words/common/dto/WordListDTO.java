package io.hepp.cov2words.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * @author Thomas Hepp, thomas@hepp.io
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class WordListDTO implements Serializable {
    @JsonProperty(value = "term")
    private List<String> terms;
}
