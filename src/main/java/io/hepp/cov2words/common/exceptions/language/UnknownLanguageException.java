package io.hepp.cov2words.common.exceptions.language;

import io.hepp.cov2words.common.annotation.ErrorHandling;
import io.hepp.cov2words.common.constant.ErrorCodes;
import io.hepp.cov2words.common.exceptions.ExternalAPIException;

/**
 * @author Thomas Hepp, thomas@hepp.io
 */
@ErrorHandling(
        error_code = ErrorCodes.LanguageError.UNKNOWN_LANGUAGE,
        status_code = 200,
        message = UnknownLanguageException.MESSAGE,
        logging = false
)
public class UnknownLanguageException extends ExternalAPIException {
    public static final String MESSAGE = "Unknown language code. Please check your input";
}
