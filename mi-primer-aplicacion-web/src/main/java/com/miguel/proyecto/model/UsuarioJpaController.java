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
public class UsuarioJpaController implements Serializable {

    public UsuarioJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Usuario usuario) {
        if (usuario.getRespuestaList() == null) {
            usuario.setRespuestaList(new ArrayList<Respuesta>());
        }
        if (usuario.getPreguntaList() == null) {
            usuario.setPreguntaList(new ArrayList<Pregunta>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Respuesta> attachedRespuestaList = new ArrayList<Respuesta>();
            for (Respuesta respuestaListRespuestaToAttach : usuario.getRespuestaList()) {
                respuestaListRespuestaToAttach = em.getReference(respuestaListRespuestaToAttach.getClass(), respuestaListRespuestaToAttach.getId());
                attachedRespuestaList.add(respuestaListRespuestaToAttach);
            }
            usuario.setRespuestaList(attachedRespuestaList);
            List<Pregunta> attachedPreguntaList = new ArrayList<Pregunta>();
            for (Pregunta preguntaListPreguntaToAttach : usuario.getPreguntaList()) {
                preguntaListPreguntaToAttach = em.getReference(preguntaListPreguntaToAttach.getClass(), preguntaListPreguntaToAttach.getId());
                attachedPreguntaList.add(preguntaListPreguntaToAttach);
            }
            usuario.setPreguntaList(attachedPreguntaList);
            em.persist(usuario);
            for (Respuesta respuestaListRespuesta : usuario.getRespuestaList()) {
                Usuario oldUsuarioIdOfRespuestaListRespuesta = respuestaListRespuesta.getUsuarioId();
                respuestaListRespuesta.setUsuarioId(usuario);
                respuestaListRespuesta = em.merge(respuestaListRespuesta);
                if (oldUsuarioIdOfRespuestaListRespuesta != null) {
                    oldUsuarioIdOfRespuestaListRespuesta.getRespuestaList().remove(respuestaListRespuesta);
                    oldUsuarioIdOfRespuestaListRespuesta = em.merge(oldUsuarioIdOfRespuestaListRespuesta);
                }
            }
            for (Pregunta preguntaListPregunta : usuario.getPreguntaList()) {
                Usuario oldUsuarioIdOfPreguntaListPregunta = preguntaListPregunta.getUsuarioId();
                preguntaListPregunta.setUsuarioId(usuario);
                preguntaListPregunta = em.merge(preguntaListPregunta);
                if (oldUsuarioIdOfPreguntaListPregunta != null) {
                    oldUsuarioIdOfPreguntaListPregunta.getPreguntaList().remove(preguntaListPregunta);
                    oldUsuarioIdOfPreguntaListPregunta = em.merge(oldUsuarioIdOfPreguntaListPregunta);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Usuario usuario) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Usuario persistentUsuario = em.find(Usuario.class, usuario.getId());
            List<Respuesta> respuestaListOld = persistentUsuario.getRespuestaList();
            List<Respuesta> respuestaListNew = usuario.getRespuestaList();
            List<Pregunta> preguntaListOld = persistentUsuario.getPreguntaList();
            List<Pregunta> preguntaListNew = usuario.getPreguntaList();
            List<String> illegalOrphanMessages = null;
            for (Respuesta respuestaListOldRespuesta : respuestaListOld) {
                if (!respuestaListNew.contains(respuestaListOldRespuesta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Respuesta " + respuestaListOldRespuesta + " since its usuarioId field is not nullable.");
                }
            }
            for (Pregunta preguntaListOldPregunta : preguntaListOld) {
                if (!preguntaListNew.contains(preguntaListOldPregunta)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Pregunta " + preguntaListOldPregunta + " since its usuarioId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Respuesta> attachedRespuestaListNew = new ArrayList<Respuesta>();
            for (Respuesta respuestaListNewRespuestaToAttach : respuestaListNew) {
                respuestaListNewRespuestaToAttach = em.getReference(respuestaListNewRespuestaToAttach.getClass(), respuestaListNewRespuestaToAttach.getId());
                attachedRespuestaListNew.add(respuestaListNewRespuestaToAttach);
            }
            respuestaListNew = attachedRespuestaListNew;
            usuario.setRespuestaList(respuestaListNew);
            List<Pregunta> attachedPreguntaListNew = new ArrayList<Pregunta>();
            for (Pregunta preguntaListNewPreguntaToAttach : preguntaListNew) {
                preguntaListNewPreguntaToAttach = em.getReference(preguntaListNewPreguntaToAttach.getClass(), preguntaListNewPreguntaToAttach.getId());
                attachedPreguntaListNew.add(preguntaListNewPreguntaToAttach);
            }
            preguntaListNew = attachedPreguntaListNew;
            usuario.setPreguntaList(preguntaListNew);
            usuario = em.merge(usuario);
            for (Respuesta respuestaListNewRespuesta : respuestaListNew) {
                if (!respuestaListOld.contains(respuestaListNewRespuesta)) {
                    Usuario oldUsuarioIdOfRespuestaListNewRespuesta = respuestaListNewRespuesta.getUsuarioId();
                    respuestaListNewRespuesta.setUsuarioId(usuario);
                    respuestaListNewRespuesta = em.merge(respuestaListNewRespuesta);
                    if (oldUsuarioIdOfRespuestaListNewRespuesta != null && !oldUsuarioIdOfRespuestaListNewRespuesta.equals(usuario)) {
                        oldUsuarioIdOfRespuestaListNewRespuesta.getRespuestaList().remove(respuestaListNewRespuesta);
                        oldUsuarioIdOfRespuestaListNewRespuesta = em.merge(oldUsuarioIdOfRespuestaListNewRespuesta);
                    }
                }
            }
            for (Pregunta preguntaListNewPregunta : preguntaListNew) {
                if (!preguntaListOld.contains(preguntaListNewPregunta)) {
                    Usuario oldUsuarioIdOfPreguntaListNewPregunta = preguntaListNewPregunta.getUsuarioId();
                    preguntaListNewPregunta.setUsuarioId(usuario);
                    preguntaListNewPregunta = em.merge(preguntaListNewPregunta);
                    if (oldUsuarioIdOfPreguntaListNewPregunta != null && !oldUsuarioIdOfPreguntaListNewPregunta.equals(usuario)) {
                        oldUsuarioIdOfPreguntaListNewPregunta.getPreguntaList().remove(preguntaListNewPregunta);
                        oldUsuarioIdOfPreguntaListNewPregunta = em.merge(oldUsuarioIdOfPreguntaListNewPregunta);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = usuario.getId();
                if (findUsuario(id) == null) {
                    throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.");
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
            Usuario usuario;
            try {
                usuario = em.getReference(Usuario.class, id);
                usuario.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The usuario with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Respuesta> respuestaListOrphanCheck = usuario.getRespuestaList();
            for (Respuesta respuestaListOrphanCheckRespuesta : respuestaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Respuesta " + respuestaListOrphanCheckRespuesta + " in its respuestaList field has a non-nullable usuarioId field.");
            }
            List<Pregunta> preguntaListOrphanCheck = usuario.getPreguntaList();
            for (Pregunta preguntaListOrphanCheckPregunta : preguntaListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Usuario (" + usuario + ") cannot be destroyed since the Pregunta " + preguntaListOrphanCheckPregunta + " in its preguntaList field has a non-nullable usuarioId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(usuario);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Usuario> findUsuarioEntities() {
        return findUsuarioEntities(true, -1, -1);
    }

    public List<Usuario> findUsuarioEntities(int maxResults, int firstResult) {
        return findUsuarioEntities(false, maxResults, firstResult);
    }

    private List<Usuario> findUsuarioEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Usuario.class));
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

    public Usuario findUsuario(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Usuario.class, id);
        } finally {
            em.close();
        }
    }

    public int getUsuarioCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Usuario> rt = cq.from(Usuario.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
