package io.hepp.cov2words.common.exceptions.originstamp;

/**
 * Defines an exception which occurs during interaction with the OriginStamp API.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
public class OriginStampApiException extends Exception {
    /**
     * Constructor that is used to hand over the exception.
     */
    public OriginStampApiException(String message) {
        super(message);
    }
}
