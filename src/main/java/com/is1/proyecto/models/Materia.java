package com.is1.proyecto.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table("Materia")
@IdName("id_materia")
public class Materia extends Model {
    
    public int getIdMateria() {
        return getInteger("id_materia");
    }

    public void setIdMateria(int idMateria) {
        set("id_materia", idMateria);
    }

    public String getCodigo() {
        return getString("codigo");
    }

    public void setCodigo(String codigo) {
        set("codigo", codigo);
    }

    public String getNombre() {
        return getString("nombre");
    }

    public void setNombre(String nombre) {
        set("nombre", nombre);
    }

    public String getPeriodo() {
        return getString("periodo");
    }

    public void setPeriodo(String periodo) {
        set("periodo", periodo);
    }

    public int getIdPlan() {
        return getInteger("id_plan");
    }

    public void setIdPlan(int idPlan) {
        set("id_plan", idPlan);
    }
}
