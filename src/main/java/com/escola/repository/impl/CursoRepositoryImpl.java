package com.escola.repository.impl;

import com.escola.config.PersistenceManager;
import com.escola.model.Curso;
import com.escola.repository.CursoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of the {@link CursoRepository} interface.
 * Handles the persistence operations for {@link Curso} entities using
 * {@link EntityManager}.
 *
 * @version 1.0
 * @author SeuNomeAqui
 */
public class CursoRepositoryImpl implements CursoRepository {

    /**
     * {@inheritDoc}
     */
    @Override
    public Curso salvar(Curso curso) {
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.persist(curso);
            transaction.commit();
            return curso;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Erro ao salvar curso: " + e.getMessage(), e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Curso> buscarPorId(Long id) {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Curso.class, id));
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Curso> buscarPorNomeExato(String nome) {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            TypedQuery<Curso> query = em.createQuery("SELECT c FROM Curso c WHERE c.nome = :nome", Curso.class);
            query.setParameter("nome", nome);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Curso> buscarPorNomeContendo(String nomeParcial) {
        if (nomeParcial == null || nomeParcial.trim().isEmpty()) {
            return Collections.emptyList();
        }
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            // Using LOWER for case-insensitive search
            TypedQuery<Curso> query = em.createQuery(
                    "SELECT c FROM Curso c WHERE LOWER(c.nome) LIKE LOWER(:nomeParcial)", Curso.class);
            query.setParameter("nomeParcial", "%" + nomeParcial + "%");
            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Curso> listarTodos() {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            TypedQuery<Curso> query = em.createQuery("SELECT c FROM Curso c ORDER BY c.nome", Curso.class);
            return query.getResultList();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Curso atualizar(Curso curso) {
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = em.getTransaction();
            transaction.begin();
            Curso mergedCurso = em.merge(curso);
            transaction.commit();
            return mergedCurso;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Erro ao atualizar curso: " + e.getMessage(), e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean deletarPorId(Long id) {
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = em.getTransaction();
            transaction.begin();
            Curso curso = em.find(Curso.class, id);
            if (curso != null) {
                // Consider implications on Matricula records.
                // If Matriculas refer to this Curso, deletion might fail due to foreign key constraints
                // unless cascade is set or matriculas are handled by the service layer.
                em.remove(curso);
                transaction.commit();
                return true;
            }
            transaction.commit(); // Commit even if not found to close transaction
            return false;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            // Check for ConstraintViolationException if there are matriculas referencing this curso.
            // javax.persistence.PersistenceException wrapping org.hibernate.exception.ConstraintViolationException
            throw new RuntimeException("Erro ao deletar curso: " + e.getMessage(), e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}