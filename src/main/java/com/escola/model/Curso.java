package com.escola.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

/**
 * Represents a course offered by the school.
 * Each course has a unique ID, name, description, and workload.
 * <p>
 * This entity is mapped to the "cursos" table in the database.
 * </p>
 *
 * @version 1.0
 * @author SeuNomeAqui
 */
@Entity
@Table(name = "cursos")
public class Curso {

    /**
     * The unique identifier for the course.
     * Generated automatically by the database.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the course.
     * Cannot be null or empty.
     */
    @Column(nullable = false, unique = true) // Assuming course names are unique
    private String nome;

    /**
     * A brief description of the course.
     */
    @Column(length = 1000) // Optional: define max length
    private String descricao;

    /**
     * The workload of the course in hours.
     * Must be a positive value.
     */
    @Column(name = "carga_horaria", nullable = false)
    private int cargaHoraria;

    /**
     * The set of enrollments associated with this course.
     * This represents the students enrolled in this course.
     * Mapped by the "curso" field in the Matricula entity.
     */
    @OneToMany(mappedBy = "curso", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private Set<Matricula> matriculas = new HashSet<>();


    /**
     * Default constructor required by JPA.
     */
    public Curso() {
    }

    /**
     * Constructs a new Curso.
     *
     * @param nome The name of the course.
     * @param descricao The description of the course.
     * @param cargaHoraria The workload of the course in hours.
     */
    public Curso(String nome, String descricao, int cargaHoraria) {
        this.nome = nome;
        this.descricao = descricao;
        this.cargaHoraria = cargaHoraria;
    }

    // Getters and Setters with JavaDoc

    /**
     * Gets the unique identifier of the course.
     * @return The course's ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the unique identifier of the course.
     * @param id The course's ID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the name of the course.
     * @return The course's name.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Sets the name of the course.
     * @param nome The course's name.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Gets the description of the course.
     * @return The course's description.
     */
    public String getDescricao() {
        return descricao;
    }

    /**
     * Sets the description of the course.
     * @param descricao The course's description.
     */
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Gets the workload of the course in hours.
     * @return The course's workload.
     */
    public int getCargaHoraria() {
        return cargaHoraria;
    }

    /**
     * Sets the workload of the course in hours.
     * @param cargaHoraria The course's workload.
     */
    public void setCargaHoraria(int cargaHoraria) {
        this.cargaHoraria = cargaHoraria;
    }

    /**
     * Gets the set of matriculas (enrollments) for this course.
     * @return A set of {@link Matricula} objects.
     */
    public Set<Matricula> getMatriculas() {
        return matriculas;
    }

    /**
     * Sets the set of matriculas (enrollments) for this course.
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
        Curso curso = (Curso) o;
        return Objects.equals(id, curso.id) &&
                Objects.equals(nome, curso.nome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nome);
    }

    @Override
    public String toString() {
        return "Curso{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", cargaHoraria=" + cargaHoraria +
                '}';
    }
}