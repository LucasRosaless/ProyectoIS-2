A continuación tenés la representación en Markdown del diagrama de clases UML proporcionado. He respetado la estructura de clases, atributos (con sus tipos y visibilidad), enumeraciones y las relaciones con sus respectivas multiplicidades.

---

## Diagrama de Clases UML

### Clases y Atributos

| **persona** | **usser** |
| :--- | :--- |
| + DNI : int | - usuario : int |
| + nombre : long | - pass : int |
| + apellido : long | |
| + telefono : long | |
| + fecha_nacimiento : date | |
| + edad : int | |
| - correo : long | |

| **estudiante** | **profesor** |
| :--- | :--- |
| - instancia : tipo_alumno | - salario : int |
| | + cargo_profesor : tipo_profesor |

| **carrera** | **materia** |
| :--- | :--- |
| + codigo : int | - codigo : int |
| + nombre : long | - nombre : int |
| | + periodo : periodo_academico |

| **plan_estudio** | **correlatividad** |
| :--- | :--- |
| - id_plan : int | - codigo : int |
| - nombre : int | - materia : int |
| - plan_estudio : int | |

| **nota** |
| :--- |
| + id_nota : int |
| + nota : int |
| + condicion : long |

---

### Enumeraciones

* **«enumeration» tipo_alumno**
    * ingresante
    * avanzado
* **«enumeration» tipo_profesor**
    * ayudante
    * jefe_practico
    * responsable
* **«enumeration» periodo_academico**
    * cuatrimestral
    * anual

---

### Relaciones y Multiplicidad

1.  **Herencia:**
    * `estudiante` **hereda de** `persona`.
    * `profesor` **hereda de** `persona`.
2.  **Asociaciones:**
    * `persona` (1...1) --- (1...*) `usser`
    * `profesor` (1...*) --- (1...*) `materia`
    * `carrera` (1...*) --- (1...*) `materia`
    * `estudiante` (1...*) --- (1...*) `carrera`
3.  **Relaciones Estudiante - Materia:**
    * `estudiante` (1...*) --- **Cursa** (0...*) `materia`
    * `estudiante` (0...*) --- **Cursada** (0...*) `materia`
    * `estudiante` (0...*) --- **porCursar** (0...*) `materia`
4.  **Clase de Asociación:**
    * La relación entre `estudiante` y `materia` genera la clase **nota**.
5.  **Composición:**
    * `materia` (1...*) ◆--- (1...*) `plan_estudio`
6.  **Recursividad / Autorelación:**
    * `materia` (0...*) --- **correlatividad** ---> `correlatividad` (clase asociada a la relación recursiva de materia).
