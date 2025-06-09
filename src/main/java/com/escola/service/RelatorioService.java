package com.escola.service;

import com.escola.dto.RelatorioCursoDTO;
import java.util.List;

/**
 * Service interface for generating advanced reports related to courses and enrollments.
 * This interface defines the contract for operations that provide analytical insights
 * into the educational data, rather than direct data manipulation.
 *
 * @version 1.0
 * @author FelipeCardoso
 */
public interface RelatorioService {

    /**
     * Generates a comprehensive engagement report for all courses available in the system.
     * Each entry in the returned list provides detailed statistics for a single course,
     * such as the total number of enrolled students, the average age of those students,
     * and the count of students who have enrolled within the last 30 days.
     *
     * @return A {@link List} of {@link RelatorioCursoDTO} objects, where each DTO encapsulates
     * the engagement metrics for a specific course. The list will be empty if no courses are found
     * or if no engagement data is available.
     */
    List<RelatorioCursoDTO> gerarRelatorioEngajamentoCursos();
}