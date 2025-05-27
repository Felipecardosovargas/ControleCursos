package com.escola.service.impl;

import com.escola.dto.MatriculaDTO;
import com.escola.dto.MatriculaRequestDTO;
import com.escola.exception.EntidadeNaoEncontradaException;
import com.escola.exception.OperacaoInvalidaException;
import com.escola.model.Aluno;
import com.escola.model.Curso;
import com.escola.model.Matricula;
import com.escola.repository.AlunoRepository;
import com.escola.repository.CursoRepository;
import com.escola.repository.MatriculaRepository;
import com.escola.service.MatriculaService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public final class MatriculaServiceImpl implements MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlunoRepository alunoRepository;
    private final CursoRepository cursoRepository;

    public MatriculaServiceImpl(MatriculaRepository matriculaRepository,
                                AlunoRepository alunoRepository,
                                CursoRepository cursoRepository) {
        this.matriculaRepository = matriculaRepository;
        this.alunoRepository = alunoRepository;
        this.cursoRepository = cursoRepository;
    }

    @Override
    public MatriculaDTO realizarMatricula(MatriculaRequestDTO matriculaRequestDTO)
            throws EntidadeNaoEncontradaException, OperacaoInvalidaException {

        Long alunoId = matriculaRequestDTO.getAlunoId();
        Long cursoId = matriculaRequestDTO.getCursoId();

        Aluno aluno = alunoRepository.buscarPorId(alunoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Aluno com ID " + alunoId + " não encontrado."));
        Curso curso = cursoRepository.buscarPorId(cursoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Curso com ID " + cursoId + " não encontrado."));

        if (matriculaRepository.findByAlunoIdAndCursoId(alunoId, cursoId).isPresent()) {
            throw new OperacaoInvalidaException("O aluno já está matriculado neste curso.");
        }

        Matricula matricula = new Matricula(aluno, curso);
        matricula.setDataMatricula(LocalDate.now());

        Matricula matriculaSalva = matriculaRepository.salvar(matricula);
        return convertToDto(matriculaSalva);
    }

    @Override
    public List<Matricula> listarTodasMatriculasComNomes() {
        return matriculaRepository.listarTodasComNomes();
    }

    @Override
    public Optional<Matricula> buscarPorId(Long id) {
        return matriculaRepository.buscarPorId(id);
    }

    @Override
    public MatriculaDTO atualizar(Matricula matriculaInput)
            throws EntidadeNaoEncontradaException, OperacaoInvalidaException {

        if (matriculaInput.getId() == null) {
            throw new OperacaoInvalidaException("ID da Matrícula é obrigatório para atualização.");
        }
        if (matriculaInput.getAluno() == null || matriculaInput.getAluno().getId() == null) {
            throw new OperacaoInvalidaException("Dados do Aluno (com ID) são obrigatórios para atualizar a matrícula.");
        }
        if (matriculaInput.getCurso() == null || matriculaInput.getCurso().getId() == null) {
            throw new OperacaoInvalidaException("Dados do Curso (com ID) são obrigatórios para atualizar a matrícula.");
        }

        Matricula existente = matriculaRepository.buscarPorId(matriculaInput.getId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Matrícula com ID " + matriculaInput.getId() + " não encontrada."));

        Aluno alunoAtualizado = alunoRepository.buscarPorId(matriculaInput.getAluno().getId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Aluno com ID " + matriculaInput.getAluno().getId() + " não encontrado."));
        Curso cursoAtualizado = cursoRepository.buscarPorId(matriculaInput.getCurso().getId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Curso com ID " + matriculaInput.getCurso().getId() + " não encontrado."));

        existente.setAluno(alunoAtualizado);
        existente.setCurso(cursoAtualizado);
        existente.setDataMatricula(matriculaInput.getDataMatricula());

        Matricula matriculaAtualizada = matriculaRepository.atualizar(existente);
        return convertToDto(matriculaAtualizada);
    }

    @Override
    public void remover(Long id) throws EntidadeNaoEncontradaException, OperacaoInvalidaException {
        Matricula existente = matriculaRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Matrícula com ID " + id + " não encontrada."));
        matriculaRepository.remover(existente);
    }

    @Override
    public List<MatriculaDTO> listarTodasMatriculasComDetalhes() {
        return matriculaRepository.listarTodas().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public MatriculaDTO buscarMatriculaPorIdComDetalhes(Long id) throws EntidadeNaoEncontradaException {
        Matricula matricula = (Matricula) matriculaRepository.buscarPorIdComDetalhes(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Matrícula com ID " + id + " não encontrada."));
        return convertToDto(matricula);
    }

    @Override
    public void cancelarMatricula(Long id) throws EntidadeNaoEncontradaException, OperacaoInvalidaException {
        Matricula matricula = matriculaRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Matrícula com ID " + id + " não encontrada."));
        if (matricula.isCancelada()) {
            throw new OperacaoInvalidaException("A matrícula já está cancelada.");
        }
        matricula.setCancelada(true);
        matriculaRepository.atualizar(matricula);
    }

    private MatriculaDTO convertToDto(Matricula matricula) {
        if (matricula == null) {
            return null;
        }
        Long alunoId = (matricula.getAluno() != null) ? matricula.getAluno().getId() : null;
        String alunoNome = (matricula.getAluno() != null) ? matricula.getAluno().getNome() : null;
        Long cursoId = (matricula.getCurso() != null) ? matricula.getCurso().getId() : null;
        String cursoNome = (matricula.getCurso() != null) ? matricula.getCurso().getNome() : null;
        LocalDate dataMatricula = matricula.getDataMatricula();

        return new MatriculaDTO(
                matricula.getId(),
                alunoId,
                alunoNome,
                cursoId,
                cursoNome,
                dataMatricula
        );
    }
}
