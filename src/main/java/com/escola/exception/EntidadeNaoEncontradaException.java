package com.escola.exception;

/**
 * Unchecked exception thrown when a requested entity is not found in the system.
 * <p>
 * This typically occurs when an operation attempts to retrieve or manipulate an entity
 * (e.g., Aluno, Curso, Matricula) by its identifier, but no such entity exists.
 * </p>
 *
 * @version 1.0
 * @author SeuNomeAqui
 */
public class EntidadeNaoEncontradaException extends RuntimeException {

    /**
     * Constructs a new EntidadeNaoEncontradaException with the specified detail message.
     *
     * @param message the detail message. The detail message is saved for
     * later retrieval by the {@link #getMessage()} method.
     */
    public EntidadeNaoEncontradaException(String message) {
        super(message);
    }

    /**
     * Constructs a new EntidadeNaoEncontradaException with the specified detail message and cause.
     * <p>Note that the detail message associated with {@code cause} is
     * <i>not</i> automatically incorporated in this runtime exception's detail
     * message.</p>
     *
     * @param message the detail message (which is saved for later retrieval
     * by the {@link #getMessage()} method).
     * @param cause   the cause (which is saved for later retrieval by the
     * {@link #getCause()} method).  (A {@code null} value is
     * permitted, and indicates that the cause is nonexistent or
     * unknown.)
     */
    public EntidadeNaoEncontradaException(String message, Throwable cause) {
        super(message, cause);
    }
}