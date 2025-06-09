package com.escola.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object representing a Student (Aluno).
 * This class is used to transfer student data between different layers of the application.
 * It follows the Single Responsibility Principle by only holding data and no business logic.
 *
 * @version 1.1
 * @author FelipeCardoso
 */
public class AlunoDTO {

    private Long id;
    private String nome;
    private String email;
    private LocalDate dataNascimento;

    /**
     * Default constructor.
     */
    public AlunoDTO() {
    }

    /**
     * Parameterized constructor to initialize all fields.
     *
     * @param id              the unique identifier of the student
     * @param nome            the name of the student
     * @param email           the email of the student
     * @param dataNascimento  the birth date of the student
     */
    public AlunoDTO(Long id, String nome, String email, LocalDate dataNascimento) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.dataNascimento = dataNascimento;
    }

    /**
     * Gets the student ID.
     *
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the student ID.
     *
     * @param id the unique identifier to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the student name.
     *
     * @return the name
     */
    public String getNome() {
        return nome;
    }

    /**
     * Sets the student name.
     *
     * @param nome the name to set
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Gets the student email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the student email.
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the student's date of birth.
     *
     * @return the date of birth
     */
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    /**
     * Sets the student's date of birth.
     *
     * @param dataNascimento the date of birth to set
     */
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    /**
     * Returns a string representation of the student.
     *
     * @return formatted string containing student information
     */
    @Override
    public String toString() {
        return "AlunoDTO{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", dataNascimento=" + dataNascimento +
                '}';
    }
}
