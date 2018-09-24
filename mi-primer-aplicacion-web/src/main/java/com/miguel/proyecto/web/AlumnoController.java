/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.miguel.proyecto.web;

import java.util.List;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

import com.miguel.proyecto.model.Alumno;
import com.miguel.proyecto.model.PersistenceUtil;
import com.miguel.proyecto.model.controller.AlumnoJpaController;

/**
 *
 * @author miguel
 */
@ManagedBean
@RequestScoped
public class AlumnoController {

    private Alumno alumno;
    private AlumnoJpaController ajp;

    /**
     * Creates a new instance of AlumnoController
     */
    public AlumnoController() {
        ajp = new AlumnoJpaController(PersistenceUtil.getEntityManagerFactory());
        alumno = new Alumno();
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public void setAlumno(Alumno a) {
        alumno = a;
    }

    public String addAlumno() {
        ajp.create(alumno);
        return "lista";
    }

    public List<Alumno> getRegistrados() {
        return ajp.findAlumnoEntities();
    }

}
