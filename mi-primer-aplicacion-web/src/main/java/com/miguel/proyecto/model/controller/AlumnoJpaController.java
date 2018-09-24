/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.miguel.proyecto.model.controller;

import java.io.Serializable;

import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.miguel.proyecto.model.Profesor;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.miguel.proyecto.model.Alumno;
import com.miguel.proyecto.model.controller.exceptions.IllegalOrphanException;
import com.miguel.proyecto.model.controller.exceptions.NonexistentEntityException;

/**
 *
 * @author miguel
 */
public class AlumnoJpaController implements Serializable {

    public AlumnoJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Alumno alumno) {
        if (alumno.getProfesorList() == null) {
            alumno.setProfesorList(new ArrayList<Profesor>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            List<Profesor> attachedProfesorList = new ArrayList<Profesor>();
            for (Profesor profesorListProfesorToAttach : alumno.getProfesorList()) {
                profesorListProfesorToAttach = em.getReference(profesorListProfesorToAttach.getClass(), profesorListProfesorToAttach.getId());
                attachedProfesorList.add(profesorListProfesorToAttach);
            }
            alumno.setProfesorList(attachedProfesorList);
            em.persist(alumno);
            for (Profesor profesorListProfesor : alumno.getProfesorList()) {
                Alumno oldIdAlumnoOfProfesorListProfesor = profesorListProfesor.getIdAlumno();
                profesorListProfesor.setIdAlumno(alumno);
                profesorListProfesor = em.merge(profesorListProfesor);
                if (oldIdAlumnoOfProfesorListProfesor != null) {
                    oldIdAlumnoOfProfesorListProfesor.getProfesorList().remove(profesorListProfesor);
                    oldIdAlumnoOfProfesorListProfesor = em.merge(oldIdAlumnoOfProfesorListProfesor);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Alumno alumno) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Alumno persistentAlumno = em.find(Alumno.class, alumno.getId());
            List<Profesor> profesorListOld = persistentAlumno.getProfesorList();
            List<Profesor> profesorListNew = alumno.getProfesorList();
            List<String> illegalOrphanMessages = null;
            for (Profesor profesorListOldProfesor : profesorListOld) {
                if (!profesorListNew.contains(profesorListOldProfesor)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Profesor " + profesorListOldProfesor + " since its idAlumno field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            List<Profesor> attachedProfesorListNew = new ArrayList<Profesor>();
            for (Profesor profesorListNewProfesorToAttach : profesorListNew) {
                profesorListNewProfesorToAttach = em.getReference(profesorListNewProfesorToAttach.getClass(), profesorListNewProfesorToAttach.getId());
                attachedProfesorListNew.add(profesorListNewProfesorToAttach);
            }
            profesorListNew = attachedProfesorListNew;
            alumno.setProfesorList(profesorListNew);
            alumno = em.merge(alumno);
            for (Profesor profesorListNewProfesor : profesorListNew) {
                if (!profesorListOld.contains(profesorListNewProfesor)) {
                    Alumno oldIdAlumnoOfProfesorListNewProfesor = profesorListNewProfesor.getIdAlumno();
                    profesorListNewProfesor.setIdAlumno(alumno);
                    profesorListNewProfesor = em.merge(profesorListNewProfesor);
                    if (oldIdAlumnoOfProfesorListNewProfesor != null && !oldIdAlumnoOfProfesorListNewProfesor.equals(alumno)) {
                        oldIdAlumnoOfProfesorListNewProfesor.getProfesorList().remove(profesorListNewProfesor);
                        oldIdAlumnoOfProfesorListNewProfesor = em.merge(oldIdAlumnoOfProfesorListNewProfesor);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = alumno.getId();
                if (findAlumno(id) == null) {
                    throw new NonexistentEntityException("The alumno with id " + id + " no longer exists.");
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
            Alumno alumno;
            try {
                alumno = em.getReference(Alumno.class, id);
                alumno.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The alumno with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            List<Profesor> profesorListOrphanCheck = alumno.getProfesorList();
            for (Profesor profesorListOrphanCheckProfesor : profesorListOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Alumno (" + alumno + ") cannot be destroyed since the Profesor " + profesorListOrphanCheckProfesor + " in its profesorList field has a non-nullable idAlumno field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(alumno);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Alumno> findAlumnoEntities() {
        return findAlumnoEntities(true, -1, -1);
    }

    public List<Alumno> findAlumnoEntities(int maxResults, int firstResult) {
        return findAlumnoEntities(false, maxResults, firstResult);
    }

    private List<Alumno> findAlumnoEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Alumno.class));
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

    public Alumno findAlumno(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Alumno.class, id);
        } finally {
            em.close();
        }
    }

    public int getAlumnoCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Alumno> rt = cq.from(Alumno.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }

}
