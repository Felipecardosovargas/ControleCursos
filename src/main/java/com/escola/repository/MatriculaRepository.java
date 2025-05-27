package com.escola.repository;

import com.escola.model.Matricula;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório para operações com a entidade {@link Matricula}.
 * Foco em acesso a dados, sem regras de negócio.
 */
public interface MatriculaRepository {

    /**
     * Salva uma nova matrícula.
     */
    Matricula salvar(Matricula matricula);

    /**
     * Atualiza uma matrícula existente.
     */
    Matricula atualizar(Matricula matricula);

    /**
     * Remove uma matrícula do sistema.
     */
    void remover(Matricula matricula);

    /**
     * Busca uma matrícula por ID.
     */
    Optional<Matricula> buscarPorId(Long id);

    /**
     * Lista todas as matrículas.
     */
    List<Matricula> listarTodas();

    /**
     * Busca matrícula por aluno e curso.
     */
    Optional<Matricula> buscarPorAlunoIdECursoId(Long alunoId, Long cursoId);

    /**
     * Conta o número de matrículas de um aluno.
     */
    long contarPorAlunoId(Long alunoId);

    /**
     * Conta o número de matrículas em um curso.
     */
    long contarPorCursoId(Long cursoId);

    /**
     * Lista matrículas por ID do curso.
     */
    List<Matricula> listarPorCursoId(Long cursoId);

    /**
     * Lista matrículas por ID do aluno.
     */
    List<Matricula> listarPorAlunoId(Long alunoId);

    /**
     * Deleta matrícula por ID. Retorna se a operação foi bem-sucedida.
     */
    boolean deletarPorId(Long id);

    Optional<Object> findByAlunoIdAndCursoId(Long alunoId, Long cursoId);

    List<Matricula> listarTodasComNomes();

    Optional<Object> buscarPorIdComDetalhes(Long id);
}
