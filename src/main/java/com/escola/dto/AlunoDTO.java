package com.escola.dto;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Data Transfer Object for {@link com.escola.model.Aluno}.
 * Used to transfer student data, typically between the service layer and the
 * controller or presentation layer. This helps to decouple the domain model
 * from the exposure layer and can be tailored for specific use cases.
 * This class is declared as final as it is a simple data carrier.
 *
 * @version 1.0
 * @author SeuNomeAqui
 */
public final class AlunoDTO { // DTOs can be final

    private final Long id;
    private final String nome;
    private final String email;
    private final LocalDate dataNascimento;

    /**
     * Constructs an AlunoDTO.
     *
     * @param id The ID of the student.
     * @param nome The name of the student.
     * @param email The email of the student.
     * @param dataNascimento The date of birth of the student.
     */
    public AlunoDTO(Long id, String nome, String email, LocalDate dataNascimento) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.dataNascimento = dataNascimento;
    }

    // Only getters as DTOs are often immutable or primarily for reading

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    @Override
    public String toString() {
        return "AlunoDTO{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", dataNascimento=" + dataNascimento +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlunoDTO alunoDTO = (AlunoDTO) o;
        return Objects.equals(id, alunoDTO.id) &&
                Objects.equals(nome, alunoDTO.nome) &&
                Objects.equals(email, alunoDTO.email) &&
                Objects.equals(dataNascimento, alunoDTO.dataNascimento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome, email, dataNascimento);
    }
}