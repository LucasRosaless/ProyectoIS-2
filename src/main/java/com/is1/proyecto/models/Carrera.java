package com.is1.proyecto.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table("Carrera")
@IdName("id_carrera")
public class Carrera extends Model {
    
    public int getIdCarrera() {
        return getInteger("id_carrera");
    }

    public void setIdCarrera(int idCarrera) {
        set("id_carrera", idCarrera);
    }

    public int getCodigo() {
        return getInteger("codigo");
    }

    public void setCodigo(int codigo) {
        set("codigo", codigo);
    }

    public String getNombre() {
        return getString("nombre");
    }

    public void setNombre(String nombre) {
        set("nombre", nombre);
    }

    public int getDuracionAnios() {
        return getInteger("duracion_anios");
    }

    public void setDuracionAnios(int duracionAnios) {
        set("duracion_anios", duracionAnios);
    }
}
