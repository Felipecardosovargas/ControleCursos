package com.escola.service.impl;

import com.escola.dto.AlunoDTO;
import com.escola.exception.EntidadeNaoEncontradaException;
import com.escola.exception.OperacaoInvalidaException;
import com.escola.exception.ValidacaoException;
import com.escola.model.Aluno;
import com.escola.repository.AlunoRepository;
import com.escola.repository.MatriculaRepository; // Needed to check for existing matriculas
import com.escola.repository.impl.AlunoRepositoryImpl;
import com.escola.repository.impl.MatriculaRepositoryImpl; // Example instantiation
import com.escola.service.AlunoService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link AlunoService} interface.
 * Handles business logic for student management, coordinating with the
 * {@link AlunoRepository}.
 *
 * @version 1.0
 * @author SeuNomeAqui
 */
public class AlunoServiceImpl implements AlunoService {

    private final AlunoRepository alunoRepository;
    private final MatriculaRepository matriculaRepository; // Dependency for validation

    // Basic email validation pattern
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");

    /**
     * Constructs an AlunoServiceImpl with necessary repositories.
     * In a real application with dependency injection, these would be injected.
     * For this "no frameworks" setup, we instantiate them directly or pass them.
     */
    public AlunoServiceImpl() {
        // Manual instantiation (replace with DI if a simple DI mechanism is used)
        this.alunoRepository = new AlunoRepositoryImpl();
        this.matriculaRepository = new MatriculaRepositoryImpl(); // Assuming it exists
    }

    // Constructor for injecting dependencies (better for testability)
    public AlunoServiceImpl(AlunoRepository alunoRepository, MatriculaRepository matriculaRepository) {
        this.alunoRepository = alunoRepository;
        this.matriculaRepository = matriculaRepository;
    }


    private AlunoDTO convertToDTO(Aluno aluno) {
        if (aluno == null) return null;
        return new AlunoDTO(aluno.getId(), aluno.getNome(), aluno.getEmail(), aluno.getDataNascimento());
    }

    private void validarAluno(String nome, String email, LocalDate dataNascimento) throws ValidacaoException {
        if (nome == null || nome.trim().isEmpty()) {
            throw new ValidacaoException("Nome do aluno não pode ser vazio.");
        }
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidacaoException("Email inválido.");
        }
        if (dataNascimento == null || dataNascimento.isAfter(LocalDate.now())) {
            throw new ValidacaoException("Data de nascimento inválida.");
        }
    }

    @Override
    public AlunoDTO criarAluno(String nome, String email, LocalDate dataNascimento) throws ValidacaoException {
        validarAluno(nome, email, dataNascimento);

        // Check for email uniqueness
        if (alunoRepository.buscarPorEmail(email).isPresent()) {
            throw new ValidacaoException("Email já cadastrado: " + email);
        }

        Aluno aluno = new Aluno(nome, email, dataNascimento);
        Aluno alunoSalvo = alunoRepository.salvar(aluno);
        return convertToDTO(alunoSalvo);
    }

    @Override
    public AlunoDTO buscarAlunoPorId(Long id) throws EntidadeNaoEncontradaException {
        Aluno aluno = alunoRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Aluno não encontrado com ID: " + id));
        return convertToDTO(aluno);
    }

    @Override
    public AlunoDTO buscarAlunoPorEmail(String email) throws EntidadeNaoEncontradaException {
        Aluno aluno = alunoRepository.buscarPorEmail(email)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Aluno não encontrado com email: " + email));
        return convertToDTO(aluno);
    }

    @Override
    public List<AlunoDTO> listarTodosAlunos() {
        return alunoRepository.listarTodos().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public AlunoDTO atualizarAluno(Long id, String nome, String email, LocalDate dataNascimento)
            throws EntidadeNaoEncontradaException, ValidacaoException {
        Aluno alunoExistente = alunoRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Aluno não encontrado com ID: " + id + " para atualização."));

        boolean modificado = false;

        if (nome != null && !nome.trim().isEmpty() && !nome.equals(alunoExistente.getNome())) {
            alunoExistente.setNome(nome);
            modificado = true;
        }
        if (email != null && !email.equals(alunoExistente.getEmail())) {
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                throw new ValidacaoException("Novo email inválido.");
            }
            // Check if a new email is already taken by another student
            Optional<Aluno> alunoComNovoEmail = alunoRepository.buscarPorEmail(email);
            if (alunoComNovoEmail.isPresent() && !alunoComNovoEmail.get().getId().equals(id)) {
                throw new ValidacaoException("Novo email '" + email + "' já está em uso por outro aluno.");
            }
            alunoExistente.setEmail(email);
            modificado = true;
        }
        if (dataNascimento != null && !dataNascimento.equals(alunoExistente.getDataNascimento())) {
            if (dataNascimento.isAfter(LocalDate.now())) {
                throw new ValidacaoException("Nova data de nascimento inválida.");
            }
            alunoExistente.setDataNascimento(dataNascimento);
            modificado = true;
        }

        if (modificado) {
            Aluno alunoAtualizado = alunoRepository.atualizar(alunoExistente);
            return convertToDTO(alunoAtualizado);
        }
        return convertToDTO(alunoExistente); // No changes made
    }

    @Override
    public void deletarAluno(Long id) throws EntidadeNaoEncontradaException, OperacaoInvalidaException {
        Aluno aluno = alunoRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Aluno não encontrado com ID: " + id + " para deleção."));

        // Business Rule: Check if a student has any enrollments before deleting
        // This requires a method in MatriculaRepository like: countByAlunoId(Long alunoId)
        // For simplicity, assume MatriculaRepository has a method like `findByAlunoId`
        // long countMatriculas = matriculaRepository.listarPorAlunoId(id).size(); // Example
        // if (countMatriculas > 0) {
        //    throw new OperacaoInvalidaException("Não é possível deletar aluno com ID " + id + " pois possui matrículas ativas.");
        // }
        // To fully implement this, MatriculaRepository and its methods need to be defined.
        // The cascade settings on Aluno.matriculas also play a role. If CascadeType.REMOVE
        // is used, deleting an Aluno would also delete their Matriculas. The requirement
        // should clarify this behavior. For now, we proceed with deletion.

        alunoRepository.deletarPorId(id);
    }
}