package com.escola.exception;

/**
 * Unchecked exception thrown when an attempted operation is invalid due to
 * the current state of the system or specific business rules.
 * <p>
 * Examples include trying to delete an entity that is referenced by other
 * critical entities or performing an action that violates a business constraint
 * not covered by simple data validation.
 * </p>
 * <p>
 * As an unchecked exception, methods are not required to declare this in their
 * 'throws' clause. It's typically handled by a global exception handler
 * that translates it into an appropriate client error response (e.g., HTTP 400 or 409).
 * </p>
 *
 * @version 1.1
 * @author SeuNomeAqui
 */
public class OperacaoInvalidaException extends RuntimeException { // Alterado de Exception para RuntimeException

  /**
   * Constructs a new OperacaoInvalidaException with the specified detail message.
   *
   * @param message the detail message. The detail message is saved for
   * later retrieval by the {@link #getMessage()} method.
   */
  public OperacaoInvalidaException(String message) {
    super(message);
  }

  /**
   * Constructs a new OperacaoInvalidaException with the specified detail message and cause.
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
  public OperacaoInvalidaException(String message, Throwable cause) {
    super(message, cause);
  }
}