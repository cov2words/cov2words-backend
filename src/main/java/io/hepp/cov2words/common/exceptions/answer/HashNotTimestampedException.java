package io.hepp.cov2words.common.exceptions.answer;

import io.hepp.cov2words.common.annotation.ErrorHandling;
import io.hepp.cov2words.common.constant.ErrorCodes;

/**
 * @author Thomas Hepp, thomas@hepp.io
 */
@ErrorHandling(
        error_code = ErrorCodes.AnswerError.INVALID_HASH_STATUS,
        status_code = 401,
        message = HashNotTimestampedException.MESSAGE,
        logging = false
)
public class HashNotTimestampedException extends Throwable {
    public static final String MESSAGE = "Hash does not exist for a given answer.";
}
