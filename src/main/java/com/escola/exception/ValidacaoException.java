package com.escola.exception;

/**
 * Checked exception thrown when input data fails business validation rules.
 * <p>
 * This exception indicates that data provided by a client or user does not meet
 * the required criteria (e.g., invalid format, missing fields, inconsistent values).
 * Callers are expected to handle this exception, typically by informing the user
 * about the validation errors.
 * </p>
 *
 * @version 1.0
 * @author FelipeCardoso
 */

public class ValidacaoException extends RuntimeException {
    public ValidacaoException(String message) {
        super(message);
    }
}