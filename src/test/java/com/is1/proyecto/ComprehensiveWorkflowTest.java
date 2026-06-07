package com.is1.proyecto;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.Model;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.is1.proyecto.models.*;
import java.util.List;
import java.util.Map;

class ComprehensiveWorkflowTest {

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
    void testCompleteAcademicAndEnrollmentWorkflow() {
        // --- 1. CONFIGURACIÓN DEL PLAN DE ESTUDIOS ---
        // Crear una nueva Carrera
        int idCarrera = 9901;
        Carrera carrera = new Carrera();
        carrera.set("id_carrera", idCarrera);
        carrera.set("codigo", 901);
        carrera.set("nombre", "Ingeniería Aeroespacial");
        carrera.set("duracion_anios", 5);
        carrera.insert();

        // Crear un Plan de Estudios asociado a la Carrera
        int idPlan = 9902;
        PlanEstudio plan = new PlanEstudio();
        plan.set("id_plan", idPlan);
        plan.set("resolucion", "Res Aero 456/26");
        plan.set("anio_vigencia", 2026);
        plan.set("estado", 1); // 1 = Activo
        plan.set("id_carrera", idCarrera);
        plan.insert();

        // Crear materias: Física I (introductoria) y Física II (requiere Física I)
        int idFisicaI = 9903;
        Materia fisicaI = new Materia();
        fisicaI.set("id_materia", idFisicaI);
        fisicaI.set("codigo", "FIS1");
        fisicaI.set("nombre", "Física I");
        fisicaI.set("periodo", "CUATRIMESTRAL");
        fisicaI.set("id_plan", idPlan);
        fisicaI.insert();

        int idFisicaII = 9904;
        Materia fisicaII = new Materia();
        fisicaII.set("id_materia", idFisicaII);
        fisicaII.set("codigo", "FIS2");
        fisicaII.set("nombre", "Física II");
        fisicaII.set("periodo", "CUATRIMESTRAL");
        fisicaII.set("id_plan", idPlan);
        fisicaII.insert();

        // Establecer la relación correlativa: Física II requiere Física I aprobada
        Base.exec("INSERT INTO Correlativas_previas (id_materia, id_materia_correlativa) VALUES (?, ?)", idFisicaII, idFisicaI);

        // Crear cátedras (comisiones) para ambas materias
        int idCatedraFisicaI = 9905;
        Catedra catFisicaI = new Catedra();
        catFisicaI.set("id_catedra", idCatedraFisicaI);
        catFisicaI.set("anio", 2026);
        catFisicaI.set("comision", 1);
        catFisicaI.set("id_materia", idFisicaI);
        catFisicaI.insert();

        int idCatedraFisicaII = 9906;
        Catedra catFisicaII = new Catedra();
        catFisicaII.set("id_catedra", idCatedraFisicaII);
        catFisicaII.set("anio", 2026);
        catFisicaII.set("comision", 2);
        catFisicaII.set("id_materia", idFisicaII);
        catFisicaII.insert();


        // --- 2. CONFIGURACIÓN DEL ESTUDIANTE ---
        // Crear Persona
        String dniAlumno = "99000111";
        Persona persAlumno = new Persona();
        persAlumno.set("dni", dniAlumno);
        persAlumno.set("nombre", "Carlos");
        persAlumno.set("apellido", "Astron");
        persAlumno.set("correo", "carlos.a@aero.com");
        persAlumno.insert();

        // Crear Alumno vinculado al Plan Aeroespacial
        int legajoAlumno = 99555;
        Alumno alumno = new Alumno();
        alumno.set("legajo", legajoAlumno);
        alumno.set("dni_persona", dniAlumno);
        alumno.set("tipo_alumno", "INGRESANTE");
        alumno.setIdPlan(idPlan);
        alumno.insert();


        // --- 3. SIMULACIÓN DE INSCRIPCIÓN Y VALIDACIÓN DE CORRELATIVAS ---
        // VALIDACIÓN: Intentar inscribir al alumno en Física II (Debería fallar porque no aprobó Física I)
        List<Map> correlativasFisII = Base.findAll(
            "SELECT id_materia_correlativa FROM Correlativas_previas WHERE id_materia = ?", idFisicaII);
        
        assertFalse(correlativasFisII.isEmpty(), "Física II debe tener una correlativa registrada");
        
        boolean cumpleCorrelativaFisII = true;
        for (Map corr : correlativasFisII) {
            int idCorr = ((Number) corr.get("id_materia_correlativa")).intValue();
            boolean correlativaAprobada = false;
            List<Model> catedrasCorr = Catedra.find("id_materia = ?", idCorr);
            for (Model catCorr : catedrasCorr) {
                Inscripcion inscCorr = (Inscripcion) Inscripcion.findFirst(
                    "legajo_alumno = ? AND id_catedra = ? AND estado_inscripcion = 'APROBADA'",
                    legajoAlumno, catCorr.getInteger("id_catedra"));
                if (inscCorr != null) {
                    correlativaAprobada = true;
                    break;
                }
            }
            if (!correlativaAprobada) {
                cumpleCorrelativaFisII = false;
                break;
            }
        }
        
        // Assert: No cumple correlativas para Física II aún
        assertFalse(cumpleCorrelativaFisII, "El estudiante no debería poder inscribirse a Física II sin haber aprobado Física I");

        // INSCRIPCIÓN VÁLIDA: Inscribirse a Física I (No tiene correlativas previas)
        int idInscFisI = 99101;
        Inscripcion inscFisI = new Inscripcion();
        inscFisI.set("id_inscripcion", idInscFisI);
        inscFisI.set("fecha_inscripcion", (int) (System.currentTimeMillis() / 1000));
        inscFisI.set("estado_inscripcion", "EN_CURSADA");
        inscFisI.set("legajo_alumno", legajoAlumno);
        inscFisI.set("id_catedra", idCatedraFisicaI);
        inscFisI.insert();

        // Verificar que está inscripto correctamente
        Inscripcion checkInscFisI = Inscripcion.findFirst("id_inscripcion = ?", idInscFisI);
        assertNotNull(checkInscFisI);
        assertEquals("EN_CURSADA", checkInscFisI.getEstadoInscripcion());


        // --- 4. SIMULACIÓN DE EXÁMENES Y CARGA DE NOTAS ---
        // Registrar nota de parcial
        int idNotaParcial = 99201;
        Nota notaParcial = new Nota();
        notaParcial.set("id_nota", idNotaParcial);
        notaParcial.set("valor", 8);
        notaParcial.set("tipo_nota", "PARCIAL");
        notaParcial.set("fecha", (int) (System.currentTimeMillis() / 1000));
        notaParcial.set("id_inscripcion", idInscFisI);
        notaParcial.insert();

        // Registrar nota de examen final (Aprobado con 9)
        int idNotaFinal = 99202;
        Nota notaFinal = new Nota();
        notaFinal.set("id_nota", idNotaFinal);
        notaFinal.set("valor", 9);
        notaFinal.set("tipo_nota", "FINAL");
        notaFinal.set("fecha", (int) (System.currentTimeMillis() / 1000));
        notaFinal.set("id_inscripcion", idInscFisI);
        notaFinal.insert();

        // Promover el estado de la inscripción a APROBADA
        inscFisI.set("estado_inscripcion", "APROBADA");
        inscFisI.saveIt();


        // --- 5. COMPROBAR HABILITACIÓN DE MATERIAS SUBSIGUIENTES ---
        // Volver a evaluar si cumple correlativa para Física II
        boolean cumpleCorrelativaFisIINuevo = true;
        for (Map corr : correlativasFisII) {
            int idCorr = ((Number) corr.get("id_materia_correlativa")).intValue();
            boolean correlativaAprobada = false;
            List<Model> catedrasCorr = Catedra.find("id_materia = ?", idCorr);
            for (Model catCorr : catedrasCorr) {
                Inscripcion inscCorr = (Inscripcion) Inscripcion.findFirst(
                    "legajo_alumno = ? AND id_catedra = ? AND estado_inscripcion = 'APROBADA'",
                    legajoAlumno, catCorr.getInteger("id_catedra"));
                if (inscCorr != null) {
                    correlativaAprobada = true;
                    break;
                }
            }
            if (!correlativaAprobada) {
                cumpleCorrelativaFisIINuevo = false;
                break;
            }
        }

        // Assert: Ahora sí cumple correlativas para Física II
        assertTrue(cumpleCorrelativaFisIINuevo, "El estudiante ahora debería estar habilitado para cursar Física II");

        // Inscribirse en Física II exitosamente
        int idInscFisII = 99102;
        Inscripcion inscFisII = new Inscripcion();
        inscFisII.set("id_inscripcion", idInscFisII);
        inscFisII.set("fecha_inscripcion", (int) (System.currentTimeMillis() / 1000));
        inscFisII.set("estado_inscripcion", "EN_CURSADA");
        inscFisII.set("legajo_alumno", legajoAlumno);
        inscFisII.set("id_catedra", idCatedraFisicaII);
        inscFisII.insert();

        // Verificar inscripción de Física II
        Inscripcion checkInscFisII = Inscripcion.findFirst("id_inscripcion = ?", idInscFisII);
        assertNotNull(checkInscFisII);
        assertEquals("EN_CURSADA", checkInscFisII.getEstadoInscripcion());
    }

