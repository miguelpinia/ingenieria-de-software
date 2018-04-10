/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.miguel.proyecto.web;

import com.miguel.proyecto.model.Pregunta;
import com.miguel.proyecto.model.Usuario;
import java.util.Iterator;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import static javax.faces.context.FacesContext.getCurrentInstance;

/**
 *
 * @author miguel
 */
@ManagedBean
@ViewScoped
public class PreguntasController {

    private final Iterator<Pregunta> preguntas;
    private boolean accesado;
    private String bloquePreguntas = "";
    private String usuario;

    /**
     * Creates a new instance of PreguntasController
     */
    public PreguntasController() {
        FacesContext context = getCurrentInstance();
        Usuario u = (Usuario) context.getExternalContext().getSessionMap().get("datos");
        usuario = u.getNombre();
        preguntas = u.getPreguntaList().iterator();
        accesado = false;
    }

    public String getPregunta() {
        if (!accesado) {
            accesado = true;
            return "";
        }
        if (!preguntas.hasNext()) {
            return bloquePreguntas + "<br/>No hay más preguntas";
        }
        bloquePreguntas += String.format(PREGUNTA, usuario, preguntas.next().getContenido(), "Una respuesta bien larga");
        return bloquePreguntas;
    }

    private final static String PREGUNTA = "<div class=\"media text-muted pt-3\">\n"
                                           + "          <img data-src=\"holder.js/32x32?theme=thumb&amp;bg=007bff&amp;fg=007bff&amp;size=1\" alt=\"32x32\" class=\"mr-2 rounded\" src=\"data:image/svg+xml;charset=UTF-8,%%3Csvg%%20width%%3D%%2232%%22%%20height%%3D%%2232%%22%%20xmlns%%3D%%22http%%3A%%2F%%2Fwww.w3.org%%2F2000%%2Fsvg%%22%%20viewBox%%3D%%220%%200%%2032%%2032%%22%%20preserveAspectRatio%%3D%%22none%%22%%3E%%3Cdefs%%3E%%3Cstyle%%20type%%3D%%22text%%2Fcss%%22%%3E%%23holder_162ad2b92c0%%20text%%20%%7B%%20fill%%3A%%23007bff%%3Bfont-weight%%3Abold%%3Bfont-family%%3AArial%%2C%%20Helvetica%%2C%%20Open%%20Sans%%2C%%20sans-serif%%2C%%20monospace%%3Bfont-size%%3A2pt%%20%%7D%%20%%3C%%2Fstyle%%3E%%3C%%2Fdefs%%3E%%3Cg%%20id%%3D%%22holder_162ad2b92c0%%22%%3E%%3Crect%%20width%%3D%%2232%%22%%20height%%3D%%2232%%22%%20fill%%3D%%22%%23007bff%%22%%3E%%3C%%2Frect%%3E%%3Cg%%3E%%3Ctext%%20x%%3D%%2211.046875%%22%%20y%%3D%%2217.2%%22%%3E32x32%%3C%%2Ftext%%3E%%3C%%2Fg%%3E%%3C%%2Fg%%3E%%3C%%2Fsvg%%3E\" data-holder-rendered=\"true\" style=\"width: 32px; height: 32px;\">\n"
                                           + "          <div class=\"media-body pb-3 mb-0 small lh-125 border-bottom border-gray\">\n"
                                           + "            <div class=\"d-flex justify-content-between align-items-center w-100\">\n"
                                           + "              <strong class=\"text-gray-dark\">%s</strong>\n"
                                           + "              <span class=\"d-block\">%s</span>\n"
                                           + "              <a href=\"#\">Follow</a>\n"
                                           + "            </div>\n"
                                           + "            <span class=\"d-block\">@%s</span>\n"
                                           + "          </div>\n"
                                           + "        </div>";
}
