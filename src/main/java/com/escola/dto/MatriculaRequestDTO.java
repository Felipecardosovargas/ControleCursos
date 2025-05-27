package com.escola.dto;

import java.util.Objects;

/**
 * Data Transfer Object used for requesting the creation of a new
 * {@link com.escola.model.Matricula}.
 * It contains the necessary identifiers for the student and the course.
 * This class is declared as final as it is a simple data carrier.
 *
 * @version 1.0
 * @author SeuNomeAqui
 */
public final class MatriculaRequestDTO {

    private final Long alunoId;
    private final Long cursoId;
    // Could add dataMatricula here if you want to allow specifying it on creation,
    // otherwise, the service/entity can default it to LocalDate.now().

    /**
     * Constructs a MatriculaRequestDTO.
     *
     * @param alunoId The ID of the student to be enrolled.
     * @param cursoId The ID of the course for enrollment.
     */
    public MatriculaRequestDTO(Long alunoId, Long cursoId) {
        this.alunoId = alunoId;
        this.cursoId = cursoId;
    }

    // Default constructor for JSON deserialization (e.g., by Jackson)
    // Jackson can often work without it if there's a constructor with all fields
    // and parameter names match JSON properties, or with @JsonCreator.
    // For simplicity, if your JSON mapper requires it, add it.
    // public MatriculaRequestDTO() { this.alunoId = null; this.cursoId = null; }


    // Getters

    public Long getAlunoId() {
        return alunoId;
    }

    public Long getCursoId() {
        return cursoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatriculaRequestDTO that = (MatriculaRequestDTO) o;
        return Objects.equals(alunoId, that.alunoId) &&
                Objects.equals(cursoId, that.cursoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(alunoId, cursoId);
    }

    @Override
    public String toString() {
        return "MatriculaRequestDTO{" +
                "alunoId=" + alunoId +
                ", cursoId=" + cursoId +
                '}';
    }
}