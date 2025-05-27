package com.escola.service;

import com.escola.dto.CursoDTO;
import java.util.List;

/**
 * Interface que define os serviços relacionados à entidade Curso.
 * Utiliza DTOs para encapsular os dados de entrada e saída.
 */
public interface CursoService {

    /**
     * Cria um novo curso.
     *
     * @param nome Nome do curso.
     * @param descricao Descrição do curso.
     * @param cargaHoraria Carga horária do curso.
     * @return O DTO do curso criado.
     */
    CursoDTO criarCurso(String nome, String descricao, int cargaHoraria);

    /**
     * Lista todos os cursos cadastrados.
     *
     * @return Lista de DTOs de cursos.
     */
    List<CursoDTO> listarTodosCursos();

    /**
     * Busca cursos que contenham o nome informado (case-insensitive).
     *
     * @param nomeQuery Parte ou todo o nome do curso.
     * @return Lista de DTOs de cursos correspondentes.
     */
    List<CursoDTO> buscarCursosPorNomeContendo(String nomeQuery);

    /**
     * Busca um curso pelo ID.
     *
     * @param id ID do curso.
     * @return DTO do curso encontrado.
     */
    CursoDTO buscarCursoPorId(Long id);

    /**
     * Atualiza as informações de um curso.
     *
     * @param id ID do curso a ser atualizado.
     * @param nome Novo nome do curso.
     * @param descricao Nova descrição.
     * @param cargaHoraria Nova carga horária.
     * @return DTO do curso atualizado.
     */
    CursoDTO atualizarCurso(Long id, String nome, String descricao, Integer cargaHoraria);

    /**
     * Remove um curso pelo ID.
     *
     * @param id ID do curso a ser removido.
     */
    void deletarCurso(Long id);
}
