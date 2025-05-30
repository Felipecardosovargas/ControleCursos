package com.escola.dto;

import com.escola.model.Matricula;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) que representa uma matrícula entre aluno e curso.
 * <p>
 * Facilita o transporte de dados entre camadas, encapsulando informações relevantes
 * sem expor diretamente entidades de domínio. Contém dados desnormalizados para exibição.
 * </p>
 *
 * <p><b>Exemplo de uso:</b></p>
 * <pre>{@code
 * MatriculaDTO dto = new MatriculaDTO(matricula);
 * }</pre>
 *
 * @author SeuNome
 * @version 1.2
 * @since 1.0
 */
public class MatriculaDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long alunoId;
    private String alunoNome;
    private Long cursoId;
    private String cursoNome;
    private LocalDate dataMatricula;
    private boolean cancelada;

    /**
     * Construtor padrão.
     */
    public MatriculaDTO() {
    }

    /**
     * Construtor completo.
     *
     * @param id             ID da matrícula.
     * @param alunoId        ID do aluno.
     * @param alunoNome      Nome do aluno.
     * @param cursoId        ID do curso.
     * @param cursoNome      Nome do curso.
     * @param dataMatricula  Data da matrícula.
     * @param cancelada      Flag indicando se a matrícula está cancelada.
     */
    public MatriculaDTO(Long id, Long alunoId, String alunoNome,
                        Long cursoId, String cursoNome,
                        LocalDate dataMatricula, boolean cancelada) {
        this.id = id;
        this.alunoId = alunoId;
        this.alunoNome = alunoNome;
        this.cursoId = cursoId;
        this.cursoNome = cursoNome;
        this.dataMatricula = dataMatricula;
        this.cancelada = cancelada;
    }

    /**
     * Construtor baseado na entidade {@link Matricula}.
     *
     * @param matricula Instância da entidade de domínio.
     */
    public MatriculaDTO(Matricula matricula) {
        this.id = matricula.getId();
        this.alunoId = matricula.getAluno().getId();
        this.alunoNome = matricula.getAluno().getNome();
        this.cursoId = matricula.getCurso().getId();
        this.cursoNome = matricula.getCurso().getNome();
        this.dataMatricula = matricula.getDataMatricula();
        this.cancelada = matricula.isCancelada(); // Supondo que tenha este método
    }

    public MatriculaDTO(Long id, Long id1, String nome, Long id2, String nome1, LocalDate dataMatricula) {
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

    public boolean isCancelada() {
        return cancelada;
    }

    public void setCancelada(boolean cancelada) {
        this.cancelada = cancelada;
    }

    // === equals, hashCode e toString ===

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatriculaDTO)) return false;
        MatriculaDTO that = (MatriculaDTO) o;
        return cancelada == that.cancelada &&
                Objects.equals(id, that.id) &&
                Objects.equals(alunoId, that.alunoId) &&
                Objects.equals(alunoNome, that.alunoNome) &&
                Objects.equals(cursoId, that.cursoId) &&
                Objects.equals(cursoNome, that.cursoNome) &&
                Objects.equals(dataMatricula, that.dataMatricula);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, alunoId, alunoNome, cursoId, cursoNome, dataMatricula, cancelada);
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
                ", cancelada=" + cancelada +
                '}';
    }
}
