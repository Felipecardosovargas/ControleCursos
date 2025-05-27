package com.escola.service;

import com.escola.dto.RelatorioCursoDTO;
import java.util.List;

/**
 * Service interface for generating advanced reports.
 *
 * @version 1.0
 * @author SeuNomeAqui
 */
public interface RelatorioService {

    /**
     * Generates an engagement report for all courses.
     * Each entry in the list will contain details for one course, including
     * total enrolled students, average student age, and students enrolled in the last 30 days.
     *
     * @return A list of {@link RelatorioCursoDTO} objects.
     */
    List<RelatorioCursoDTO> gerarRelatorioEngajamentoCursos();
}