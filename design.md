# Diseño y Arquitectura del Sistema

Este documento detalla la estructura técnica del Sistema de Gestión Universitaria, incluyendo su arquitectura fundamentada en capas y el diseño detallado de sus entidades.

## 1. Arquitectura del Sistema
El sistema sigue un patrón de **Arquitectura en Capas (Layered Architecture)** para asegurar la separación de responsabilidades y facilitar el mantenimiento.

*   **Capa de Presentación (Frontend):** Implementada con **HTML5, CSS3 y el motor de plantillas Mustache**. Se encarga de renderizar la interfaz de usuario de forma dinámica basada en los datos provistos por el backend.
*   **Capa de Lógica de Negocio (Backend):** Desarrollada en **Java**. Contiene los controladores y servicios que gestionan las reglas académicas (validación de correlativas, cálculos de promedio, inscripciones).
*   **Capa de Acceso a Datos (Persistencia):** Utiliza **SQLite** como motor de base de datos relacional, gestionando la persistencia de las entidades mediante una capa de DAO (Data Access Objects).

### Diagrama de Componentes e Interacciones


---

## 2. Diagrama de Diseño (Clases)
A continuación se presenta el modelo de dominio detallado, representando las entidades principales y sus relaciones dentro del sistema académico.

```mermaid
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
```

---

## 3. Responsabilidades e Interacciones
Cada componente del sistema tiene una responsabilidad clara para asegurar el flujo de datos:

1.  **Gestión de Inscripciones:** El componente de `Estudiante` interactúa con `Materia` y el servicio de `Correlatividad` en Java para verificar si el alumno cumple los requisitos previos antes de persistir la inscripción en `SQLite`.
2.  **Calificaciones:** Al finalizar un periodo, el `Profesor` registra una `Nota` vinculando un `Estudiante` con una `Materia`. Este evento actualiza el estado académico del alumno (Cursada/Aprobada).
3.  **Definición Académica:** El `Administrador` configura las `Carreras` y los `Planes de Estudio`, estableciendo la estructura sobre la cual operarán las inscripciones y cursadas.
