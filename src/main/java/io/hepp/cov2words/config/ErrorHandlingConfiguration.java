package io.hepp.cov2words.config;

import io.hepp.cov2words.common.annotation.ErrorHandling;
import io.hepp.cov2words.common.dto.WrappedResponseDTO;
import io.hepp.cov2words.common.exceptions.ExternalAPIException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.reflect.UndeclaredThrowableException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Global Error handler that catches the external API exceptions and generates the error response object.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@ControllerAdvice
@Slf4j(topic = "ErrorHandlingConfiguration")
public class ErrorHandlingConfiguration extends ResponseEntityExceptionHandler {

    private static final String DEFAULT_ERROR_MESSAGE = "An internal error occurred";
    private static final String ERROR_MESSAGE_PATTERN = "({}) {}";

    private final boolean logException;

    public ErrorHandlingConfiguration() {
        this(true);
    }

    public ErrorHandlingConfiguration(boolean logException) {
        this.logException = logException;
    }

    @ExceptionHandler(value = ExternalAPIException.class)
    protected ResponseEntity<Object> handleExternalAPIException(Exception exception, WebRequest request) {
        ExternalAPIException externalAPIException = (ExternalAPIException) exception;
        ErrorHandling errorHandling = externalAPIException.getClass().getAnnotation(ErrorHandling.class);

        String errorId = RandomStringUtils.randomAlphanumeric(8);
        // check if annotation exists
        if (errorHandling == null) {
            log.error(
                    ERROR_MESSAGE_PATTERN,
                    errorId,
                    DEFAULT_ERROR_MESSAGE,
                    this.logException ? exception : null);

            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_JSON);
            return handleExceptionInternal(exception, WrappedResponseDTO.fromError(
                    1,
                    DEFAULT_ERROR_MESSAGE,
                    errorId),
                    header,
                    HttpStatus.OK,
                    request);
        } else {
            log.error(
                    ERROR_MESSAGE_PATTERN,
                    errorId,
                    errorHandling.message(),
                    (this.logException && errorHandling.logging()) ? exception : null);
            // Returning an object with HTTP Status Code.
            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.APPLICATION_JSON);
            return handleExceptionInternal(
                    exception,
                    WrappedResponseDTO.
                            fromError(
                                    errorHandling.error_code(),
                                    errorHandling.message(),
                                    errorId),
                    header,
                    Objects.requireNonNull(HttpStatus.resolve(errorHandling.status_code())),
                    request);
        }
    }

    @ExceptionHandler(value = NoSuchAlgorithmException.class)
    protected ResponseEntity<Object> handleNoSuchAlgorithmException(Exception ex, WebRequest request) {
        return handleDefaultException(ex, request);
    }

    @ExceptionHandler(value = UndeclaredThrowableException.class)
    protected ResponseEntity<Object> handleDefaultException(UndeclaredThrowableException ex, WebRequest request) {
        return this.handleExternalAPIException((Exception) ex.getUndeclaredThrowable(), request);
    }

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<Object> handleDefaultException(Exception ex, WebRequest request) {
        String errorId = RandomStringUtils.randomAlphanumeric(8);
        log.error(ERROR_MESSAGE_PATTERN, errorId, DEFAULT_ERROR_MESSAGE, ex);
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        // Returning an object with HTTP status code.
        return handleExceptionInternal(
                ex,
                WrappedResponseDTO
                        .fromError(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                DEFAULT_ERROR_MESSAGE,
                                errorId),
                header,
                HttpStatus.OK,
                request);
    }
}
