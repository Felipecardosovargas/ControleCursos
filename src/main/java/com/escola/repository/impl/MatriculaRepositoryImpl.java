package com.escola.repository.impl;

import com.escola.config.PersistenceManager;
import com.escola.model.Matricula;
import com.escola.repository.MatriculaRepository;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

public class MatriculaRepositoryImpl implements MatriculaRepository {

    @Override
    public Matricula salvar(Matricula matricula) {
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(matricula);
            tx.commit();
            return matricula;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao salvar matrícula: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public Matricula atualizar(Matricula matricula) {
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Matricula atualizado = em.merge(matricula);
            tx.commit();
            return atualizado;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao atualizar matrícula: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public void remover(Matricula matricula) {
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Matricula managed = em.find(Matricula.class, matricula.getId());
            if (managed != null) {
                em.remove(managed);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao remover matrícula: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Matricula> buscarPorId(Long id) {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            Matricula matricula = em.find(Matricula.class, id);
            return Optional.ofNullable(matricula);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Matricula> listarTodas() {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            TypedQuery<Matricula> query = em.createQuery(
                    "SELECT m FROM Matricula m", Matricula.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Matricula> buscarPorAlunoIdECursoId(Long alunoId, Long cursoId) {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            TypedQuery<Matricula> query = em.createQuery(
                    "SELECT m FROM Matricula m WHERE m.aluno.id = :alunoId AND m.curso.id = :cursoId", Matricula.class
            );
            query.setParameter("alunoId", alunoId);
            query.setParameter("cursoId", cursoId);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public long contarPorAlunoId(Long alunoId) {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(m) FROM Matricula m WHERE m.aluno.id = :alunoId", Long.class
            );
            query.setParameter("alunoId", alunoId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public long contarPorCursoId(Long cursoId) {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            TypedQuery<Long> query = em.createQuery(
                    "SELECT COUNT(m) FROM Matricula m WHERE m.curso.id = :cursoId", Long.class
            );
            query.setParameter("cursoId", cursoId);
            return query.getSingleResult();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Matricula> listarPorCursoId(Long cursoId) {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            TypedQuery<Matricula> query = em.createQuery(
                    "SELECT m FROM Matricula m WHERE m.curso.id = :cursoId", Matricula.class
            );
            query.setParameter("cursoId", cursoId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Matricula> listarPorAlunoId(Long alunoId) {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            TypedQuery<Matricula> query = em.createQuery(
                    "SELECT m FROM Matricula m WHERE m.aluno.id = :alunoId", Matricula.class
            );
            query.setParameter("alunoId", alunoId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public boolean deletarPorId(Long id) {
        EntityManager em = PersistenceManager.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Matricula matricula = em.find(Matricula.class, id);
            if (matricula != null) {
                em.remove(matricula);
                tx.commit();
                return true;
            }
            tx.commit();
            return false;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw new RuntimeException("Erro ao deletar matrícula: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Object> findByAlunoIdAndCursoId(Long alunoId, Long cursoId) {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            TypedQuery<Matricula> query = em.createQuery(
                    "SELECT m FROM Matricula m WHERE m.aluno.id = :alunoId AND m.curso.id = :cursoId", Matricula.class
            );
            query.setParameter("alunoId", alunoId);
            query.setParameter("cursoId", cursoId);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }

    @Override
    public List<Matricula> listarTodasComNomes() {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            TypedQuery<Matricula> query = em.createQuery(
                    "SELECT m FROM Matricula m JOIN FETCH m.aluno JOIN FETCH m.curso", Matricula.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<Object> buscarPorIdComDetalhes(Long id) {
        EntityManager em = PersistenceManager.getEntityManager();
        try {
            TypedQuery<Matricula> query = em.createQuery(
                    "SELECT m FROM Matricula m JOIN FETCH m.aluno JOIN FETCH m.curso WHERE m.id = :id", Matricula.class
            );
            query.setParameter("id", id);
            return Optional.ofNullable(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } finally {
            em.close();
        }
    }
}