    @Test
    void testTeacherAssignmentAndCommissions() {
        // Crear una cátedra de prueba
        int idMateria = 9920;
        Materia mat = new Materia();
        mat.set("id_materia", idMateria);
        mat.set("codigo", "TEST101");
        mat.set("nombre", "Materia de Prueba");
        mat.set("periodo", "CUATRIMESTRAL");
        mat.set("id_plan", 1); // Utiliza plan existente 1
        mat.insert();

        int idCatedra = 9921;
        Catedra cat = new Catedra();
        cat.set("id_catedra", idCatedra);
        cat.set("anio", 2026);
        cat.set("comision", 3);
        cat.set("id_materia", idMateria);
        cat.insert();

        // Crear un Profesor
        String dniProf = "99333222";
        Persona persProf = new Persona();
        persProf.set("dni", dniProf);
        persProf.set("nombre", "Dr. Richard");
        persProf.set("apellido", "Feynman");
        persProf.set("correo", "feynman@caltech.edu");
        persProf.insert();

        String legajoDocente = "PROF_FEYNMAN_99";
        Profesor prof = new Profesor();
        prof.set("legajo_docente", legajoDocente);
        prof.set("dni_persona", dniProf);
        prof.insert();

        // Asignar el docente a la cátedra con rol RESPONSABLE
        Base.exec("INSERT INTO Asignacion_Docente (legajo_docente, id_catedra, rol, fecha_asignacion) VALUES (?, ?, ?, ?)",
            legajoDocente, idCatedra, "RESPONSABLE", (int)(System.currentTimeMillis() / 1000));

        // Verificar la asignación
        List<Map> asignaciones = Base.findAll(
            "SELECT * FROM Asignacion_Docente WHERE legajo_docente = ? AND id_catedra = ?", legajoDocente, idCatedra);
        
        assertEquals(1, asignaciones.size());
        assertEquals("RESPONSABLE", asignaciones.get(0).get("rol"));
    }

