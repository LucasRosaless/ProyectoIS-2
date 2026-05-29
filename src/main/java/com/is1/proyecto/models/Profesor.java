package com.is1.proyecto.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table("Profesor")
@IdName("legajo_docente")
public class Profesor extends Model {
    
    public String getLegajoDocente() {
        return getString("legajo_docente");
    }

    public void setLegajoDocente(String legajoDocente) {
        set("legajo_docente", legajoDocente);
    }

    public String getDniPersona() {
        return getString("dni_persona");
    }

    public void setDniPersona(String dniPersona) {
        set("dni_persona", dniPersona);
    }
}
