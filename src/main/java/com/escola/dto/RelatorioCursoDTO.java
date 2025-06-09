package com.escola.dto;

import java.util.Objects;

/**
 * DTO que representa dados agregados de engajamento de um curso.
 * Inclui total de alunos, média de idade e novos alunos nos últimos 30 dias.
 * Imutável e seguro para uso em múltiplas threads.
 *
 * @version 1.1
 * @author FelipeCardoso
 */
public final class RelatorioCursoDTO {

    private final String cursoNome;
    private final long totalAlunosMatriculados;
    private final double mediaIdadeAlunos;
    private final long novosAlunosUltimos30Dias;

    /**
     * Construtor principal.
     *
     * @param cursoNome                 Nome do curso.
     * @param totalAlunosMatriculados  Total de alunos matriculados no curso.
     * @param mediaIdadeAlunos         Média de idade dos alunos.
     * @param novosAlunosUltimos30Dias Alunos matriculados nos últimos 30 dias.
     */
    public RelatorioCursoDTO(String cursoNome,
                             long totalAlunosMatriculados,
                             double mediaIdadeAlunos,
                             long novosAlunosUltimos30Dias) {
        this.cursoNome = cursoNome;
        this.totalAlunosMatriculados = totalAlunosMatriculados;
        this.mediaIdadeAlunos = mediaIdadeAlunos;
        this.novosAlunosUltimos30Dias = novosAlunosUltimos30Dias;
    }

    // Getters apenas (imutável)
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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof RelatorioCursoDTO other)) return false;
        return totalAlunosMatriculados == other.totalAlunosMatriculados &&
                Double.compare(mediaIdadeAlunos, other.mediaIdadeAlunos) == 0 &&
                novosAlunosUltimos30Dias == other.novosAlunosUltimos30Dias &&
                Objects.equals(cursoNome, other.cursoNome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cursoNome, totalAlunosMatriculados, mediaIdadeAlunos, novosAlunosUltimos30Dias);
    }

    @Override
    public String toString() {
        return String.format(
                "RelatorioCursoDTO[curso='%s', totalMatriculas=%d, mediaIdade=%.2f, novosUltimos30Dias=%d]",
                cursoNome, totalAlunosMatriculados, mediaIdadeAlunos, novosAlunosUltimos30Dias
        );
    }
}
