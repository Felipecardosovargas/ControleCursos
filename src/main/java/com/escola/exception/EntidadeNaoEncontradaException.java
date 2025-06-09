package com.escola.exception;

/**
 * Unchecked exception thrown when a requested entity is not found in the system.
 * <p>
 * This exception indicates that an operation attempted to retrieve or manipulate an entity
 * (e.g., {@code Aluno}, {@code Curso}, {@code Matricula}) by its identifier or specific criteria,
 * but no matching entity could be found in the underlying data store or within the application's context.
 * </p>
 * <p>
 * As an unchecked exception (extending {@link RuntimeException}), it does not need to be
 * explicitly declared in method signatures or caught by calling code, making the API cleaner.
 * It typically signals a recoverable application state where the absence of an entity
 * is unexpected given the application's flow, but might be handled at a higher level
 * (e.g., by a global exception handler in a web application).
 * </p>
 *
 * @version 1.1
 * @author FelipeCardoso
 */
public class EntidadeNaoEncontradaException extends RuntimeException {

    /**
     * Constructs a new {@code EntidadeNaoEncontradaException} with the specified detail message.
     * The detail message is a description of the specific reason for the exception,
     * often indicating which entity was not found and by what identifier.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * Example: "Aluno com ID 123 não encontrado."
     */
    public EntidadeNaoEncontradaException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code EntidadeNaoEncontradaException} with the specified detail message and
     * cause. This constructor is useful for wrapping other exceptions (e.g., a data access exception)
     * that led to the entity not being found.
     *
     * @param message the detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the {@link #getCause()} method).
     * A {@code null} value is permitted, and indicates that the cause is nonexistent or unknown.
     */
    public EntidadeNaoEncontradaException(String message, Throwable cause) {
        super(message, cause);
    }
}