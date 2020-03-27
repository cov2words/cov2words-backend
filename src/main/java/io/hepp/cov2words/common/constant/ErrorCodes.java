package io.hepp.cov2words.common.constant;

/**
 * Contains the static error codes.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
public final class ErrorCodes {
    /**
     * Everything was fine :).
     */
    public static final int NO_ERROR = 0;

    /**
     * Private constructor to avoid accidental creation of an instance.
     */
    private ErrorCodes() {
    }

    /**
     * Contains language relevant error codes.
     */
    public static final class LanguageError {
        private static final int OFFSET = 1000;
        public static final int UNKNOWN_LANGUAGE = OFFSET + 1;

        /**
         * Private constructor to avoid accidental creation of an instance.
         */
        private LanguageError() {
        }
    }

    /**
     * Contains answer / result relevant error codes.
     */
    public static final class AnswerError {
        private static final int OFFSET = 2000;
        public static final int INVALID_ANSWER_RESULT = OFFSET + 1;
        public static final int NO_ANSWER_FOR_WORD_COMBINATION = OFFSET + 2;

        /**
         * Private constructor to avoid accidental creation of an instance.
         */
        private AnswerError() {
        }
    }

    /**
     * Contains word related error codes.
     */
    public static final class WordError {
        private static final int OFFSET = 3000;
        public static final int INVALID_WORD_ORDER = OFFSET + 1;

        /**
         * Private constructor to avoid accidental creation of an instance.
         */
        private WordError() {
        }
    }
}
