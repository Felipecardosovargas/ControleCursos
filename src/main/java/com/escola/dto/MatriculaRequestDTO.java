package com.escola.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO usado para realizar requisições de matrícula.
 * Contém apenas os IDs necessários para processar a matrícula.
 * <p>
 * Este objeto é usado principalmente em requisições HTTP para criar novas matrículas.
 *
 * @author SeuNome
 * @version 1.0
 */
public class MatriculaRequestDTO implements Serializable {

    private Long alunoId;
    private Long cursoId;

    /**
     * Construtor padrão necessário para desserialização.
     */
    public MatriculaRequestDTO() {
    }

    /**
     * Construtor completo.
     *
     * @param alunoId ID do aluno.
     * @param cursoId ID do curso.
     */
    public MatriculaRequestDTO(Long alunoId, Long cursoId) {
        this.alunoId = alunoId;
        this.cursoId = cursoId;
    }

    public Long getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(Long alunoId) {
        this.alunoId = alunoId;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatriculaRequestDTO)) return false;
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
