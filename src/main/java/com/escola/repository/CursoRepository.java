package com.escola.repository;

import com.escola.model.Curso;
import java.util.List;
import java.util.Optional;

/**
 * Interface for data access operations related to {@link Curso} entities.
 * Defines the contract for CRUD operations and custom queries for courses.
 *
 * @version 1.0
 * @author SeuNomeAqui
 */
public interface CursoRepository {

    /**
     * Persists a new Curso entity to the database.
     *
     * @param curso The {@link Curso} entity to save. Must not be null.
     * @return The persisted Curso entity, typically with a generated ID.
     */
    Curso salvar(Curso curso);

    /**
     * Retrieves a Curso entity by its unique identifier.
     *
     * @param id The ID of the Curso to retrieve. Must not be null.
     * @return An {@link Optional} containing the Curso if found, or an empty Optional otherwise.
     */
    Optional<Curso> buscarPorId(Long id);

    /**
     * Retrieves a Curso entity by its exact name.
     * Course names are expected to be unique.
     *
     * @param nome The exact name of the Curso to retrieve. Must not be null or empty.
     * @return An {@link Optional} containing the Curso if found, or an empty Optional otherwise.
     */
    Optional<Curso> buscarPorNomeExato(String nome);

    /**
     * Retrieves a list of Curso entities whose names contain the given string (case-insensitive).
     *
     * @param nomeParcial The partial name to search for. Must not be null.
     * @return A {@link List} of Cursos matching the criteria. The list may be empty.
     */
    List<Curso> buscarPorNomeContendo(String nomeParcial);

    /**
     * Retrieves all Curso entities from the database.
     *
     * @return A {@link List} of all Cursos. The list may be empty if no courses exist.
     */
    List<Curso> listarTodos();

    /**
     * Updates an existing Curso entity in the database.
     *
     * @param curso The {@link Curso} entity with updated information. Must not be null.
     * @return The updated Curso entity.
     */
    Curso atualizar(Curso curso);

    /**
     * Deletes a Curso entity from the database by its unique identifier.
     *
     * @param id The ID of the Curso to delete. Must not be null.
     * @return true if the course was deleted, false if the course was not found.
     */
    boolean deletarPorId(Long id);
}