    @Test
    void testDuplicateEnrollmentProtection() {
        // Crear un alumno de prueba
        int legajoTest = 99666;
        Persona pers = new Persona();
        pers.set("dni", "99666777");
        pers.set("nombre", "Maria");
        pers.set("apellido", "Curie");
        pers.insert();

        Alumno alu = new Alumno();
        alu.set("legajo", legajoTest);
        alu.set("dni_persona", "99666777");
        alu.set("tipo_alumno", "AVANZADO");
        alu.set("id_plan", 1);
        alu.insert();

        // Crear una cátedra
        int idCatedra = 9960;
        Catedra cat = new Catedra();
        cat.set("id_catedra", idCatedra);
        cat.set("anio", 2026);
        cat.set("comision", 1);
        cat.set("id_materia", 1); // Materia id 1 (Analisis Mat I)
        cat.insert();

        // Primera inscripción: Correcta
        Inscripcion insc1 = new Inscripcion();
        insc1.set("id_inscripcion", 99601);
        insc1.set("fecha_inscripcion", (int) (System.currentTimeMillis() / 1000));
        insc1.set("estado_inscripcion", "EN_CURSADA");
        insc1.set("legajo_alumno", legajoTest);
        insc1.set("id_catedra", idCatedra);
        insc1.insert();

        // Simular validación de inscripción duplicada (segunda inscripción)
        // en la misma materia (usando la lógica de App.java)
        int idMateria = cat.getInteger("id_materia");
        List<Model> catedrasDeMateria = Catedra.find("id_materia = ?", idMateria);
        
        boolean yaInscripto = false;
        for (Model c : catedrasDeMateria) {
            Inscripcion inscExistente = (Inscripcion) Inscripcion.findFirst(
                "legajo_alumno = ? AND id_catedra = ? AND (estado_inscripcion = 'EN_CURSADA' OR estado_inscripcion = 'REGULAR' OR estado_inscripcion = 'APROBADA')",
                legajoTest, c.getInteger("id_catedra"));
            if (inscExistente != null) {
                yaInscripto = true;
                break;
            }
        }

        assertTrue(yaInscripto, "El estudiante ya está inscripto en la materia, por lo que una nueva inscripción debe ser bloqueada.");
    }

