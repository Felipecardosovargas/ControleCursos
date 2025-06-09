package com.escola.service;

import com.escola.dto.CursoDTO;
import java.util.List;

/**
 * Interface that defines the services related to the Course entity.
 * It uses DTOs (Data Transfer Objects) to encapsulate input and output data,
 * promoting data isolation and clear communication between layers.
 *
 * @version 1.1
 * @author FelipeCardoso
 */
public interface CursoService {

    /**
     * Creates a new course with the provided details.
     * This method handles the persistence of a new course record in the system.
     *
     * @param nome The name, of course.
     * @param descricao The description of the course.
     * @param cargaHoraria The workload (in hours) of the course.
     * @return A {@link CursoDTO} representing the newly created course, including its assigned ID.
     */
    CursoDTO criarCurso(String nome, String descricao, int cargaHoraria);

    /**
     * Retrieves a list of all registered courses.
     * This method provides a comprehensive overview of all courses currently available in the system.
     *
     * @return A {@link List} of {@link CursoDTO} objects, where each DTO represents a course.
     * Returns an empty list if no courses are registered.
     */
    List<CursoDTO> listarTodosCursos();

    /**
     * Searches for courses whose name contains the specified query string (case-insensitive).
     * This method is useful for filtering or finding courses based on partial name matches.
     *
     * @param nomeQuery The full or partial name of the course to search for.
     * @return A {@link List} of {@link CursoDTO} objects that match the search criterion.
     * Returns an empty list if no matching courses are found.
     */
    List<CursoDTO> buscarCursosPorNomeContendo(String nomeQuery);

    /**
     * Retrieves a specific course by its unique identifier.
     *
     * @param id The unique ID of the course to be retrieved.
     * @return A {@link CursoDTO} representing the found course.
     * @throws com.escola.exception.EntidadeNaoEncontradaException If no course with the given ID is found.
     */
    CursoDTO buscarCursoPorId(Long id);

    /**
     * Updates the information of an existing course.
     * This method allows modification of a course's name, description, and workload.
     *
     * @param id The unique ID of the course to be updated.
     * @param nome The new name for the course. Can be null if not updating the name.
     * @param descricao The new description for the course. Can be null if not updating the description.
     * @param cargaHoraria The new workload for the course. Can be null if not updating the workload.
     * @return A {@link CursoDTO} representing the updated course.
     * @throws com.escola.exception.EntidadeNaoEncontradaException If the course with the specified ID does not exist.
     */
    CursoDTO atualizarCurso(Long id, String nome, String descricao, Integer cargaHoraria);

    /**
     * Deletes a course from the system based on its unique identifier.
     * This operation permanently removes the course record.
     *
     * @param id The unique ID of the course to be deleted.
     * @throws com.escola.exception.EntidadeNaoEncontradaException If the course with the specified ID does not exist.
     */
    void deletarCurso(Long id);
}