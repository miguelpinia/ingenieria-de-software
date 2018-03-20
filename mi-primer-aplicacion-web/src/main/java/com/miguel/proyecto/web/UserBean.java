/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.miguel.proyecto.web;

import com.miguel.proyecto.model.Login;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import static javax.faces.context.FacesContext.getCurrentInstance;

/**
 *
 * @author miguel
 */
@ManagedBean
@SessionScoped
public class UserBean {

    /**
     * Creates a new instance of UserBean
     */
    public UserBean() {
    }

    public boolean isLogged() {
        FacesContext context = getCurrentInstance();
        Login l = (Login) context.getExternalContext().getSessionMap().get("usuario");
        return l != null;
    }

    public Login getUsuario() {
        FacesContext context = getCurrentInstance();
        return (Login) context.getExternalContext().getSessionMap().get("usuario");
    }

}
