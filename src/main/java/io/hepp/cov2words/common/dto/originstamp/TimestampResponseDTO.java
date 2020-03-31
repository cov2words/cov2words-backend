package io.hepp.cov2words.common.dto.originstamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * DTO that contains the timestamp information.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TimestampResponseDTO extends OriginStampResponse {

    @JsonProperty("created")
    private boolean created;

    @JsonProperty("date_created")
    private long dateCreated;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("hash_string")
    private String hash;

    @JsonProperty("timestamps")
    private List<TimestampDataDTO> timestampDataDTOList;

    /**
     * Child DTO that contains the currency specific timestamp information.
     */
    @Getter
    @Setter
    @ToString
    public static class TimestampDataDTO implements Serializable {

        @JsonProperty("currency_id")
        private int currency;

        @JsonProperty("transaction")
        private String transaction;

        @JsonProperty("private_key")
        private String privateKey;

        @JsonProperty("timestamp")
        private Long timestamp;

        @JsonProperty("submit_status")
        private long status;
    }
}
