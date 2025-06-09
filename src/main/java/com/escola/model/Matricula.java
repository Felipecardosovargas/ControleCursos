package com.escola.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents an enrollment of a student in a course.
 * This entity acts as the associative table for the Many-to-Many
 * relationship between Aluno and Curso.
 * <p>
 * This entity is mapped to the "matriculas" table in the database.
 * </p>
 *
 * @version 1.0
 * @author FelipeCardoso
 */
@Entity
@Table(name = "matriculas",
        uniqueConstraints = @UniqueConstraint(columnNames = {"aluno_id", "curso_id"})) // A student can only enroll once in the same course
public class Matricula {

    /**
     * The unique identifier for the enrollment.
     * Generated automatically by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The student who is enrolled.
     * This is a Many-to-One relationship with the Aluno entity.
     * The "aluno_id" column in the "matriculas" table will be the foreign key.
     * Cannot be null.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    /**
     * The course in which the student is enrolled.
     * This is a Many-to-One relationship with the Curso entity.
     * The "curso_id" column in the "matriculas" table will be the foreign key.
     * Cannot be null.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id", nullable = false)
    private Curso curso;

    /**
     * The date on which the enrollment was made.
     * Defaults to the current date if not specified.
     * Cannot be null.
     */
    @Column(name = "data_matricula", nullable = false)
    private LocalDate dataMatricula;

    /**
     * Default constructor required by JPA.
     */
    public Matricula() {
        this.dataMatricula = LocalDate.now(); // Default to current date
    }

    /**
     * Constructs a new Matricula.
     *
     * @param aluno The student enrolling.
     * @param curso The course to enroll in.
     */
    public Matricula(Aluno aluno, Curso curso) {
        this(); // Calls the default constructor to set dataMatricula
        this.aluno = aluno;
        this.curso = curso;
    }

    /**
     * Constructs a new Matricula with a specific enrollment date.
     *
     * @param aluno The student enrolling.
     * @param curso The course to enroll in.
     * @param dataMatricula The specific date of enrollment.
     */
    public Matricula(Aluno aluno, Curso curso, LocalDate dataMatricula) {
        this.aluno = aluno;
        this.curso = curso;
        this.dataMatricula = dataMatricula;
    }

    // Getters and Setters with JavaDoc

    /**
     * Gets the unique identifier of the enrollment.
     * @return The enrollment's ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the enrollment.
     * @param id The enrollment's ID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the student associated with this enrollment.
     * @return The {@link Aluno} object.
     */
    public Aluno getAluno() {
        return aluno;
    }

    /**
     * Sets the student for this enrollment.
     * @param aluno The {@link Aluno} object.
     */
    public void setAluno(Aluno aluno) {
        this.aluno = aluno;
    }

    /**
     * Gets the course associated with this enrollment.
     * @return The {@link Curso} object.
     */
    public Curso getCurso() {
        return curso;
    }

    /**
     * Sets the course for this enrollment.
     * @param curso The {@link Curso} object.
     */
    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    /**
     * Gets the date of enrollment.
     * @return The enrollment date.
     */
    public LocalDate getDataMatricula() {
        return dataMatricula;
    }

    /**
     * Sets the date of enrollment.
     * @param dataMatricula The enrollment date.
     */
    public void setDataMatricula(LocalDate dataMatricula) {
        this.dataMatricula = dataMatricula;
    }

    // equals, hashCode, and toString
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matricula matricula = (Matricula) o;
        return Objects.equals(id, matricula.id) ||
                (Objects.equals(aluno, matricula.aluno) &&
                        Objects.equals(curso, matricula.curso)); // Business key for uniqueness
    }

    @Override
    public int hashCode() {
        // If ID is null (before persisting), use a business key for hashCode consistency
        return id != null ? Objects.hash(id) : Objects.hash(aluno, curso);
    }

    @Column(name = "cancelada", nullable = false)
    private boolean cancelada = false;

    public boolean isCancelada() {
        return cancelada;
    }

    public void setCancelada(boolean cancelada) {
        this.cancelada = cancelada;
    }


    @Override
    public String toString() {
        return "Matricula{" +
                "id=" + id +
                ", alunoId=" + (aluno != null ? aluno.getId() : "null") +
                ", cursoId=" + (curso != null ? curso.getId() : "null") +
                ", dataMatricula=" + dataMatricula +
                '}';
    }
}