package com.escola.repository;

import com.escola.model.Aluno;
import java.util.List;
import java.util.Optional;

/**
 * Interface for data access operations related to {@link Aluno} entities.
 * Defines the contract for CRUD operations and custom queries for students.
 *
 * @version 1.1
 * @author FelipeCardoso
 */
public interface AlunoRepository {

    /**
     * Persists a new Aluno entity to the database.
     *
     * @param aluno The {@link Aluno} entity to save. Must not be null.
     * @return The persisted Aluno entity, typically with a generated ID.
     */
    Aluno salvar(Aluno aluno);

    /**
     * Retrieves an Aluno entity by its unique identifier.
     *
     * @param id The ID of the Aluno to retrieve. Must not be null.
     * @return An {@link Optional} containing the Aluno if found, or an empty Optional otherwise.
     */
    Optional<Aluno> buscarPorId(Long id);

    /**
     * Retrieves an Aluno entity by its email address.
     * Email is expected to be unique.
     *
     * @param email The email of the Aluno to retrieve. Must not be null or empty.
     * @return An {@link Optional} containing the Aluno if found, or an empty Optional otherwise.
     */
    Optional<Aluno> buscarPorEmail(String email);

    /**
     * Retrieves all Aluno entities from the database.
     *
     * @return A {@link List} of all Alunos. The list may be empty if no students exist.
     */
    List<Aluno> listarTodos();

    /**
     * Updates an existing Aluno entity in the database.
     *
     * @param aluno The {@link Aluno} entity with updated information. Must not be null.
     * @return The updated Aluno entity.
     */
    Aluno atualizar(Aluno aluno);

    /**
     * Deletes an Aluno entity from the database by its unique identifier.
     *
     * @param id The ID of the Aluno to delete. Must not be null.
     */
    void deletarPorId(Long id);
}