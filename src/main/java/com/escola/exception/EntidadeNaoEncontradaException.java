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
    public EntidadeNaoEncontradaException(String message) {
        super(message);
    }
}