# Documentación de Pruebas Unitarias e Integración (Suite de Testing)

Este documento detalla la estructura y el funcionamiento de la suite de pruebas del Sistema de Gestión Universitaria. Las pruebas están diseñadas para asegurar la integridad de las reglas académicas y validar que las relaciones e interacciones de los objetos coincidan al 100% con el modelo UML diseñado.

---

## 1. Cómo Ejecutar las Pruebas

El proyecto utiliza **Maven** para la gestión de dependencias y el ciclo de vida del build. Para correr todas las pruebas locales (incluyendo base de datos en memoria o transaccional de desarrollo), ejecuta el siguiente comando en la raíz del proyecto:

```bash
mvn clean test
```

---

## 2. Estructura de la Suite de Pruebas

Las pruebas se dividen en dos clases principales dentro de `src/test/java/com/is1/proyecto/`:

### A. Pruebas Unitarias Básicas (`ModelTest.java`)
Valida de forma aislada que las operaciones básicas de **Creación, Lectura, Actualización y Eliminación (CRUD)** sobre las tablas de base de datos persistan correctamente en SQLite usando el ORM ActiveJDBC.

*   `testModelCrud()`: Verifica el CRUD básico de `Persona`, `Carrera` y `PlanEstudio`.
*   `testAlumnoCreation()`: Valida la creación de un registro de `Alumno` y su vinculación con una `Persona`.
*   `testMateriaPlanAssociation()`: Valida que una `Materia` pertenezca a un `PlanEstudio` mediante su clave foránea.
*   `testProfesorCreation()` y `testAdministradorCreation()`: Verifican la persistencia y recuperación de datos específicos para docentes y administrativos.
*   `testAlumnoPlanAndInscripcion()`: Valida la vinculación directa de un alumno con su Plan de Estudio.

---

### B. Pruebas Integrales de Flujo Académico (`ComprehensiveWorkflowTest.java`)
Simulan escenarios reales del día a día universitario que involucran transiciones de estados, validación de reglas de negocio y joins de base de datos complejos.

#### Escenario 1: Flujo Completo de Cursada, Correlativas y Aprobación
*   **Método:** `testCompleteAcademicAndEnrollmentWorkflow()`
*   **Simulación:**
    1.  Se define una carrera ("Ingeniería Aeroespacial") con su Plan de Estudios.
    2.  Se crean dos materias encadenadas: **Física I** (inicial) y **Física II** (requiere Física I aprobada).
    3.  Se establece la relación en la tabla `Correlativas_previas`.
    4.  Se registra a un alumno de forma regular.
    5.  **Validación 1 (Bloqueo):** El alumno intenta inscribirse a Física II y el sistema **bloquea la inscripción** debido a que no tiene aprobada Física I.
    6.  **Inscripción 1 (Éxito):** El alumno se inscribe en la comisión habilitada de Física I (estado `EN_CURSADA`).
    7.  **Calificaciones:** Se simula la carga de notas de exámenes parciales y finales. Al asentarse la aprobación (nota >= 4), el estado de la cursada se promueve a `APROBADA`.
    8.  **Validación 2 (Habilitación):** Se re-evalúa el prerrequisito para Física II. Al estar aprobada Física I, la validación da exitosa.
    9.  **Inscripción 2 (Éxito):** El estudiante se inscribe exitosamente a Física II.

#### Escenario 2: Asignación Docente y Comisiones (Cátedras)
*   **Método:** `testTeacherAssignmentAndCommissions()`
*   **Simulación:**
    1.  Se crea una materia de testeo con su respectiva `Catedra` (comisión).
    2.  Se crea una `Persona` y se le da el rol de `Profesor`.
    3.  Se asigna al docente a la cátedra con un rol específico (`RESPONSABLE`) en la tabla asociativa `Asignacion_Docente`.
    4.  Se verifica que la relación existe y mantiene la integridad de datos.

#### Escenario 3: Prevención de Inscripción Duplicada
*   **Método:** `testDuplicateEnrollmentProtection()`
*   **Simulación:**
    1.  Se inscribe a un estudiante en una cátedra activa.
    2.  Se intenta inscribirlo una segunda vez en la misma materia (u otra comisión de la misma asignatura) mientras la primera sigue en estado `EN_CURSADA` o `APROBADA`.
    3.  El sistema detecta la inscripción existente y **bloquea el duplicado** para evitar inconsistencias en el acta.

#### Escenario 4: Seguridad y Autenticación de Usuarios
*   **Método:** `testUserAuthenticationAndHashing()`
*   **Simulación:**
    1.  Simula la creación de una cuenta encriptando la contraseña usando el algoritmo **BCrypt** (igual que la ruta `/user/new` del backend).
    2.  Verifica que las credenciales válidas permiten la autenticación (`BCrypt.checkpw` = true).
    3.  Verifica que las credenciales inválidas son rechazadas.

#### Escenario 5: Historial Académico y Cálculo de Promedio
*   **Método:** `testAcademicGradesAndAverage()`
*   **Simulación:**
    1.  Un alumno aprueba dos materias con calificaciones finales de `8` y `10`.
    2.  Se ejecuta la consulta SQL exacta con `JOIN` que calcula el promedio del estudiante.
    3.  Se valida que el promedio calculado sea exactamente `9.0`.

---

## 3. Alineación con el Modelo UML

Las pruebas demuestran la fidelidad del código con el diagrama UML de clases y componentes:

| Relación UML | Implementación en Código | Validación en Tests |
| :--- | :--- | :--- |
| **Herencia** (`Persona` -> `Alumno`/`Profesor`/`Administrador`) | Clave foránea `dni_persona` apuntando a `Persona.dni`. | Se verifica creando primero la Persona y luego insertando el rol asociado. |
| **Asociación Recursiva** (Correlativas) | Tabla `Correlativas_previas` referenciando `Materia(id_materia)`. | `testCompleteAcademicAndEnrollmentWorkflow` valida el bloqueo por falta de correlativas. |
| **Clase Asociativa** (`Inscripcion`) | Tabla `Inscripcion` vinculando `Alumno` y `Catedra`. | Se instancian registros y se asocian sus correspondientes notas finales. |
| **Clase Asociativa** (`Asignacion_Docente`) | Tabla `Asignacion_Docente` vinculando `Profesor` y `Catedra` con atributo `rol`. | `testTeacherAssignmentAndCommissions` valida la asignación y rol. |
| **Composición** (`Inscripcion` -> `Nota`) | Clave foránea `id_inscripcion` en `Nota`. | Se validan promedios consumiendo notas vinculadas a la inscripción. |
