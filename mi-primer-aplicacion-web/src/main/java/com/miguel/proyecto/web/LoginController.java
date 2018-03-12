/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.miguel.proyecto.web;

import com.miguel.proyecto.model.EntityProvider;
import com.miguel.proyecto.model.LoginJpaController;
import java.util.Locale;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author miguel
 */
@ManagedBean
@SessionScoped
public class LoginController {

    private EntityManagerFactory emf;
    private LoginJpaController jpaController;
    private Usuario usuario;

    public LoginController() {
        FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale("es-Mx"));
        emf = EntityProvider.provider();
        jpaController = new LoginJpaController(emf);
        usuario = new Usuario();
    }

    public Usuario getusuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String canLogin() {
        boolean logged = jpaController.canLogin(usuario.getUsuario(), usuario.getContraseña());
        if (logged) {
            return "inicio";
        }
        return "registro";
    }

}
