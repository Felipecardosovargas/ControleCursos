package com.escola.repository.impl;

import com.escola.config.PersistenceManager;
import com.escola.model.Aluno;
import com.escola.repository.AlunoRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of the {@link AlunoRepository} interface.
 * Handles the persistence operations for {@link Aluno} entities using
 * {@link EntityManager}.
 * <p>
 * This class manages database transactions for each operation.
 * </p>
 *
 * @version 1.1
 * @author FelipeCardoso
 */
public class AlunoRepositoryImpl implements AlunoRepository {

    /**
     * {@inheritDoc}
     */
    @Override
    public Aluno salvar(Aluno aluno) {
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = em.getTransaction();
            transaction.begin();
            em.persist(aluno);
            transaction.commit();
            return aluno;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            // Consider logging the exception or rethrowing a custom data access exception
            throw new RuntimeException("Erro ao salvar aluno: " + e.getMessage(), e);
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
    public Optional<Aluno> buscarPorId(Long id) {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Aluno.class, id));
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
    public Optional<Aluno> buscarPorEmail(String email) {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            TypedQuery<Aluno> query = em.createQuery("SELECT a FROM Aluno a WHERE a.email = :email", Aluno.class);
            query.setParameter("email", email);
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
    public List<Aluno> listarTodos() {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            TypedQuery<Aluno> query = em.createQuery("SELECT a FROM Aluno a ORDER BY a.nome", Aluno.class);
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
    public Aluno atualizar(Aluno aluno) {
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = em.getTransaction();
            transaction.begin();
            Aluno mergedAluno = em.merge(aluno);
            transaction.commit();
            return mergedAluno;
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Erro ao atualizar aluno: " + e.getMessage(), e);
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
    public void deletarPorId(Long id) {
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction transaction = null;
        try {
            transaction = em.getTransaction();
            transaction.begin();
            Aluno aluno = em.find(Aluno.class, id);
            if (aluno != null) {
                // Before deleting an Aluno, consider implications on related Matricula records.
                // Depending on business rules, you might need to delete associated Matriculas
                // or prevent deletion if Matriculas exist.
                // For simplicity, this example directly removes the Aluno.
                // If CascadeType.REMOVE or orphanRemoval=true is set on Aluno.matriculas,
                // JPA might handle this, but explicit service logic is often clearer.
                em.remove(aluno);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Erro ao deletar aluno: " + e.getMessage(), e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
}