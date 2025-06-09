package com.escola.service;

import com.escola.dto.MatriculaDTO;
import com.escola.dto.MatriculaRequestDTO;
import com.escola.exception.EntidadeNaoEncontradaException;
import com.escola.exception.OperacaoInvalidaException;
import com.escola.model.Matricula;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing student enrollments (Matricula).
 * This interface defines the core business operations related to enrollment,
 * including creating, retrieving, updating, and deleting enrollments,
 * as well as specific actions like listing with details or canceling.
 *
 * @version 1.0
 * @author FelipeCardoso
 */
public interface MatriculaService {

    /**
     * Performs a new student enrollment based on the provided request data.
     * This method handles the creation of a new enrollment, associating a student with a course.
     * Business rules and validations for the enrollment process should be applied here.
     *
     * @param matriculaRequestDTO The data transfer object containing the necessary information
     * to perform the enrollment (e.g., student ID, course ID).
     * @return A {@link MatriculaDTO} representing the newly created enrollment with its details.
     * @throws EntidadeNaoEncontradaException If the specified student or course does not exist.
     * @throws OperacaoInvalidaException      If the enrollment cannot be performed due to
     * business rule violations (e.g., a student already enrolled in course, course full).
     */
    MatriculaDTO realizarMatricula(MatriculaRequestDTO matriculaRequestDTO)
            throws EntidadeNaoEncontradaException, OperacaoInvalidaException;

    /**
     * Retrieves a list of all enrollments, including the names of associated students and courses.
     * This method provides a basic overview of all existing enrollments for administrative purposes.
     *
     * @return A {@link List} of {@link Matricula} objects, each representing an enrollment
     * with its associated student and course names populated.
     */
    List<Matricula> listarTodasMatriculasComNomes();

    /**
     * Searches for a specific enrollment by its unique identifier.
     *
     * @param id The unique ID of the enrollment to search for.
     * @return An {@link Optional} containing the {@link Matricula} object if found,
     * or an empty {@link Optional} if no enrollment with the given ID exists.
     */
    Optional<Matricula> buscarPorId(Long id);

    /**
     * Updates an existing enrollment with the provided input data.
     * This method applies changes to an existing enrollment, such as updating its status or other attributes.
     *
     * @param matriculaInput The {@link Matricula} object containing the updated data for an existing enrollment.
     * The ID within this object must correspond to an existing enrollment.
     * @return A {@link MatriculaDTO} representing the updated enrollment with its details.
     * @throws EntidadeNaoEncontradaException If the enrollment to be updated does not exist.
     * @throws OperacaoInvalidaException      If the update operation violates any business rules.
     */
    MatriculaDTO atualizar(Matricula matriculaInput)
            throws EntidadeNaoEncontradaException, OperacaoInvalidaException;

    /**
     * Removes an enrollment from the system by its unique identifier.
     * This operation typically implies a hard delete of the enrollment record.
     *
     * @param id The unique ID of the enrollment to be removed.
     * @throws EntidadeNaoEncontradaException If the enrollment to be removed does not exist.
     * @throws OperacaoInvalidaException      If the enrollment cannot be removed due to
     * business rules (e.g., historical data retention policies).
     */
    void remover(Long id) throws EntidadeNaoEncontradaException, OperacaoInvalidaException;

    /**
     * Retrieves a list of all enrollments with comprehensive details.
     * This method provides a more detailed view of each enrollment, possibly including
     * full student and course information, suitable for detailed reports or UI displays.
     *
     * @return A {@link List} of {@link MatriculaDTO} objects, each containing detailed
     * information about an enrollment.
     */
    List<MatriculaDTO> listarTodasMatriculasComDetalhes();

    /**
     * Searches for a specific enrollment by its unique identifier and returns it with comprehensive details.
     * This method is similar to {@link #buscarPorId(Long)} but provides a richer DTO containing
     * full student and course information.
     *
     * @param id The unique ID of the enrollment to search for.
     * @return A {@link MatriculaDTO} representing the found enrollment with all its details.
     * @throws EntidadeNaoEncontradaException If no enrollment with the given ID exists.
     */
    MatriculaDTO buscarMatriculaPorIdComDetalhes(Long id) throws EntidadeNaoEncontradaException;

    /**
     * Cancels an existing student enrollment based on its unique identifier.
     * This operation typically involves updating the enrollment status to "canceled" rather than a hard delete,
     * allowing for historical tracking.
     *
     * @param id The unique ID of the enrollment to be canceled.
     * @throws EntidadeNaoEncontradaException If the enrollment to be canceled does not exist.
     * @throws OperacaoInvalidaException      If the enrollment cannot be canceled due to
     * business rules (e.g., already canceled, course completed).
     */
    void cancelarMatricula(Long id) throws EntidadeNaoEncontradaException, OperacaoInvalidaException;

}