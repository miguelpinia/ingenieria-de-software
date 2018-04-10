/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.miguel.proyecto.model;

import com.miguel.proyecto.model.exceptions.IllegalOrphanException;
import com.miguel.proyecto.model.exceptions.NonexistentEntityException;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author miguel
 */
public class PreguntaJpaController implements Serializable {

    public PreguntaJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Pregunta pregunta) {
        if (pregunta.getRespuestaList() == null) {
            pregunta.setRespuestaList(new ArrayList<Respuesta>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario usuarioId = pregunta.getUsuarioId();
            if (usuarioId != null) {
                usuarioId = em.getReference(usuarioId.getClass(), usuarioId.getId());
                pregunta.setUsuarioId(usuarioId);
            }
            List<Respuesta> attachedRespuestaList = new ArrayList<Respuesta>();
            for (Respuesta respuestaListRespuestaToAttach : pregunta.getRespuestaList()) {
                respuestaListRespuestaToAttach = em.getReference(respuestaListRespuestaToAttach.getClass(), respuestaListRespuestaToAttach.getId());
                attachedRespuestaList.add(respuestaListRespuestaToAttach);
            }
            pregunta.setRespuestaList(attachedRespuestaList);
            em.persist(pregunta);
            if (usuarioId != null) {
                usuarioId.getPreguntaList().add(pregunta);
                usuarioId = em.merge(usuarioId);
            }
            for (Respuesta respuestaListRespuesta : pregunta.getRespuestaList()) {
                Pregunta oldPreguntaIdOfRespuestaListRespuesta = respuestaListRespuesta.getPreguntaId();
                respuestaListRespuesta.setPreguntaId(pregunta);
                respuestaListRespuesta = em.merge(respuestaListRespuesta);
                if (oldPreguntaIdOfRespuestaListRespuesta != null) {
                    oldPreguntaIdOfRespuestaListRespuesta.getRespuestaList().remove(respuestaListRespuesta);
                    oldPreguntaIdOfRespuestaListRespuesta = em.merge(oldPreguntaIdOfRespuestaListRespuesta);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Pregunta pregunta) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pregunta persistentPregunta = em.find(Pregunta.class, pregunta.getId());
            Usuario usuarioIdOld = persistentPregunta.getUsuarioId();
            Usuario usuarioIdNew = pregunta.getUsuarioId();
            List<Respuesta> respuestaListOld = persistentPregunta.getRespuestaList();
            List<Respuesta> respuestaListNew = pregunta.getRespuestaList();
            List<String> illegalOrphanMessages = null;
            for (Respuesta respuestaListOldRespuesta : respuestaListOld) {
                if (!respuestaListNew.contains(respuestaListOldRespuesta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Respuesta " + respuestaListOldRespuesta + " since its preguntaId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (usuarioIdNew != null) {
                usuarioIdNew = em.getReference(usuarioIdNew.getClass(), usuarioIdNew.getId());
                pregunta.setUsuarioId(usuarioIdNew);
            }
            List<Respuesta> attachedRespuestaListNew = new ArrayList<Respuesta>();
            for (Respuesta respuestaListNewRespuestaToAttach : respuestaListNew) {
                respuestaListNewRespuestaToAttach = em.getReference(respuestaListNewRespuestaToAttach.getClass(), respuestaListNewRespuestaToAttach.getId());
                attachedRespuestaListNew.add(respuestaListNewRespuestaToAttach);
            }
            respuestaListNew = attachedRespuestaListNew;
            pregunta.setRespuestaList(respuestaListNew);
            pregunta = em.merge(pregunta);
            if (usuarioIdOld != null && !usuarioIdOld.equals(usuarioIdNew)) {
                usuarioIdOld.getPreguntaList().remove(pregunta);
                usuarioIdOld = em.merge(usuarioIdOld);
            }
            if (usuarioIdNew != null && !usuarioIdNew.equals(usuarioIdOld)) {
                usuarioIdNew.getPreguntaList().add(pregunta);
                usuarioIdNew = em.merge(usuarioIdNew);
            }
            for (Respuesta respuestaListNewRespuesta : respuestaListNew) {
                if (!respuestaListOld.contains(respuestaListNewRespuesta)) {
                    Pregunta oldPreguntaIdOfRespuestaListNewRespuesta = respuestaListNewRespuesta.getPreguntaId();
                    respuestaListNewRespuesta.setPreguntaId(pregunta);
                    respuestaListNewRespuesta = em.merge(respuestaListNewRespuesta);
                    if (oldPreguntaIdOfRespuestaListNewRespuesta != null && !oldPreguntaIdOfRespuestaListNewRespuesta.equals(pregunta)) {
                        oldPreguntaIdOfRespuestaListNewRespuesta.getRespuestaList().remove(respuestaListNewRespuesta);
                        oldPreguntaIdOfRespuestaListNewRespuesta = em.merge(oldPreguntaIdOfRespuestaListNewRespuesta);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = pregunta.getId();
                if (findPregunta(id) == null) {
                    throw new NonexistentEntityException("The pregunta with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Pregunta pregunta;
            try {
                pregunta = em.getReference(Pregunta.class, id);
                pregunta.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The pregunta with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Respuesta> respuestaListOrphanCheck = pregunta.getRespuestaList();
            for (Respuesta respuestaListOrphanCheckRespuesta : respuestaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Pregunta (" + pregunta + ") cannot be destroyed since the Respuesta " + respuestaListOrphanCheckRespuesta + " in its respuestaList field has a non-nullable preguntaId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Usuario usuarioId = pregunta.getUsuarioId();
            if (usuarioId != null) {
                usuarioId.getPreguntaList().remove(pregunta);
                usuarioId = em.merge(usuarioId);
            }
            em.remove(pregunta);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Pregunta> findPreguntaEntities() {
        return findPreguntaEntities(true, -1, -1);
    }

    public List<Pregunta> findPreguntaEntities(int maxResults, int firstResult) {
        return findPreguntaEntities(false, maxResults, firstResult);
    }

    private List<Pregunta> findPreguntaEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Pregunta.class));
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

    public Pregunta findPregunta(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Pregunta.class, id);
        } finally {
            em.close();
        }
    }

    public int getPreguntaCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Pregunta> rt = cq.from(Pregunta.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
