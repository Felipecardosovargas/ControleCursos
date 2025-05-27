
package com.escola.dto;

import com.escola.model.Matricula;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Data Transfer Object for {@link com.escola.model.Matricula}.
 * Used to transfer enrollment data, often including denormalized information
 * like student and course names for ease of display.
 * This class is declared as final as it is a simple data carrier.
 *
 * @version 1.0
 * @author SeuNomeAqui
 */
public final class MatriculaDTO {

    private final Long id;
    private final String alunoNome; // Denormalized data
    private final Long alunoId;
    private final String cursoNome; // Denormalized data
    private final Long cursoId;
    private final LocalDate dataMatricula;

    /**
     * Constructs a MatriculaDTO.
     *
     * @param id The unique identifier of the enrollment.
     * @param alunoId The ID of the enrolled student.
     * @param alunoNome The name of the enrolled student.
     * @param cursoId The ID of the course.
     * @param cursoNome The name of the course.
     * @param dataMatricula The date of enrollment.
     */
    public MatriculaDTO(Long id, Long alunoId, String alunoNome, Long cursoId, String cursoNome, LocalDate dataMatricula) {
        this.id = id;
        this.alunoId = alunoId;
        this.alunoNome = alunoNome;
        this.cursoId = cursoId;
        this.cursoNome = cursoNome;
        this.dataMatricula = dataMatricula;
    }

    public MatriculaDTO(Matricula matricula) {
        this.id = matricula.getId();
        this.alunoId = matricula.getAluno().getId();
        this.alunoNome = matricula.getAluno().getNome();
        this.cursoId = matricula.getCurso().getId();
        this.cursoNome = matricula.getCurso().getNome();
        this.dataMatricula = matricula.getDataMatricula();
    }

    // Getters

    public Long getId() {
        return id;
    }

    public String getAlunoNome() {
        return alunoNome;
    }

    public Long getAlunoId() {
        return alunoId;
    }

    public String getCursoNome() {
        return cursoNome;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public LocalDate getDataMatricula() {
        return dataMatricula;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatriculaDTO that = (MatriculaDTO) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(alunoId, that.alunoId) &&
                Objects.equals(alunoNome, that.alunoNome) &&
                Objects.equals(cursoId, that.cursoId) &&
                Objects.equals(cursoNome, that.cursoNome) &&
                Objects.equals(dataMatricula, that.dataMatricula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, alunoId, alunoNome, cursoId, cursoNome, dataMatricula);
    }

    @Override
    public String toString() {
        return "MatriculaDTO{" +
                "id=" + id +
                ", alunoId=" + alunoId +
                ", alunoNome='" + alunoNome + '\'' +
                ", cursoId=" + cursoId +
                ", cursoNome='" + cursoNome + '\'' +
                ", dataMatricula=" + dataMatricula +
                '}';
    }
}