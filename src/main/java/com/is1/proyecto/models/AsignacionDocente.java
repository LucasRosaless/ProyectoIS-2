package com.is1.proyecto.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.CompositePK;
import org.javalite.activejdbc.annotations.Table;

@Table("Asignacion_Docente")
@CompositePK({"legajo_docente", "id_catedra"})
public class AsignacionDocente extends Model {

    public String getLegajoDocente() {
        return getString("legajo_docente");
    }

    public void setLegajoDocente(String legajoDocente) {
        set("legajo_docente", legajoDocente);
    }

    public int getIdCatedra() {
        return getInteger("id_catedra");
    }

    public void setIdCatedra(int idCatedra) {
        set("id_catedra", idCatedra);
    }

    public String getRol() {
        return getString("rol");
    }

    public void setRol(String rol) {
        set("rol", rol);
    }

    public int getFechaAsignacion() {
        return getInteger("fecha_asignacion");
    }

    public void setFechaAsignacion(int fechaAsignacion) {
        set("fecha_asignacion", fechaAsignacion);
    }
}
