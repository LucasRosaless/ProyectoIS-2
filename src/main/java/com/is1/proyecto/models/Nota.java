package com.is1.proyecto.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table("Nota")
@IdName("id_nota")
public class Nota extends Model {
    
    public int getIdNota() {
        return getInteger("id_nota");
    }

    public void setIdNota(int idNota) {
        set("id_nota", idNota);
    }

    public int getValor() {
        return getInteger("valor");
    }

    public void setValor(int valor) {
        set("valor", valor);
    }

    public String getTipoNota() {
        return getString("tipo_nota");
    }

    public void setTipoNota(String tipoNota) {
        set("tipo_nota", tipoNota);
    }

    public int getFecha() {
        return getInteger("fecha");
    }

    public void setFecha(int fecha) {
        set("fecha", fecha);
    }

    public int getIdInscripcion() {
        return getInteger("id_inscripcion");
    }

    public void setIdInscripcion(int idInscripcion) {
        set("id_inscripcion", idInscripcion);
    }
}
