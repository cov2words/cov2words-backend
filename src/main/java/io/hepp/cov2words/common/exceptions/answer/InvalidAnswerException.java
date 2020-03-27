package io.hepp.cov2words.common.exceptions.answer;

import io.hepp.cov2words.common.annotation.ErrorHandling;
import io.hepp.cov2words.common.constant.ErrorCodes;

/**
 * @author Thomas Hepp, thomas@hepp.io
 */
@ErrorHandling(
        error_code = ErrorCodes.AnswerError.INVALID_ANSWER_RESULT,
        status_code = 200,
        message = InvalidAnswerException.MESSAGE,
        logging = false
)
public class InvalidAnswerException extends Throwable {
    public static final String MESSAGE = "The given answer seems to be incorrect.";
}