    @Test
    void testUserAuthenticationAndHashing() {
        // Encriptar contraseña y guardar usuario
        String passwordPlana = "secreto123";
        String hash = org.mindrot.jbcrypt.BCrypt.hashpw(passwordPlana, org.mindrot.jbcrypt.BCrypt.gensalt());

        User user = new User();
        user.set("name", "testuser_99");
        user.set("password", hash);
        user.set("tipo_usuario", "alumno");
        user.insert();

        // Verificar búsqueda e inicio de sesión
        User foundUser = User.findFirst("name = ?", "testuser_99");
        assertNotNull(foundUser);
        assertTrue(org.mindrot.jbcrypt.BCrypt.checkpw(passwordPlana, foundUser.getString("password")), "La contraseña correcta debería ser validada");
        assertFalse(org.mindrot.jbcrypt.BCrypt.checkpw("password_incorrecto", foundUser.getString("password")), "Una contraseña incorrecta debería ser rechazada");
    }

    @Test
    void testAcademicGradesAndAverage() {
        // Crear alumno
        Persona pers = new Persona();
        pers.set("dni", "99777888");
        pers.set("nombre", "Juana");
        pers.set("apellido", "Moro");
        pers.insert();

        Alumno alu = new Alumno();
        alu.set("legajo", 99888);
        alu.set("dni_persona", "99777888");
        alu.set("tipo_alumno", "AVANZADO");
        alu.set("id_plan", 1);
        alu.insert();

        // Crear dos inscripciones en cátedras diferentes
        // Inscripcion 1
        Inscripcion insc1 = new Inscripcion();
        insc1.set("id_inscripcion", 99801);
        insc1.set("fecha_inscripcion", (int) (System.currentTimeMillis() / 1000));
        insc1.set("estado_inscripcion", "APROBADA");
        insc1.set("legajo_alumno", 99888);
        insc1.set("id_catedra", 1);
        insc1.insert();

        // Nota para inscripcion 1
        Nota nota1 = new Nota();
        nota1.set("id_nota", 99811);
        nota1.set("valor", 8);
        nota1.set("tipo_nota", "FINAL");
        nota1.set("id_inscripcion", 99801);
        nota1.insert();

        // Inscripcion 2
        Inscripcion insc2 = new Inscripcion();
        insc2.set("id_inscripcion", 99802);
        insc2.set("fecha_inscripcion", (int) (System.currentTimeMillis() / 1000));
        insc2.set("estado_inscripcion", "APROBADA");
        insc2.set("legajo_alumno", 99888);
        insc2.set("id_catedra", 2);
        insc2.insert();

        // Nota para inscripcion 2
        Nota nota2 = new Nota();
        nota2.set("id_nota", 99812);
        nota2.set("valor", 10);
        nota2.set("tipo_nota", "FINAL");
        nota2.set("id_inscripcion", 99802);
        nota2.insert();

        // Recuperar notas finales del alumno y calcular promedio
        List<Map> notasFinales = Base.findAll(
            "SELECT n.valor FROM Nota n " +
            "JOIN Inscripcion i ON n.id_inscripcion = i.id_inscripcion " +
            "WHERE i.legajo_alumno = ? AND n.tipo_nota = 'FINAL'", 99888);

        assertEquals(2, notasFinales.size());
        double sum = 0;
        for (Map n : notasFinales) {
            sum += ((Number) n.get("valor")).doubleValue();
        }
        double promedio = sum / notasFinales.size();
        assertEquals(9.0, promedio, 0.01, "El promedio de notas finales debería ser 9.0");
    }

