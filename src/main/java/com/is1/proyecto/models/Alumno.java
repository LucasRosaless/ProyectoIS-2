package com.is1.proyecto.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table("Alumno")
@IdName("legajo")
public class Alumno extends Model {
    
    public int getLegajo() {
        return getInteger("legajo");
    }

    public void setLegajo(int legajo) {
        set("legajo", legajo);
    }

    public String getDniPersona() {
        return getString("dni_persona");
    }

    public void setDniPersona(String dniPersona) {
        set("dni_persona", dniPersona);
    }

    public String getTipoAlumno() {
        return getString("tipo_alumno");
    }

    public void setTipoAlumno(String tipoAlumno) {
        set("tipo_alumno", tipoAlumno);
    }
}
