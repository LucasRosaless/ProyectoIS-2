package com.is1.proyecto.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table("Inscripcion")
@IdName("id_inscripcion")
public class Inscripcion extends Model {
    
    public int getIdInscripcion() {
        return getInteger("id_inscripcion");
    }

    public void setIdInscripcion(int idInscripcion) {
        set("id_inscripcion", idInscripcion);
    }

    public int getFechaInscripcion() {
        return getInteger("fecha_inscripcion");
    }

    public void setFechaInscripcion(int fechaInscripcion) {
        set("fecha_inscripcion", fechaInscripcion);
    }

    public String getEstadoInscripcion() {
        return getString("estado_inscripcion");
    }

    public void setEstadoInscripcion(String estadoInscripcion) {
        set("estado_inscripcion", estadoInscripcion);
    }

    public int getLegajoAlumno() {
        return getInteger("legajo_alumno");
    }

    public void setLegajoAlumno(int legajoAlumno) {
        set("legajo_alumno", legajoAlumno);
    }

    public int getIdCatedra() {
        return getInteger("id_catedra");
    }

    public void setIdCatedra(int idCatedra) {
        set("id_catedra", idCatedra);
    }
}
