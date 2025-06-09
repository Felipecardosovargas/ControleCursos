package com.escola.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * Represents a student in the course management system.
 * Each student has a unique ID, name, email, and date of birth.
 * <p>
 * This entity is mapped to the "alunos" table in the database.
 * </p>
 *
 * @version 1.1
 * @author FelipeCardoso
 */
@Entity
@Table(name = "alunos")
public class Aluno {

    /**
     * The unique identifier for the student.
     * Generated automatically by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The full name of the student.
     * Cannot be null or empty.
     */
    @Column(nullable = false)
    private String nome;

    /**
     * The email address of the student.
     * Must be unique and cannot be null.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * The date of birth of the student.
     * Cannot be null.
     */
    @Column(nullable = false, name = "data_nascimento")
    private LocalDate dataNascimento;

    /**
     * The set of enrollments associated with this student.
     * This represents the courses the student is enrolled in.
     * Mapped by the "aluno" field in the Matricula entity.
     * CascadeType.ALL and orphanRemoval=true are often used here if matriculas
     * should be deleted when a student is deleted, but be careful with business logic.
     * For this scenario, we'll manage matricula lifecycle separately or through service layer logic.
     */
    @OneToMany(mappedBy = "aluno", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<Matricula> matriculas = new HashSet<>();

    /**
     * Default constructor required by JPA.
     */
    public Aluno() {
    }

    /**
     * Constructs a new Aluno.
     *
     * @param nome The name of the student.
     * @param email The email of the student.
     * @param dataNascimento The date of birth of the student.
     */
    public Aluno(String nome, String email, LocalDate dataNascimento) {
        this.nome = nome;
        this.email = email;
        this.dataNascimento = dataNascimento;
    }

    // Getters and Setters with JavaDoc

    /**
     * Gets the unique identifier of the student.
     * @return The student's ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the student.
     * Typically only used by JPA.
     * @param id The student's ID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the student.
     * @return The student's name.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Sets the name of the student.
     * @param nome The student's name.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Gets the email of the student.
     * @return The student's email.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the student.
     * @param email The student's email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the date of birth of the student.
     * @return The student's date of birth.
     */
    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    /**
     * Sets the date of birth of the student.
     * @param dataNascimento The student's date of birth.
     */
    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    /**
     * Gets the set of matriculas (enrollments) for this student.
     * @return A set of {@link Matricula} objects.
     */
    public Set<Matricula> getMatriculas() {
        return matriculas;
    }

    /**
     * Sets the set of matriculas (enrollments) for this student.
     * @param matriculas A set of {@link Matricula} objects.
     */
    public void setMatriculas(Set<Matricula> matriculas) {
        this.matriculas = matriculas;
    }

    // equals, hashCode, and toString

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Aluno aluno = (Aluno) o;
        return Objects.equals(id, aluno.id) &&
                Objects.equals(email, aluno.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "Aluno{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", email='" + email + '\'' +
                ", dataNascimento=" + dataNascimento +
                '}';
    }
}