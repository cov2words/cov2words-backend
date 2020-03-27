package io.hepp.cov2words.common.exceptions.answer;

import io.hepp.cov2words.common.annotation.ErrorHandling;
import io.hepp.cov2words.common.constant.ErrorCodes;
import io.hepp.cov2words.common.exceptions.ExternalAPIException;

/**
 * @author Thomas Hepp, thomas@hepp.io
 */
@ErrorHandling(
        error_code = ErrorCodes.AnswerError.NO_ANSWER_FOR_WORD_COMBINATION,
        status_code = 200,
        message = NoAnswerForWordPairException.MESSAGE,
        logging = false
)
public class NoAnswerForWordPairException extends ExternalAPIException {
    public static final String MESSAGE = "We could not find an answer for your word combination.";
}
