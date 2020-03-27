package io.hepp.cov2words.common.exceptions.word;

import io.hepp.cov2words.common.annotation.ErrorHandling;
import io.hepp.cov2words.common.constant.ErrorCodes;
import io.hepp.cov2words.common.exceptions.ExternalAPIException;

/**
 * @author Thomas Hepp, thomas@hepp.io
 */
@ErrorHandling(
        error_code = ErrorCodes.WordError.UNKNOWN_WORD_INDEX,
        status_code = 200,
        message = UnknownWordIndexException.MESSAGE,
        logging = false
)
public class UnknownWordIndexException extends ExternalAPIException {
    public static final String MESSAGE = "Could not find word index. Please contact adminstrators.";
}
