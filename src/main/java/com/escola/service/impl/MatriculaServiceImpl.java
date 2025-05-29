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

/**
 * Implementação do serviço de matrícula.
 * Responsável por gerenciar operações relacionadas à entidade {@link Matricula}.
 *
 * @author Sistema
 */
public final class MatriculaServiceImpl implements MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final AlunoRepository alunoRepository;
    private final CursoRepository cursoRepository;

    /**
     * Construtor com injeção de dependência.
     *
     * @param matriculaRepository Repositório de matrículas
     * @param alunoRepository     Repositório de alunos
     * @param cursoRepository     Repositório de cursos
     */
    public MatriculaServiceImpl(MatriculaRepository matriculaRepository,
                                AlunoRepository alunoRepository,
                                CursoRepository cursoRepository) {
        this.matriculaRepository = matriculaRepository;
        this.alunoRepository = alunoRepository;
        this.cursoRepository = cursoRepository;
    }

    /**
     * Realiza a matrícula de um aluno em um curso.
     *
     * @param requestDTO Dados da matrícula
     * @return DTO da matrícula criada
     * @throws EntidadeNaoEncontradaException Caso o aluno ou curso não sejam encontrados
     * @throws OperacaoInvalidaException      Caso o aluno já esteja matriculado no curso
     */
    @Override
    public MatriculaDTO realizarMatricula(MatriculaRequestDTO requestDTO)
            throws EntidadeNaoEncontradaException, OperacaoInvalidaException {

        Aluno aluno = buscarAlunoPorId(requestDTO.getAlunoId());
        Curso curso = buscarCursoPorId(requestDTO.getCursoId());

        verificarDuplicidadeDeMatricula(aluno.getId(), curso.getId());

        Matricula novaMatricula = new Matricula(aluno, curso);
        novaMatricula.setDataMatricula(LocalDate.now());

        Matricula matriculaSalva = matriculaRepository.salvar(novaMatricula);
        return toDTO(matriculaSalva);
    }

    /**
     * Lista todas as matrículas com dados detalhados de aluno e curso.
     *
     * @return Lista de DTOs de matrículas
     */
    @Override
    public List<MatriculaDTO> listarTodasMatriculasComDetalhes() {
        return matriculaRepository.listarTodasComDetalhes().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Busca matrícula por ID com detalhes.
     *
     * @param id ID da matrícula
     * @return DTO da matrícula encontrada
     * @throws EntidadeNaoEncontradaException Caso a matrícula não seja encontrada
     */
    @Override
    public MatriculaDTO buscarMatriculaPorIdComDetalhes(Long id) throws EntidadeNaoEncontradaException {
        Matricula matricula = matriculaRepository.buscarPorIdComDetalhes(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Matrícula com ID " + id + " não encontrada."));
        return toDTO(matricula);
    }

    /**
     * Atualiza uma matrícula existente.
     *
     * @param input Dados atualizados da matrícula
     * @return DTO da matrícula atualizada
     * @throws EntidadeNaoEncontradaException Caso a matrícula, aluno ou curso não existam
     * @throws OperacaoInvalidaException      Caso falte algum dado obrigatório
     */
    @Override
    public MatriculaDTO atualizar(Matricula input)
            throws EntidadeNaoEncontradaException, OperacaoInvalidaException {

        validarDadosAtualizacao(input);

        Matricula existente = matriculaRepository.buscarPorId(input.getId())
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Matrícula com ID " + input.getId() + " não encontrada."));

        Aluno aluno = buscarAlunoPorId(input.getAluno().getId());
        Curso curso = buscarCursoPorId(input.getCurso().getId());

        existente.setAluno(aluno);
        existente.setCurso(curso);
        existente.setDataMatricula(input.getDataMatricula());

        Matricula atualizada = matriculaRepository.atualizar(existente);
        return toDTO(atualizada);
    }

    /**
     * Cancela uma matrícula existente.
     *
     * @param id ID da matrícula
     * @throws EntidadeNaoEncontradaException Caso a matrícula não seja encontrada
     * @throws OperacaoInvalidaException      Caso a matrícula já esteja cancelada
     */
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

    /**
     * Remove uma matrícula do sistema.
     *
     * @param id ID da matrícula
     * @throws EntidadeNaoEncontradaException Caso a matrícula não seja encontrada
     */
    @Override
    public void remover(Long id) throws EntidadeNaoEncontradaException {
        Matricula existente = matriculaRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Matrícula com ID " + id + " não encontrada."));
        matriculaRepository.remover(existente);
    }

    /**
     * Lista todas as matrículas com nomes de aluno e curso.
     *
     * @return Lista de matrículas
     */
    @Override
    public List<Matricula> listarTodasMatriculasComNomes() {
        return matriculaRepository.listarTodasComDetalhes();
    }

    /**
     * Busca matrícula por ID (sem detalhes).
     *
     * @param id ID da matrícula
     * @return Optional com a matrícula
     */
    @Override
    public Optional<Matricula> buscarPorId(Long id) {
        return matriculaRepository.buscarPorId(id);
    }

    // ===================== Métodos auxiliares ===================== //

    private void verificarDuplicidadeDeMatricula(Long alunoId, Long cursoId) throws OperacaoInvalidaException {
        if (matriculaRepository.buscarPorAlunoIdECursoId(alunoId, cursoId).isPresent()) {
            throw new OperacaoInvalidaException("O aluno já está matriculado neste curso.");
        }
    }

    private Aluno buscarAlunoPorId(Long alunoId) throws EntidadeNaoEncontradaException {
        return alunoRepository.buscarPorId(alunoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Aluno com ID " + alunoId + " não encontrado."));
    }

    private Curso buscarCursoPorId(Long cursoId) throws EntidadeNaoEncontradaException {
        return cursoRepository.buscarPorId(cursoId)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Curso com ID " + cursoId + " não encontrado."));
    }

    private void validarDadosAtualizacao(Matricula matricula) throws OperacaoInvalidaException {
        if (matricula.getId() == null) {
            throw new OperacaoInvalidaException("ID da Matrícula é obrigatório para atualização.");
        }
        if (matricula.getAluno() == null || matricula.getAluno().getId() == null) {
            throw new OperacaoInvalidaException("Dados do Aluno (com ID) são obrigatórios para atualização.");
        }
        if (matricula.getCurso() == null || matricula.getCurso().getId() == null) {
            throw new OperacaoInvalidaException("Dados do Curso (com ID) são obrigatórios para atualização.");
        }
    }

    /**
     * Converte entidade {@link Matricula} em {@link MatriculaDTO}.
     *
     * @param matricula A entidade de matrícula
     * @return DTO da matrícula
     */
    private MatriculaDTO toDTO(Matricula matricula) {
        if (matricula == null) return null;

        return new MatriculaDTO(
                matricula.getId(),
                Optional.ofNullable(matricula.getAluno()).map(Aluno::getId).orElse(null),
                Optional.ofNullable(matricula.getAluno()).map(Aluno::getNome).orElse(null),
                Optional.ofNullable(matricula.getCurso()).map(Curso::getId).orElse(null),
                Optional.ofNullable(matricula.getCurso()).map(Curso::getNome).orElse(null),
                matricula.getDataMatricula()
        );
    }
}
