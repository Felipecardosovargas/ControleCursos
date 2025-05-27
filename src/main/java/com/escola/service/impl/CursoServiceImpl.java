package com.escola.service.impl;

import com.escola.dto.CursoDTO;
import com.escola.exception.EntidadeNaoEncontradaException;
import com.escola.model.Curso;
import com.escola.repository.CursoRepository;
import com.escola.service.CursoService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementação da interface {@link CursoService}.
 * Fornece a lógica de negócios para operações relacionadas a cursos.
 */
public final class CursoServiceImpl implements CursoService {

    private final CursoRepository cursoRepository;

    public CursoServiceImpl(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    private CursoDTO toDTO(Curso curso) {
        if (curso == null) return null;
        return new CursoDTO(curso.getId(), curso.getNome(), curso.getDescricao(), curso.getCargaHoraria());
    }

    private Curso toEntity(String nome, String descricao, int cargaHoraria) {
        Curso curso = new Curso();
        curso.setNome(nome);
        curso.setDescricao(descricao);
        curso.setCargaHoraria(cargaHoraria);
        return curso;
    }

    private Curso toEntity(Long id, String nome, String descricao, Integer cargaHoraria) {
        Curso curso = new Curso();
        curso.setId(id);
        curso.setNome(nome);
        curso.setDescricao(descricao);
        if (cargaHoraria != null) {
            curso.setCargaHoraria(cargaHoraria);
        }
        return curso;
    }

    @Override
    public CursoDTO criarCurso(String nome, String descricao, int cargaHoraria) {
        Curso curso = toEntity(nome, descricao, cargaHoraria);
        Curso salvo = cursoRepository.salvar(curso);
        return toDTO(salvo);
    }

    @Override
    public List<CursoDTO> listarTodosCursos() {
        List<Curso> cursos = cursoRepository.listarTodos();
        return cursos.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<CursoDTO> buscarCursosPorNomeContendo(String nomeQuery) {
        List<Curso> cursos = cursoRepository.buscarPorNomeContendo(nomeQuery);
        return cursos.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public CursoDTO buscarCursoPorId(Long id) {
        Curso curso = cursoRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Curso com ID " + id + " não encontrado."));
        return toDTO(curso);
    }

    @Override
    public CursoDTO atualizarCurso(Long id, String nome, String descricao, Integer cargaHoraria) {
        Curso cursoExistente = cursoRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Curso com ID " + id + " não encontrado."));

        if (nome != null) cursoExistente.setNome(nome);
        if (descricao != null) cursoExistente.setDescricao(descricao);
        if (cargaHoraria != null) cursoExistente.setCargaHoraria(cargaHoraria);

        Curso atualizado = cursoRepository.atualizar(cursoExistente);
        return toDTO(atualizado);
    }

    @Override
    public void deletarCurso(Long id) {
        Curso cursoExistente = cursoRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Curso com ID " + id + " não encontrado."));
        cursoRepository.deletarPorId(id);
    }
}
