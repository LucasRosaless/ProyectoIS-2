package com.is1.proyecto.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table("Administrador")
@IdName("dni_persona")
public class Administrador extends Model {
    
    public String getDniPersona() {
        return getString("dni_persona");
    }

    public void setDniPersona(String dniPersona) {
        set("dni_persona", dniPersona);
    }

    public String getCargoAdministrative() {
        return getString("cargo_administrative");
    }

    public void setCargoAdministrative(String cargoAdministrative) {
        set("cargo_administrative", cargoAdministrative);
    }
}
