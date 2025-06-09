package com.escola.service.impl;

import com.escola.dto.RelatorioCursoDTO;
import com.escola.model.Curso;
import com.escola.model.Matricula;
import com.escola.repository.CursoRepository;
import com.escola.repository.MatriculaRepository;
import com.escola.repository.impl.CursoRepositoryImpl;
import com.escola.repository.impl.MatriculaRepositoryImpl;
import com.escola.service.RelatorioService;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the RelatorioService interface.
 * This class provides business logic for generating various reports related to courses and enrollments,
 * interacting with the repository layer to fetch necessary data.
 * It aims to provide insights into course engagement, such as total students,
 * average age, and recent enrollments.
 *
 * @version 1.1
 * @author FelipeCardoso
 */
public class RelatorioServiceImpl implements RelatorioService {

    private final MatriculaRepository matriculaRepository;
    private final CursoRepository cursoRepository;

    /**
     * Constructs a new RelatorioServiceImpl with specified repository implementations.
     * This constructor is primarily used for dependency injection, allowing for
     * easier testing and more flexible application setup.
     *
     * @param matriculaRepository The repository for managing enrollment data.
     * @param cursoRepository     The repository for managing course data.
     */
    public RelatorioServiceImpl(MatriculaRepository matriculaRepository, CursoRepository cursoRepository) {
        this.matriculaRepository = matriculaRepository;
        this.cursoRepository = cursoRepository;
    }

    /**
     * Default constructor for RelatorioServiceImpl.
     * It initializes the repositories with their default concrete implementations.
     * This constructor is useful for simple standalone applications where
     * manual dependency injection might be less common.
     */
    public RelatorioServiceImpl() {
        this.matriculaRepository = new MatriculaRepositoryImpl();
        this.cursoRepository = new CursoRepositoryImpl();
    }

    /**
     * Constructs a new RelatorioServiceImpl, specifically allowing injection of
     * a MatriculaRepositoryImpl instance while using the default CursoRepositoryImpl.
     *
     * @param matriculaRepository The specific MatriculaRepositoryImpl instance to use.
     */
    public RelatorioServiceImpl(MatriculaRepositoryImpl matriculaRepository) {
        this.matriculaRepository = matriculaRepository;
        this.cursoRepository = new CursoRepositoryImpl();
    }

    /**
     * Generates a comprehensive report on course engagement.
     * For each available course, this method calculates the total number of enrolled students,
     * their average age, and the count of students who enrolled in the last 30 days.
     *
     * @return A {@link List} of {@link RelatorioCursoDTO}, each containing engagement
     * statistics for a specific course. Returns an empty list if no courses are found.
     */
    @Override
    public List<RelatorioCursoDTO> gerarRelatorioEngajamentoCursos() {
        List<RelatorioCursoDTO> relatorios = new ArrayList<>();
        List<Curso> cursos = cursoRepository.listarTodos();

        for (Curso curso : cursos) {
            List<Matricula> matriculas = matriculaRepository.listarPorCursoId(curso.getId());
            long totalMatriculados = matriculas.size();
            double mediaIdade = calcularMediaIdade(matriculas);
            long novosAlunos = contarNovosAlunosNosUltimosDias(matriculas, 30);

            RelatorioCursoDTO relatorio = new RelatorioCursoDTO(curso.getNome(), totalMatriculados, mediaIdade, novosAlunos);
            relatorios.add(relatorio);
        }
        return relatorios;
    }

    /**
     * Calculates the average age of students based on a provided list of enrollments.
     * It extracts the associated student from each enrollment and computes their age.
     *
     * @param matriculas The list of {@link Matricula} objects (enrollments) to use for age calculation.
     * @return The average age of students in years, or 0.0 if the input list is empty.
     */
    private double calcularMediaIdade(List<Matricula> matriculas) {
        if (matriculas.isEmpty()) return 0.0;

        double somaIdades = matriculas.stream()
                .map(Matricula::getAluno)
                .mapToInt(this::calcularIdade)
                .sum();

        return somaIdades / matriculas.size();
    }

    /**
     * Calculates the age of a student based on their birthdate.
     * The age is determined by the period between the student's birthdate and the current date.
     *
     * @param aluno The {@link com.escola.model.Aluno} object containing the birthdate.
     * @return The age of the student in years, or 0 if the birthdate is null.
     */
    private int calcularIdade(com.escola.model.Aluno aluno) {
        LocalDate nascimento = aluno.getDataNascimento();
        if (nascimento == null) return 0;

        return Period.between(nascimento, LocalDate.now()).getYears();
    }

    /**
     * Counts the number of students who enrolled within a specified number of recent days.
     * It filters enrollments to include only those whose enrollment date falls within the defined period
     * relative to the current date.
     *
     * @param matriculas The list of {@link Matricula} objects (enrollments) to check.
     * @param dias The number of past days to consider an enrollment as "new".
     * @return The count of new students enrolled within the specified period.
     */
    private long contarNovosAlunosNosUltimosDias(List<Matricula> matriculas, int dias) {
        LocalDate dataLimite = LocalDate.now().minusDays(dias);
        return matriculas.stream()
                .filter(m -> m.getDataMatricula() != null && m.getDataMatricula().isAfter(dataLimite))
                .count();
    }
}