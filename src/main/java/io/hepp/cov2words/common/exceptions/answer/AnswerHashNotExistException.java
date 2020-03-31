package io.hepp.cov2words.common.exceptions.answer;

import io.hepp.cov2words.common.annotation.ErrorHandling;
import io.hepp.cov2words.common.constant.ErrorCodes;

/**
 * @author Thomas Hepp, thomas@hepp.io
 */
@ErrorHandling(
        error_code = ErrorCodes.AnswerError.UNKNOWN_HASH,
        status_code = 401,
        message = AnswerHashNotExistException.MESSAGE,
        logging = false
)
public class AnswerHashNotExistException extends Throwable {
    public static final String MESSAGE = "Hash does not exist for a given answer.";
}
