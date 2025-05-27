package com.escola.dto;

import java.util.Objects;

/**
 * Data Transfer Object for representing aggregated engagement report data for a course.
 * This includes total enrolled students, average student age, and the number of
 * students enrolled in a recent period (e.g., last 30 days).
 * This class is declared as final as it is a simple data carrier.
 *
 * @version 1.0
 * @author SeuNomeAqui
 */
public final class RelatorioCursoDTO {

    private final String cursoNome;
    private final long totalAlunosMatriculados;
    private final double mediaIdadeAlunos; // Using double for average
    private final long novosAlunosUltimos30Dias;

    /**
     * Constructs a RelatorioCursoDTO.
     *
     * @param cursoNome The name of the course.
     * @param totalAlunosMatriculados The total number of students enrolled in the course.
     * @param mediaIdadeAlunos The average age of students enrolled in the course.
     * @param novosAlunosUltimos30Dias The number of students enrolled in the course in the last 30 days.
     */
    public RelatorioCursoDTO(String cursoNome, long totalAlunosMatriculados, double mediaIdadeAlunos, long novosAlunosUltimos30Dias) {
        this.cursoNome = cursoNome;
        this.totalAlunosMatriculados = totalAlunosMatriculados;
        this.mediaIdadeAlunos = mediaIdadeAlunos;
        this.novosAlunosUltimos30Dias = novosAlunosUltimos30Dias;
    }

    // Getters

    public String getCursoNome() {
        return cursoNome;
    }

    public long getTotalAlunosMatriculados() {
        return totalAlunosMatriculados;
    }

    public double getMediaIdadeAlunos() {
        return mediaIdadeAlunos;
    }

    public long getNovosAlunosUltimos30Dias() {
        return novosAlunosUltimos30Dias;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RelatorioCursoDTO that = (RelatorioCursoDTO) o;
        return totalAlunosMatriculados == that.totalAlunosMatriculados &&
                Double.compare(that.mediaIdadeAlunos, mediaIdadeAlunos) == 0 &&
                novosAlunosUltimos30Dias == that.novosAlunosUltimos30Dias &&
                Objects.equals(cursoNome, that.cursoNome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cursoNome, totalAlunosMatriculados, mediaIdadeAlunos, novosAlunosUltimos30Dias);
    }

    @Override
    public String toString() {
        return "RelatorioCursoDTO{" +
                "cursoNome='" + cursoNome + '\'' +
                ", totalAlunosMatriculados=" + totalAlunosMatriculados +
                ", mediaIdadeAlunos=" + String.format("%.2f", mediaIdadeAlunos) + // Formatting for display
                ", novosAlunosUltimos30Dias=" + novosAlunosUltimos30Dias +
                '}';
    }
}