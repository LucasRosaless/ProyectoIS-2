package com.is1.proyecto.models;

import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.Table;

@Table("profesores") // Esta anotación asocia explícitamente el modelo 'Profesor' con la tabla 'profesores' en la DB.
public class Profesor extends Model {

    // ActiveJDBC mapea automáticamente las columnas de la tabla 'profesores'
    // (como 'id', 'name', 'email', 'department', 'password', etc.) a los atributos de esta clase.
    // No necesitas declarar los campos aquí como variables de instancia,
    // ya que la clase Model base se encarga de la interacción con la base de datos.

    // Opcional: Puedes agregar métodos getters y setters si prefieres un acceso más tipado,
    // aunque los métodos genéricos de Model (getString(), set(), getInteger(), etc.) ya funcionan.

    public String getName() {
        return getString("name"); // Obtiene el valor de la columna 'name'
    }

    public void setName(String name) {
        set("name", name); // Establece el valor para la columna 'name'
    }

    public String getEmail() {
        return getString("email"); // Obtiene el valor de la columna 'email'
    }

    public void setEmail(String email) {
        set("email", email); // Establece el valor para la columna 'email'
    }

    public String getDepartment() {
        return getString("department"); // Obtiene el valor de la columna 'department'
    }

    public void setDepartment(String department) {
        set("department", department); // Establece el valor para la columna 'department'
    }

    public String getPassword() {
        return getString("password"); // Obtiene el valor de la columna 'password'
    }

    public void setPassword(String password) {
        set("password", password); // Establece el valor para la columna 'password'
    }

}

