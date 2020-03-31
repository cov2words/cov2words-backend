package io.hepp.cov2words.common.dto.originstamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

/**
 * Default response object that is used by OriginStamp.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ServiceResponseDTO {
    /**
     * error_code != 0 => ERROR
     */
    @JsonProperty("error_code")
    private int errorCode;

    /**
     * Contains the corresponding error message.
     */
    @JsonProperty("error_message")
    private String errorMessage;

    /**
     * Data field that contains the actual payload.
     */
    @JsonProperty("data")
    private JsonNode responseBody;
}
