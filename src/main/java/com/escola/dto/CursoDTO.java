package com.escola.dto;

import java.util.Objects;

/**
 * Data Transfer Object for {@link com.escola.model.Curso}.
 * Used to transfer course data between application layers, particularly
 * from the service layer to the controller/presentation layer.
 * This class is declared as final as it is a simple data carrier.
 *
 * @version 1.0
 * @author SeuNomeAqui
 */
public final class CursoDTO {

    private final Long id;
    private final String nome;
    private final String descricao;
    private final int cargaHoraria;

    /**
     * Constructs a CursoDTO.
     *
     * @param id The unique identifier of the course.
     * @param nome The name of the course.
     * @param descricao A description of the course.
     * @param cargaHoraria The workload of the course in hours.
     */
    public CursoDTO(Long id, String nome, String descricao, int cargaHoraria) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.cargaHoraria = cargaHoraria;
    }

    // Getters

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getCargaHoraria() {
        return cargaHoraria;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
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