    @Test
    void testProfessorStudentTracking() {
        // --- 1. SETUP PROFESSOR ---
        String dniProf = "99444111";
        Persona persProf = new Persona();
        persProf.set("dni", dniProf);
        persProf.set("nombre", "Marie");
        persProf.set("apellido", "Curie");
        persProf.set("user_login", "marie_curie");
        persProf.insert();

        String legajoDocente = "PROF_CURIE_99";
        Profesor prof = new Profesor();
        prof.set("legajo_docente", legajoDocente);
        prof.set("dni_persona", dniProf);
        prof.insert();

        // --- 2. SETUP CATEDRA AND MATERIA ---
        int idMateria = 9930;
        Materia mat = new Materia();
        mat.set("id_materia", idMateria);
        mat.set("codigo", "CHEM101");
        mat.set("nombre", "Química General");
        mat.set("periodo", "CUATRIMESTRAL");
        mat.set("id_plan", 1);
        mat.insert();

        int idCatedra = 9931;
        Catedra cat = new Catedra();
        cat.set("id_catedra", idCatedra);
        cat.set("anio", 2026);
        cat.set("comision", 1);
        cat.set("id_materia", idMateria);
        cat.insert();

        // Assign professor to catedra
        AsignacionDocente asig = new AsignacionDocente();
        asig.set("legajo_docente", legajoDocente);
        asig.set("id_catedra", idCatedra);
        asig.set("rol", "RESPONSABLE");
        asig.set("fecha_asignacion", (int)(System.currentTimeMillis() / 1000));
        asig.insert();

        // --- 3. SETUP STUDENTS ENROLLED ---
        // Student 1 (At risk: Libre)
        String dniAlu1 = "99888111";
        Persona persAlu1 = new Persona();
        persAlu1.set("dni", dniAlu1);
        persAlu1.set("nombre", "Pierre");
        persAlu1.set("apellido", "Curie");
        persAlu1.insert();

        int legajoAlu1 = 99771;
        Alumno alu1 = new Alumno();
        alu1.set("legajo", legajoAlu1);
        alu1.set("dni_persona", dniAlu1);
        alu1.set("tipo_alumno", "INGRESANTE");
        alu1.set("id_plan", 1);
        alu1.insert();

        int idInsc1 = 99401;
        Inscripcion insc1 = new Inscripcion();
        insc1.set("id_inscripcion", idInsc1);
        insc1.set("fecha_inscripcion", (int)(System.currentTimeMillis() / 1000));
        insc1.set("estado_inscripcion", "LIBRE");
        insc1.set("legajo_alumno", legajoAlu1);
        insc1.set("id_catedra", idCatedra);
        insc1.insert();

        // Student 2 (Avanzado, has grades, average >= 4.0: Safe)
        String dniAlu2 = "99888222";
        Persona persAlu2 = new Persona();
        persAlu2.set("dni", dniAlu2);
        persAlu2.set("nombre", "Irene");
        persAlu2.set("apellido", "Joliot");
        persAlu2.insert();

        int legajoAlu2 = 99772;
        Alumno alu2 = new Alumno();
        alu2.set("legajo", legajoAlu2);
        alu2.set("dni_persona", dniAlu2);
        alu2.set("tipo_alumno", "AVANZADO");
        alu2.set("id_plan", 1);
        alu2.insert();

        int idInsc2 = 99402;
        Inscripcion insc2 = new Inscripcion();
        insc2.set("id_inscripcion", idInsc2);
        insc2.set("fecha_inscripcion", (int)(System.currentTimeMillis() / 1000));
        insc2.set("estado_inscripcion", "EN_CURSADA");
        insc2.set("legajo_alumno", legajoAlu2);
        insc2.set("id_catedra", idCatedra);
        insc2.insert();

        // Add note to Student 2
        Nota nota1 = new Nota();
        nota1.set("id_nota", 99411);
        nota1.set("valor", 8);
        nota1.set("tipo_nota", "PARCIAL");
        nota1.set("fecha", (int)(System.currentTimeMillis() / 1000));
        nota1.set("id_inscripcion", idInsc2);
        nota1.insert();

        // --- 4. VERIFY LOGIC THAT /profesor/alumnos ENDPOINT USES ---
        // Fetch professor
        Persona p = (Persona) Persona.findFirst("user_login = ?", "marie_curie");
        assertNotNull(p);
        Profesor profObj = (Profesor) Profesor.findFirst("dni_persona = ?", p.get("dni"));
        assertNotNull(profObj);
        assertEquals(legajoDocente, profObj.get("legajo_docente"));

        // Fetch assigned classes
        List<Model> asignaciones = AsignacionDocente.find("legajo_docente = ?", legajoDocente);
        assertEquals(1, asignaciones.size());
        assertEquals(idCatedra, asignaciones.get(0).getInteger("id_catedra"));

        // Fetch enrolled students
        List<Model> inscripciones = Inscripcion.find("id_catedra = ?", idCatedra);
        assertEquals(2, inscripciones.size());

        // Verify Student 1 (Libre -> risk)
        Inscripcion checkInsc1 = (Inscripcion) Inscripcion.findFirst("id_inscripcion = ?", idInsc1);
        assertEquals("LIBRE", checkInsc1.getEstadoInscripcion());
        boolean isRisk1 = "LIBRE".equals(checkInsc1.getEstadoInscripcion());
        assertTrue(isRisk1);

        // Verify Student 2 (En Cursada, avg 8 -> safe)
        Inscripcion checkInsc2 = (Inscripcion) Inscripcion.findFirst("id_inscripcion = ?", idInsc2);
        List<Model> notasAlu2 = Nota.find("id_inscripcion = ?", idInsc2);
        assertEquals(1, notasAlu2.size());
        int valorNota = notasAlu2.get(0).getInteger("valor");
        assertEquals(8, valorNota);
        double promedio2 = (double) valorNota / notasAlu2.size();
        boolean isRisk2 = "LIBRE".equals(checkInsc2.getEstadoInscripcion()) || promedio2 < 4.0;
        assertFalse(isRisk2);
    }
}
