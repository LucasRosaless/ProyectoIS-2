package com.is1.proyecto;

import org.javalite.activejdbc.Base;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.is1.proyecto.models.*;

class ModelTest {

    @BeforeEach
    void setUp() {
        // Abrir conexión a la base de datos de desarrollo
        Base.open("org.sqlite.JDBC", "jdbc:sqlite:./db/dev.db", "", "");
        // Iniciar una transacción para revertir cualquier cambio al final de cada test
        Base.openTransaction();
    }

    @AfterEach
    void tearDown() {
        // Deshacer todos los cambios realizados en el test para dejar la base de datos limpia
        Base.rollbackTransaction();
        Base.close();
    }

    @Test
    void testModelCrud() {
        // 1. Probar Modelo Persona
        Persona p = new Persona();
        p.set("dni", "12345678");
        p.set("nombre", "Luca");
        p.set("apellido", "Porta");
        p.set("correo", "luca@example.com");
        p.set("user_login", "luca");
        p.set("pass_login", "password123");
        p.insert();

        Persona foundPersona = Persona.findFirst("dni = ?", "12345678");
        assertNotNull(foundPersona, "La persona debería existir en la BD");
        assertEquals("Luca", foundPersona.get("nombre"));
        assertEquals("Porta", foundPersona.get("apellido"));

        // 2. Probar Modelo Carrera
        Carrera c = new Carrera();
        c.set("id_carrera", 9999);
        c.set("codigo", 101);
        c.set("nombre", "Ingeniería en Sistemas");
        c.set("duracion_anios", 5);
        c.insert();

        Carrera foundCarrera = Carrera.findFirst("id_carrera = ?", 9999);
        assertNotNull(foundCarrera, "La carrera debería existir en la BD");
        assertEquals("Ingeniería en Sistemas", foundCarrera.get("nombre"));

        // 3. Probar Modelo PlanEstudio
        PlanEstudio plan = new PlanEstudio();
        plan.set("id_plan", 8888);
        plan.set("resolucion", "Res 123/26");
        plan.set("anio_vigencia", 2026);
        plan.set("estado", 1);
        plan.set("id_carrera", 9999); // Clave foránea referenciando a Carrera
        plan.insert();

        PlanEstudio foundPlan = PlanEstudio.findFirst("id_plan = ?", 8888);
        assertNotNull(foundPlan, "El plan de estudio debería existir en la BD");
        assertEquals("Res 123/26", foundPlan.get("resolucion"));
    }
}
