package io.hepp.cov2words.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for exception to define error messages and codes.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ErrorHandling {
    /**
     * Error code that will provided within the response body.
     *
     * @return the external error is returned
     */
    int error_code();

    /**
     * Status code that will be provided as request status code.
     *
     * @return the http status code is returned
     */
    int status_code();

    /**
     * Error message that will be provided within the response body.
     *
     * @return the error message is returned
     */
    String message();

    /**
     * Indicates whether the exception stacktrace should be logged or not.
     *
     * @return whether logging or not
     */
    boolean logging() default true;
}
