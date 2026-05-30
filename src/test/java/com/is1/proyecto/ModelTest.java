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

    @Test
    void testAlumnoCreation() {
        // 1. Crear Persona base para el alumno
        Persona p = new Persona();
        p.set("dni", "87654321");
        p.set("nombre", "Juan");
        p.set("apellido", "Perez");
        p.set("correo", "juan@example.com");
        p.insert();

        // 2. Crear Alumno asociado
        Alumno alu = new Alumno();
        alu.set("legajo", 55555);
        alu.set("dni_persona", "87654321");
        alu.set("tipo_alumno", "INGRESANTE");
        alu.insert();

        // 3. Validar recuperación de datos
        Alumno foundAlu = Alumno.findFirst("legajo = ?", 55555);
        assertNotNull(foundAlu, "El alumno debería estar en la base de datos");
        assertEquals("87654321", foundAlu.get("dni_persona"));
        assertEquals("INGRESANTE", foundAlu.get("tipo_alumno"));
    }

    @Test
    void testMateriaPlanAssociation() {
        // 1. Crear Carrera
        Carrera c = new Carrera();
        c.set("id_carrera", 7777);
        c.set("codigo", 102);
        c.set("nombre", "Licenciatura en Sistemas");
        c.set("duracion_anios", 4);
        c.insert();

        // 2. Crear Plan de Estudio
        PlanEstudio plan = new PlanEstudio();
        plan.set("id_plan", 6666);
        plan.set("resolucion", "Res 456/26");
        plan.set("anio_vigencia", 2026);
        plan.set("estado", 1);
        plan.set("id_carrera", 7777);
        plan.insert();

        // 3. Crear Materia vinculada al Plan
        Materia mat = new Materia();
        mat.set("id_materia", 5555);
        mat.set("codigo", "MAT101");
        mat.set("nombre", "Matemática I");
        mat.set("periodo", "CUATRIMESTRAL");
        mat.set("id_plan", 6666);
        mat.insert();

        // 4. Validar integridad de Materia
        Materia foundMat = Materia.findFirst("id_materia = ?", 5555);
        assertNotNull(foundMat, "La materia debería estar en la base de datos");
        assertEquals("MAT101", foundMat.get("codigo"));
        assertEquals("Matemática I", foundMat.get("nombre"));
        assertEquals(6666, foundMat.getInteger("id_plan"));
    }
}
