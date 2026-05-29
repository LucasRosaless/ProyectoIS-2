package com.is1.proyecto.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table("Plan_estudio")
@IdName("id_plan")
public class PlanEstudio extends Model {
    
    public int getIdPlan() {
        return getInteger("id_plan");
    }

    public void setIdPlan(int idPlan) {
        set("id_plan", idPlan);
    }

    public String getResolucion() {
        return getString("resolucion");
    }

    public void setResolucion(String resolucion) {
        set("resolucion", resolucion);
    }

    public int getAnioVigencia() {
        return getInteger("anio_vigencia");
    }

    public void setAnioVigencia(int anioVigencia) {
        set("anio_vigencia", anioVigencia);
    }

    public int getEstado() {
        return getInteger("estado");
    }

    public void setEstado(int estado) {
        set("estado", estado);
    }

    public int getIdCarrera() {
        return getInteger("id_carrera");
    }

    public void setIdCarrera(int idCarrera) {
        set("id_carrera", idCarrera);
    }
}
