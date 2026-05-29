package com.is1.proyecto.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table("Catedra")
@IdName("id_catedra")
public class Catedra extends Model {
    
    public int getIdCatedra() {
        return getInteger("id_catedra");
    }

    public void setIdCatedra(int idCatedra) {
        set("id_catedra", idCatedra);
    }

    public int getAnio() {
        return getInteger("anio");
    }

    public void setAnio(int anio) {
        set("anio", anio);
    }

    public int getComision() {
        return getInteger("comision");
    }

    public void setComision(int comision) {
        set("comision", comision);
    }

    public int getIdMateria() {
        return getInteger("id_materia");
    }

    public void setIdMateria(int idMateria) {
        set("id_materia", idMateria);
    }
}
