package io.hepp.cov2words.common.exceptions.word;

import io.hepp.cov2words.common.annotation.ErrorHandling;
import io.hepp.cov2words.common.constant.ErrorCodes;
import io.hepp.cov2words.common.exceptions.ExternalAPIException;

/**
 * @author Thomas Hepp, thomas@hepp.io
 */
@ErrorHandling(
        error_code = ErrorCodes.WordError.INVALID_WORD_ORDER,
        status_code = 200,
        message = InvalidWordOrderException.MESSAGE,
        logging = false
)
public class InvalidWordOrderException extends ExternalAPIException {
    public static final String MESSAGE = "We could not find an answer for your word combination.";
}
