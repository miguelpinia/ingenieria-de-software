package com.miguel.proyecto.web;

import com.miguel.proyecto.model.EntityProvider;
import com.miguel.proyecto.model.Login;
import com.miguel.proyecto.model.LoginJpaController;
import com.miguel.proyecto.model.Usuario;
import com.miguel.proyecto.model.UsuarioJpaController;
import java.util.Locale;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManagerFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 ,*
 ,* @author miguel
 ,*/
@ManagedBean
@RequestScoped
public class RegisterController {

    private final EntityManagerFactory emf;
    private String usuario;
    private String nombre;
    private String correo;
    private String contraseña;
    private String confirmacion;
    private UploadedFile fotografia;

    public RegisterController() {
        emf = EntityProvider.provider();
        FacesContext.getCurrentInstance().getViewRoot().setLocale(new Locale("es-Mx"));
    }

    public UploadedFile getFotografia() {
        return fotografia;
    }

    public void setFotografia(UploadedFile fotografia) {
        this.fotografia = fotografia;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getConfirmacion() {
        return confirmacion;
    }

    public void setConfirmacion(String confirmacion) {
        this.confirmacion = confirmacion;
    }

    public void fileUploadListener(FileUploadEvent e) {
        this.fotografia = e.getFile();
    }

    public String addUser() {
        if (!contraseña.equals(confirmacion)) {
            FacesContext.getCurrentInstance().addMessage(null,
                                                         new FacesMessage(FacesMessage.SEVERITY_ERROR,
                                                                          "Fallo de registro: Las contraseñas deben coincidir", ""));
        } else {
            LoginJpaController ljpa = new LoginJpaController(emf);
            UsuarioJpaController ujpa = new UsuarioJpaController(emf);

            Login login = new Login();
            login.setUsuario(usuario);
            login.setPassword(contraseña);
            ljpa.create(login);
            login = ljpa.findLoginByUsuario(usuario);

            Usuario user = new Usuario();
            user.setLoginId(login.getId());
            user.setNombre(nombre);
            user.setCorreo(correo);
            if (fotografia != null) {
                user.setFotografia(fotografia.getContents());
            }
            ujpa.create(user);

            FacesContext.getCurrentInstance().addMessage(null,
                                                         new FacesMessage(FacesMessage.SEVERITY_INFO,
                                                                          "Felicidades, el registro se ha realizado correctamente", ""));
        }
        return "index?faces-redirect=true";
    }

}
