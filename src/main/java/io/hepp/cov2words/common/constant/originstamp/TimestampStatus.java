package io.hepp.cov2words.common.constant.originstamp;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

/**
 * Enum that describes the timestamp status in the database.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@AllArgsConstructor
@Getter
public enum TimestampStatus {

    /**
     * Hash not submitted to OriginStamp.
     */
    UNSUBMITTED(-1),

    /**
     * Hash is submitted to OriginStamp and is being processed.
     */
    PROCESSING(0),

    /**
     * Hash submitted to OriginStamp, but not processed.
     */
    SUBMITTED(1),

    /**
     * Timestamp created by OriginStamp.
     */
    TIMESTAMPED(2),

    /**
     * Timestamp created.
     */
    FINISHED(3),

    /**
     * Certificate downloaded.
     */
    CERTIFIED(4);

    private int statusId;

    /**
     * Returns the TimestampStatus enum for a certain status ID.
     */
    public static TimestampStatus forStatusId(int statusId) {
        return Arrays.stream(TimestampStatus.values())
                .filter(element -> element.getStatusId() == statusId)
                .findFirst()
                .orElse(UNSUBMITTED);
    }
}
