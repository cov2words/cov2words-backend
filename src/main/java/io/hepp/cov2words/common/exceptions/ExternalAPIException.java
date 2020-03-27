package io.hepp.cov2words.common.exceptions;

/**
 * Thrown whenever an API request contains invalid request data.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
public abstract class ExternalAPIException extends Exception {

    public ExternalAPIException() {
        super();
    }

    public ExternalAPIException(String message) {
        super(message);
    }
}
