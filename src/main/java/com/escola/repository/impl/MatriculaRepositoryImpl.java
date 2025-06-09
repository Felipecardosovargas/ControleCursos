package com.escola.repository.impl;

import com.escola.config.PersistenceManager;
import com.escola.model.Matricula;
import com.escola.repository.MatriculaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of the {@link MatriculaRepository}.
 * Handles the persistence operations for {@link Matricula} entities.
 *
 * @version 1.1
 * @author FelipeCardoso
 */
public class MatriculaRepositoryImpl implements MatriculaRepository {

    @Override
    public Matricula salvar(Matricula matricula) {
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            em.persist(matricula);
            tx.commit();
            return matricula;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao salvar matrícula: " + e.getMessage(), e);
        } finally {
            if (em != null) em.close();
        }
    }

    @Override
    public Matricula atualizar(Matricula matricula) {
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            Matricula updated = em.merge(matricula);
            tx.commit();
            return updated;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao atualizar matrícula: " + e.getMessage(), e);
        } finally {
            if (em != null) em.close();
        }
    }

    @Override
    public void remover(Matricula matricula) {
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            Matricula attached = em.contains(matricula) ? matricula : em.merge(matricula);
            em.remove(attached);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao remover matrícula: " + e.getMessage(), e);
        } finally {
            if (em != null) em.close();
        }
    }

    @Override
    public Optional<Matricula> buscarPorId(Long id) {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            return Optional.ofNullable(em.find(Matricula.class, id));
        } finally {
            if (em != null) em.close();
        }
    }

    @Override
    public Optional<Matricula> buscarPorAlunoIdECursoId(Long alunoId, Long cursoId) {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            String jpql = "SELECT m FROM Matricula m WHERE m.aluno.id = :alunoId AND m.curso.id = :cursoId";
            TypedQuery<Matricula> query = em.createQuery(jpql, Matricula.class);
            query.setParameter("alunoId", alunoId);
            query.setParameter("cursoId", cursoId);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            if (em != null) em.close();
        }
    }

    @Override
    public long contarPorAlunoId(Long alunoId) {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            String jpql = "SELECT COUNT(m) FROM Matricula m WHERE m.aluno.id = :alunoId";
            return em.createQuery(jpql, Long.class)
                    .setParameter("alunoId", alunoId)
                    .getSingleResult();
        } finally {
            if (em != null) em.close();
        }
    }

    @Override
    public long contarPorCursoId(Long cursoId) {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            String jpql = "SELECT COUNT(m) FROM Matricula m WHERE m.curso.id = :cursoId";
            return em.createQuery(jpql, Long.class)
                    .setParameter("cursoId", cursoId)
                    .getSingleResult();
        } finally {
            if (em != null) em.close();
        }
    }

    @Override
    public List<Matricula> listarTodas() {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            String jpql = "SELECT m FROM Matricula m";
            return em.createQuery(jpql, Matricula.class).getResultList();
        } finally {
            if (em != null) em.close();
        }
    }

    @Override
    public List<Matricula> listarPorCursoId(Long cursoId) {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            String jpql = "SELECT m FROM Matricula m WHERE m.curso.id = :cursoId";
            return em.createQuery(jpql, Matricula.class)
                    .setParameter("cursoId", cursoId)
                    .getResultList();
        } finally {
            if (em != null) em.close();
        }
    }

    @Override
    public List<Matricula> listarPorAlunoId(Long alunoId) {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            String jpql = "SELECT m FROM Matricula m WHERE m.aluno.id = :alunoId";
            return em.createQuery(jpql, Matricula.class)
                    .setParameter("alunoId", alunoId)
                    .getResultList();
        } finally {
            if (em != null) em.close();
        }
    }

    @Override
    public boolean deletarPorId(Long id) {
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = null;
        try {
            tx = em.getTransaction();
            tx.begin();
            Matricula matricula = em.find(Matricula.class, id);
            if (matricula != null) {
                em.remove(matricula);
                tx.commit();
                return true;
            }
            tx.commit(); // Commit mesmo se não encontrar
            return false;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao deletar matrícula: " + e.getMessage(), e);
        } finally {
            if (em != null) em.close();
        }
    }

    @Override
    public List<Matricula> listarTodasComDetalhes() {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            String jpql = "SELECT m FROM Matricula m " +
                    "JOIN FETCH m.aluno " +
                    "JOIN FETCH m.curso";
            return em.createQuery(jpql, Matricula.class).getResultList();
        } finally {
            if (em != null) em.close();
        }
    }

    @Override
    public Optional<Matricula> buscarPorIdComDetalhes(Long id) {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            String jpql = "SELECT m FROM Matricula m " +
                    "JOIN FETCH m.aluno " +
                    "JOIN FETCH m.curso " +
                    "WHERE m.id = :id";
            TypedQuery<Matricula> query = em.createQuery(jpql, Matricula.class);
            query.setParameter("id", id);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            if (em != null) em.close();
        }
    }
}
