package io.hepp.cov2words.common.exceptions.answer;

import io.hepp.cov2words.common.annotation.ErrorHandling;
import io.hepp.cov2words.common.constant.ErrorCodes;

/**
 * @author Thomas Hepp, thomas@hepp.io
 */
@ErrorHandling(
        error_code = ErrorCodes.AnswerError.INVALID_HASH,
        status_code = 401,
        message = InvalidHashException.MESSAGE,
        logging = false
)
public class InvalidHashException extends Throwable {
    public static final String MESSAGE = "The given input parameter is not correct";
}
