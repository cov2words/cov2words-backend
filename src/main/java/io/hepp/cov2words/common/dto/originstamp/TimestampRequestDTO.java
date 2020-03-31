package io.hepp.cov2words.common.dto.originstamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * DTO that contains the request data for generating a new timestamp.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class TimestampRequestDTO extends OriginStampRequest {
    @JsonProperty("url")
    private String url;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("hash")
    private String hash;

    @JsonProperty("notifications")
    private List<NotificationDTO> notifications;

    /**
     * DTO that contains notification data.
     */
    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    public static class NotificationDTO implements Serializable {

        @JsonProperty(value = "target")
        private String target;

        @JsonProperty(value = "currency")
        private int currency;

        @JsonProperty(value = "notification_type")
        private int notificationType;
    }
}
