package com.escola.dto;

import java.util.Objects;

/**
 * Data Transfer Object representing a Course (Curso).
 * Used to transfer course data between different layers of the application.
 * This class holds data and no business logic.
 *
 * @version 1.1
 * @author FelipeCardoso
 */
public class CursoDTO {

    private Long id;
    private String nome;
    private String descricao;
    private int cargaHoraria;

    /**
     * Default constructor.
     */
    public CursoDTO() {
    }

    /**
     * Parameterized constructor to initialize all fields.
     *
     * @param id           the unique identifier of the course
     * @param nome         the name of the course
     * @param descricao    a description of the course
     * @param cargaHoraria the workload of the course in hours
     */
    public CursoDTO(Long id, String nome, String descricao, int cargaHoraria) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.cargaHoraria = cargaHoraria;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }

    public void setCargaHoraria(int cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CursoDTO)) return false;
        CursoDTO cursoDTO = (CursoDTO) o;
        return cargaHoraria == cursoDTO.cargaHoraria &&
                Objects.equals(id, cursoDTO.id) &&
                Objects.equals(nome, cursoDTO.nome) &&
                Objects.equals(descricao, cursoDTO.descricao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, descricao, cargaHoraria);
    }

    @Override
    public String toString() {
        return "CursoDTO{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", cargaHoraria=" + cargaHoraria +
                '}';
    }
}
