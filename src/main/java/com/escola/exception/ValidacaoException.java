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
 * @author SeuNomeAqui
 */
public class ValidacaoException extends Exception { // Can also be RuntimeException

    /**
     * Constructs a new ValidacaoException with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for
     * later retrieval by the {@link #getMessage()} method.
     */
    public ValidacaoException(String message) {
        super(message);
    }

    /**
     * Constructs a new ValidacaoException with the specified detail message and cause.
     * <p>Note that the detail message associated with {@code cause} is
     * <i>not</i> automatically incorporated in this exception's detail
     * message.</p>
     *
     * @param message the detail message (which is saved for later retrieval
     * by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     * {@link #getCause()} method).  (A {@code null} value is
     * permitted, and indicates that the cause is nonexistent or
     * unknown.)
     */
    public ValidacaoException(String message, Throwable cause) {
        super(message, cause);
    }
}