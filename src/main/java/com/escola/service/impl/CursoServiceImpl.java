package com.escola.service.impl;

import com.escola.dto.CursoDTO;
import com.escola.exception.EntidadeNaoEncontradaException;
import com.escola.model.Curso;
import com.escola.repository.CursoRepository;
import com.escola.service.CursoService;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Objects; // Used for Objects.requireNonNull

/**
 * Implementation of the {@link CursoService} interface.
 * This class provides the business logic for course-related operations,
 * acting as an intermediary between the controllers (or presentation layer)
 * and the data access layer (repository).
 *
 * @version 1.1
 * @author FelipeCardoso
 */
public final class CursoServiceImpl implements CursoService {

    private final CursoRepository cursoRepository;

    /**
     * Constructs a new CursoServiceImpl with the provided CursoRepository.
     * This constructor is designed for dependency injection, making the service
     * easily testable and promoting loose coupling.
     *
     * @param cursoRepository The repository responsible for Curso data access. Must not be null.
     * @throws NullPointerException if the provided cursoRepository is null.
     */
    public CursoServiceImpl(CursoRepository cursoRepository) {
        // Ensures that the injected repository is not null, providing a fail-fast mechanism.
        this.cursoRepository = Objects.requireNonNull(cursoRepository, "CursoRepository cannot be null.");
    }

    /**
     * Converts a {@link Curso} entity to a {@link CursoDTO}.
     * This private helper method ensures that data transferred to the presentation layer
     * is in the appropriate DTO format.
     *
     * @param curso The Curso entity to convert.
     * @return A CursoDTO representation of the entity, or null if the input Curso is null.
     */
    private CursoDTO toDTO(Curso curso) {
        if (curso == null) return null;
        return new CursoDTO(curso.getId(), curso.getNome(), curso.getDescricao(), curso.getCargaHoraria());
    }

    /**
     * Converts input parameters into a new {@link Curso} entity.
     * This method is used for creating new Curso instances from raw input data.
     *
     * @param nome The name, of course.
     * @param descricao The description of the course.
     * @param cargaHoraria The workload of the course.
     * @return A new Curso entity populated with the provided data.
     */
    private Curso toEntity(String nome, String descricao, int cargaHoraria) {
        Curso curso = new Curso();
        curso.setNome(nome);
        curso.setDescricao(descricao);
        curso.setCargaHoraria(cargaHoraria);
        return curso;
    }

    /**
     * Converts input parameters into a new or partial {@link Curso} entity.
     * This method is generally used for updates where the ID is known, and
     * some fields might be optional (null).
     *
     * @param id The ID, of course.
     * @param nome The name of the course (can be null if not being updated).
     * @param descricao The description of the course (can be null if not being updated).
     * @param cargaHoraria The workload of the course (can be null if not being updated).
     * @return A new Curso entity populated with the provided data.
     */
    private Curso toEntity(Long id, String nome, String descricao, Integer cargaHoraria) {
        Curso curso = new Curso();
        curso.setId(id);
        curso.setNome(nome);
        curso.setDescricao(descricao);
        // Only set cargaHoraria if it's not null, allowing partial updates
        if (cargaHoraria != null) {
            curso.setCargaHoraria(cargaHoraria);
        }
        return curso;
    }

    /**
     * Creates a new course based on the provided details.
     * The method converts input parameters to a {@link Curso} entity, saves it via the repository,
     * and then converts the saved entity back to a {@link CursoDTO} for return.
     *
     * @param nome The name of the course to be created.
     * @param descricao The description of the course.
     * @param cargaHoraria The workload of the course.
     * @return A {@link CursoDTO} representing the newly created course.
     */
    @Override
    public CursoDTO criarCurso(String nome, String descricao, int cargaHoraria) {
        Curso curso = toEntity(nome, descricao, cargaHoraria);
        Curso salvo = cursoRepository.salvar(curso);
        return toDTO(salvo);
    }

    /**
     * Retrieves a list of all courses currently registered in the system.
     * It fetches all {@link Curso} entities from the repository and converts them
     * into a list of {@link CursoDTO}s.
     *
     * @return A {@link List} of {@link CursoDTO}s representing all registered courses.
     */
    @Override
    public List<CursoDTO> listarTodosCursos() {
        List<Curso> cursos = cursoRepository.listarTodos();
        // Uses Java Streams to efficiently convert each Curso entity to a CursoDTO.
        return cursos.stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Searches for courses whose names contain the specified query string (case-insensitive).
     * This method leverages the repository to perform the name-based search and
     * then converts the found entities into DTOs.
     *
     * @param nomeQuery The full or partial name string to search for within course names.
     * @return A {@link List} of {@link CursoDTO}s that match the search criterion.
     * Returns an empty list if no matching courses are found.
     */
    @Override
    public List<CursoDTO> buscarCursosPorNomeContendo(String nomeQuery) {
        List<Curso> cursos = cursoRepository.buscarPorNomeContendo(nomeQuery);
        // Converts the list of Curso entities to a list of CursoDTOs.
        return cursos.stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Retrieves a specific course by its unique identifier.
     * If no course is found with the given ID, an {@link EntidadeNaoEncontradaException} is thrown.
     *
     * @param id The unique ID of the course to retrieve.
     * @return A {@link CursoDTO} representing the found course.
     * @throws EntidadeNaoEncontradaException If a course with the specified ID does not exist.
     */
    @Override
    public CursoDTO buscarCursoPorId(Long id) {
        // Attempts to find the course by ID; if not present, throws an exception.
        Curso curso = cursoRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Curso com ID " + id + " não encontrado."));
        return toDTO(curso);
    }

    /**
     * Updates the information of an existing course.
     * The method first retrieves the existing course by its ID. If found, it updates
     * its properties with the provided non-null values. Finally, it saves the updated
     * entity via the repository.
     *
     * @param id The unique ID of the course to be updated.
     * @param nome The new name for the course. If null, the name is not updated.
     * @param descricao The new description for the course. If null, the description is not updated.
     * @param cargaHoraria The new workload for the course. If null, the workload is not updated.
     * @return A {@link CursoDTO} representing the updated course.
     * @throws EntidadeNaoEncontradaException If the course with the specified ID does not exist.
     */
    @Override
    public CursoDTO atualizarCurso(Long id, String nome, String descricao, Integer cargaHoraria) {
        // Retrieves the existing course; throws an exception if not found.
        Curso cursoExistente = cursoRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Curso com ID " + id + " não encontrado."));

        // Updates only the fields that are provided (not null).
        if (nome != null) cursoExistente.setNome(nome);
        if (descricao != null) cursoExistente.setDescricao(descricao);
        if (cargaHoraria != null) cursoExistente.setCargaHoraria(cargaHoraria);

        Curso atualizado = cursoRepository.atualizar(cursoExistente);
        return toDTO(atualizado);
    }

    /**
     * Deletes a course from the system by its unique identifier.
     * Before deletion, it verifies the existence of the course to ensure a valid operation.
     *
     * @param id The unique ID of the course to be deleted.
     * @throws EntidadeNaoEncontradaException If the course with the specified ID does not exist.
     */
    @Override
    public void deletarCurso(Long id) {
        // Verifies if the course exists before attempting to delete it.
        Curso cursoExistente = cursoRepository.buscarPorId(id)
                .orElseThrow(() -> new EntidadeNaoEncontradaException("Curso com ID " + id + " não encontrado."));
        cursoRepository.deletarPorId(id);
    }
}