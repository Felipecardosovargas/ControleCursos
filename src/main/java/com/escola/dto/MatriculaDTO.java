package com.escola.dto;

import com.escola.model.Matricula;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) representing an Enrollment (Matricula).
 *
 * <p>
 * Este DTO encapsula os dados de uma matrícula entre aluno e curso,
 * facilitando o transporte de dados entre as camadas da aplicação
 * sem expor diretamente as entidades do domínio.
 * </p>
 *
 * <p>
 * Contém dados desnormalizados como nome do aluno e nome do curso para facilitar
 * exibição em camadas de apresentação.
 * </p>
 *
 * <p><b>Exemplo de uso:</b></p>
 * <pre>{@code
 * MatriculaDTO dto = new MatriculaDTO(matricula);
 * }</pre>
 *
 * @author SeuNome
 * @version 1.1
 * @since 1.0
 */
public class MatriculaDTO implements Serializable {

    private Long id;
    private Long alunoId;
    private String alunoNome;
    private Long cursoId;
    private String cursoNome;
    private LocalDate dataMatricula;

    /**
     * Construtor padrão sem argumentos.
     */
    public MatriculaDTO() {
    }

    /**
     * Construtor com todos os atributos.
     *
     * @param id            Identificador único da matrícula.
     * @param alunoId       Identificador do aluno.
     * @param alunoNome     Nome completo do aluno.
     * @param cursoId       Identificador do curso.
     * @param cursoNome     Nome do curso.
     * @param dataMatricula Data em que a matrícula foi realizada.
     */
    public MatriculaDTO(Long id, Long alunoId, String alunoNome, Long cursoId, String cursoNome, LocalDate dataMatricula) {
        this.id = id;
        this.alunoId = alunoId;
        this.alunoNome = alunoNome;
        this.cursoId = cursoId;
        this.cursoNome = cursoNome;
        this.dataMatricula = dataMatricula;
    }

    /**
     * Constrói uma instância de {@code MatriculaDTO} com base na entidade {@link Matricula}.
     *
     * @param matricula Entidade de matrícula do domínio.
     */
    public MatriculaDTO(Matricula matricula) {
        this.id = matricula.getId();
        this.alunoId = matricula.getAluno().getId();
        this.alunoNome = matricula.getAluno().getNome();
        this.cursoId = matricula.getCurso().getId();
        this.cursoNome = matricula.getCurso().getNome();
        this.dataMatricula = matricula.getDataMatricula();
    }

    // === Getters e Setters ===

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAlunoId() {
        return alunoId;
    }

    public void setAlunoId(Long alunoId) {
        this.alunoId = alunoId;
    }

    public String getAlunoNome() {
        return alunoNome;
    }

    public void setAlunoNome(String alunoNome) {
        this.alunoNome = alunoNome;
    }

    public Long getCursoId() {
        return cursoId;
    }

    public void setCursoId(Long cursoId) {
        this.cursoId = cursoId;
    }

    public String getCursoNome() {
        return cursoNome;
    }

    public void setCursoNome(String cursoNome) {
        this.cursoNome = cursoNome;
    }

    public LocalDate getDataMatricula() {
        return dataMatricula;
    }

    public void setDataMatricula(LocalDate dataMatricula) {
        this.dataMatricula = dataMatricula;
    }

    // === equals, hashCode e toString ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatriculaDTO)) return false;
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
