/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.miguel.proyecto.model;

import com.miguel.proyecto.model.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author miguel
 */
public class RespuestaJpaController implements Serializable {

    public RespuestaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Respuesta respuesta) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pregunta preguntaId = respuesta.getPreguntaId();
            if (preguntaId != null) {
                preguntaId = em.getReference(preguntaId.getClass(), preguntaId.getId());
                respuesta.setPreguntaId(preguntaId);
            }
            Usuario usuarioId = respuesta.getUsuarioId();
            if (usuarioId != null) {
                usuarioId = em.getReference(usuarioId.getClass(), usuarioId.getId());
                respuesta.setUsuarioId(usuarioId);
            }
            em.persist(respuesta);
            if (preguntaId != null) {
                preguntaId.getRespuestaList().add(respuesta);
                preguntaId = em.merge(preguntaId);
            }
            if (usuarioId != null) {
                usuarioId.getRespuestaList().add(respuesta);
                usuarioId = em.merge(usuarioId);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Respuesta respuesta) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Respuesta persistentRespuesta = em.find(Respuesta.class, respuesta.getId());
            Pregunta preguntaIdOld = persistentRespuesta.getPreguntaId();
            Pregunta preguntaIdNew = respuesta.getPreguntaId();
            Usuario usuarioIdOld = persistentRespuesta.getUsuarioId();
            Usuario usuarioIdNew = respuesta.getUsuarioId();
            if (preguntaIdNew != null) {
                preguntaIdNew = em.getReference(preguntaIdNew.getClass(), preguntaIdNew.getId());
                respuesta.setPreguntaId(preguntaIdNew);
            }
            if (usuarioIdNew != null) {
                usuarioIdNew = em.getReference(usuarioIdNew.getClass(), usuarioIdNew.getId());
                respuesta.setUsuarioId(usuarioIdNew);
            }
            respuesta = em.merge(respuesta);
            if (preguntaIdOld != null && !preguntaIdOld.equals(preguntaIdNew)) {
                preguntaIdOld.getRespuestaList().remove(respuesta);
                preguntaIdOld = em.merge(preguntaIdOld);
            }
            if (preguntaIdNew != null && !preguntaIdNew.equals(preguntaIdOld)) {
                preguntaIdNew.getRespuestaList().add(respuesta);
                preguntaIdNew = em.merge(preguntaIdNew);
            }
            if (usuarioIdOld != null && !usuarioIdOld.equals(usuarioIdNew)) {
                usuarioIdOld.getRespuestaList().remove(respuesta);
                usuarioIdOld = em.merge(usuarioIdOld);
            }
            if (usuarioIdNew != null && !usuarioIdNew.equals(usuarioIdOld)) {
                usuarioIdNew.getRespuestaList().add(respuesta);
                usuarioIdNew = em.merge(usuarioIdNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = respuesta.getId();
                if (findRespuesta(id) == null) {
                    throw new NonexistentEntityException("The respuesta with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Respuesta respuesta;
            try {
                respuesta = em.getReference(Respuesta.class, id);
                respuesta.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The respuesta with id " + id + " no longer exists.", enfe);
            }
            Pregunta preguntaId = respuesta.getPreguntaId();
            if (preguntaId != null) {
                preguntaId.getRespuestaList().remove(respuesta);
                preguntaId = em.merge(preguntaId);
            }
            Usuario usuarioId = respuesta.getUsuarioId();
            if (usuarioId != null) {
                usuarioId.getRespuestaList().remove(respuesta);
                usuarioId = em.merge(usuarioId);
            }
            em.remove(respuesta);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Respuesta> findRespuestaEntities() {
        return findRespuestaEntities(true, -1, -1);
    }

    public List<Respuesta> findRespuestaEntities(int maxResults, int firstResult) {
        return findRespuestaEntities(false, maxResults, firstResult);
    }

    private List<Respuesta> findRespuestaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Respuesta.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Respuesta findRespuesta(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Respuesta.class, id);
        } finally {
            em.close();
        }
    }

    public int getRespuestaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Respuesta> rt = cq.from(Respuesta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
