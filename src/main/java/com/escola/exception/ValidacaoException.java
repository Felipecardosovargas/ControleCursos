package com.escola.exception;

/**
 * Unchecked exception thrown when input data fails business validation rules.
 * <p>
 * This exception indicates that data provided by a client or user does not meet
 * the required criteria, such as invalid format, missing mandatory fields,
 * or inconsistent values that violate application-specific business logic.
 * </p>
 * <p>
 * As an unchecked exception (extending {@link RuntimeException}), it does not need
 * to be explicitly declared in method signatures. Callers are expected to handle
 * this exception, typically by informing the user about the specific validation errors
 * or returning appropriate error responses in an API. It signals a condition that
 * can and should be corrected by the client providing the input.
 * </p>
 *
 * @version 1.1
 * @author FelipeCardoso
 */
public class ValidacaoException extends RuntimeException {

    /**
     * Constructs a new {@code ValidacaoException} with the specified detail message.
     * The detail message should describe the validation failure, usually in a user-friendly format.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * Example: "O campo 'nome' é obrigatório."
     */
    public ValidacaoException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code ValidacaoException} with the specified detail message and cause.
     * This constructor is useful when a lower-level exception (e.g., from a data parsing library)
     * triggers the validation failure.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method).
     * A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.
     */
    public ValidacaoException(String message, Throwable cause) {
        super(message, cause);
    }
}