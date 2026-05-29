package com.is1.proyecto.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table("Persona")
@IdName("dni")
public class Persona extends Model {
    
    public String getDni() {
        return getString("dni");
    }

    public void setDni(String dni) {
        set("dni", dni);
    }

    public String getNombre() {
        return getString("nombre");
    }

    public void setNombre(String nombre) {
        set("nombre", nombre);
    }

    public String getApellido() {
        return getString("apellido");
    }

    public void setApellido(String apellido) {
        set("apellido", apellido);
    }

    public String getTelefono() {
        return getString("telefono");
    }

    public void setTelefono(String telefono) {
        set("telefono", telefono);
    }

    public String getCorreo() {
        return getString("correo");
    }

    public void setCorreo(String correo) {
        set("correo", correo);
    }

    public String getUserLogin() {
        return getString("user_login");
    }

    public void setUserLogin(String userLogin) {
        set("user_login", userLogin);
    }

    public String getPassLogin() {
        return getString("pass_login");
    }

    public void setPassLogin(String passLogin) {
        set("pass_login", passLogin);
    }
}
