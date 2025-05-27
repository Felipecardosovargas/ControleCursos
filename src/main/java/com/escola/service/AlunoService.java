package com.escola.service;

import com.escola.dto.AlunoDTO;
import com.escola.exception.EntidadeNaoEncontradaException;
import com.escola.exception.OperacaoInvalidaException;
import com.escola.exception.ValidacaoException;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for managing {@link com.escola.model.Aluno} entities.
 * Defines business operations related to students, such as creation, retrieval,
 * updates, and deletion, including necessary validations.
 *
 * @version 1.0
 * @author SeuNomeAqui
 */
public interface AlunoService {

    /**
     * Creates a new student.
     *
     * @param nome The name of the student.
     * @param email The email of the student (must be unique).
     * @param dataNascimento The date of birth of the student.
     * @return The created {@link AlunoDTO}.
     * @throws ValidacaoException if input data is invalid (e.g., email already exists, name is empty).
     */
    AlunoDTO criarAluno(String nome, String email, LocalDate dataNascimento) throws ValidacaoException;

    /**
     * Finds a student by their ID.
     *
     * @param id The ID of the student.
     * @return The {@link AlunoDTO} if found.
     * @throws EntidadeNaoEncontradaException if no student is found with the given ID.
     */
    AlunoDTO buscarAlunoPorId(Long id) throws EntidadeNaoEncontradaException;

    /**
     * Finds a student by their email.
     *
     * @param email The email of the student.
     * @return The {@link AlunoDTO} if found.
     * @throws EntidadeNaoEncontradaException if no student is found with the given email.
     */
    AlunoDTO buscarAlunoPorEmail(String email) throws EntidadeNaoEncontradaException;

    /**
     * Lists all students.
     *
     * @return A list of {@link AlunoDTO}s.
     */
    List<AlunoDTO> listarTodosAlunos();

    /**
     * Updates an existing student.
     *
     * @param id The ID of the student to update.
     * @param nome The new name (if null, not changed).
     * @param email The new email (if null, not changed, must remain unique if changed).
     * @param dataNascimento The new date of birth (if null, not changed).
     * @return The updated {@link AlunoDTO}.
     * @throws EntidadeNaoEncontradaException if the student to update is not found.
     * @throws ValidacaoException if updated, data is invalid.
     */
    AlunoDTO atualizarAluno(Long id, String nome, String email, LocalDate dataNascimento) throws EntidadeNaoEncontradaException, ValidacaoException;

    /**
     * Deletes a student by their ID.
     *
     * @param id The ID of the student to delete.
     * @throws EntidadeNaoEncontradaException if the student to delete is not found.
     * @throws com.escola.exception.OperacaoInvalidaException if the student has active enrollments and cannot be deleted.
     */
    void deletarAluno(Long id) throws EntidadeNaoEncontradaException, OperacaoInvalidaException;
}