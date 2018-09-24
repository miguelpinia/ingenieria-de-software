/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.miguel.proyecto.model.controller;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.miguel.proyecto.model.Alumno;
import com.miguel.proyecto.model.Profesor;
import com.miguel.proyecto.model.controller.exceptions.NonexistentEntityException;

/**
 *
 * @author miguel
 */
public class ProfesorJpaController implements Serializable {

    public ProfesorJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Profesor profesor) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Alumno idAlumno = profesor.getIdAlumno();
            if (idAlumno != null) {
                idAlumno = em.getReference(idAlumno.getClass(), idAlumno.getId());
                profesor.setIdAlumno(idAlumno);
            }
            em.persist(profesor);
            if (idAlumno != null) {
                idAlumno.getProfesorList().add(profesor);
                idAlumno = em.merge(idAlumno);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Profesor profesor) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Profesor persistentProfesor = em.find(Profesor.class, profesor.getId());
            Alumno idAlumnoOld = persistentProfesor.getIdAlumno();
            Alumno idAlumnoNew = profesor.getIdAlumno();
            if (idAlumnoNew != null) {
                idAlumnoNew = em.getReference(idAlumnoNew.getClass(), idAlumnoNew.getId());
                profesor.setIdAlumno(idAlumnoNew);
            }
            profesor = em.merge(profesor);
            if (idAlumnoOld != null && !idAlumnoOld.equals(idAlumnoNew)) {
                idAlumnoOld.getProfesorList().remove(profesor);
                idAlumnoOld = em.merge(idAlumnoOld);
            }
            if (idAlumnoNew != null && !idAlumnoNew.equals(idAlumnoOld)) {
                idAlumnoNew.getProfesorList().add(profesor);
                idAlumnoNew = em.merge(idAlumnoNew);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = profesor.getId();
                if (findProfesor(id) == null) {
                    throw new NonexistentEntityException("The profesor with id " + id + " no longer exists.");
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
            Profesor profesor;
            try {
                profesor = em.getReference(Profesor.class, id);
                profesor.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The profesor with id " + id + " no longer exists.", enfe);
            }
            Alumno idAlumno = profesor.getIdAlumno();
            if (idAlumno != null) {
                idAlumno.getProfesorList().remove(profesor);
                idAlumno = em.merge(idAlumno);
            }
            em.remove(profesor);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Profesor> findProfesorEntities() {
        return findProfesorEntities(true, -1, -1);
    }

    public List<Profesor> findProfesorEntities(int maxResults, int firstResult) {
        return findProfesorEntities(false, maxResults, firstResult);
    }

    private List<Profesor> findProfesorEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Profesor.class));
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

    public Profesor findProfesor(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Profesor.class, id);
        } finally {
            em.close();
        }
    }

    public int getProfesorCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Profesor> rt = cq.from(Profesor.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
