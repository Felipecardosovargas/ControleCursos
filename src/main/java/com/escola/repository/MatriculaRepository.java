package com.escola.repository;

import com.escola.model.Matricula;

import java.util.List;
import java.util.Optional;

/**
 * Interface de repositório para operações com a entidade {@link Matricula}.
 * Foco em acesso a dados, sem regras de negócio.
 *
 * @version 1.1
 * @author FelipeCardoso
 */
public interface MatriculaRepository {

    /**
     * Salva uma nova matrícula no banco de dados.
     * @param matricula A entidade Matricula a ser salva.
     * @return A entidade Matricula salva (geralmente com o ID preenchido).
     */
    Matricula salvar(Matricula matricula);

    /**
     * Atualiza uma matrícula existente no banco de dados.
     * @param matricula A entidade Matricula a ser atualizada.
     * @return A entidade Matricula atualizada.
     */
    Matricula atualizar(Matricula matricula);

    /**
     * Remove uma matrícula do banco de dados com base na entidade fornecida.
     * @param matricula A entidade Matricula a ser removida.
     */
    void remover(Matricula matricula);

    /**
     * Remove uma matrícula do banco de dados pelo seu ID.
     * @param id O ID da matrícula a ser removida.
     * @return {@code true} se a matrícula foi encontrada e removida, {@code false} caso contrário.
     */
    boolean deletarPorId(Long id);

    /**
     * Busca uma matrícula pelo seu ID.
     * @param id O ID da matrícula.
     * @return Um {@link Optional} contendo a matrícula se encontrada, ou vazio caso contrário.
     */
    Optional<Matricula> buscarPorId(Long id);

    /**
     * Lista todas as matrículas existentes.
     * @return Uma lista de todas as matrículas.
     */
    List<Matricula> listarTodas();

    /**
     * Busca uma matrícula específica com base no ID do aluno e no ID do curso.
     * @param alunoId O ID do aluno.
     * @param cursoId O ID do curso.
     * @return Um {@link Optional} contendo a matrícula se encontrada, ou vazio caso contrário.
     */
    Optional<Matricula> buscarPorAlunoIdECursoId(Long alunoId, Long cursoId);

    /**
     * Conta o número de matrículas associadas a um aluno específico.
     * @param alunoId O ID do aluno.
     * @return A contagem de matrículas para o aluno.
     */
    long contarPorAlunoId(Long alunoId);

    /**
     * Conta o número de matrículas associadas a um curso específico.
     * @param cursoId O ID do curso.
     * @return A contagem de matrículas para o curso.
     */
    long contarPorCursoId(Long cursoId);

    /**
     * Lista todas as matrículas associadas a um curso específico.
     * @param cursoId O ID do curso.
     * @return Uma lista de matrículas do curso especificado.
     */
    List<Matricula> listarPorCursoId(Long cursoId);

    /**
     * Lista todas as matrículas associadas a um aluno específico.
     * @param alunoId O ID do aluno.
     * @return Uma lista de matrículas do aluno especificado.
     */
    List<Matricula> listarPorAlunoId(Long alunoId);

    /**
     * Lista todas as matrículas, incluindo detalhes (eager fetching) das entidades Aluno e Curso relacionadas.
     * @return Uma lista de matrículas com seus respectivos alunos e cursos carregados.
     */
    List<Matricula> listarTodasComDetalhes();

    /**
     * Busca uma matrícula pelo seu ID, incluindo detalhes (eager fetching) das entidades Aluno e Curso relacionadas.
     * @param id O ID da matrícula.
     * @return Um {@link Optional} contendo a matrícula com detalhes se encontrada, ou vazio caso contrário.
     */
    Optional<Matricula> buscarPorIdComDetalhes(Long id);

}