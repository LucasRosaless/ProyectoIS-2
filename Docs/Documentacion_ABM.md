# Documentación Técnica: Módulos ABM (Alta, Baja y Modificación)

## Introducción

Este documento detalla la estructura técnica y el funcionamiento de los módulos ABM implementados para las entidades principales del sistema: **Alumnos**, **Profesores**, **Carreras**, **Planes de Estudio** y **Materias**.

## Arquitectura de Rutas (Endpoints)

Se ha utilizado el framework **SparkJava** para definir las rutas HTTP. Cada entidad posee un conjunto de rutas estándar siguiendo los principios RESTful para aplicaciones web clásicas:

- `GET /{entidad}`: Listado general de la entidad.
- `GET /{entidad}/new`: Muestra el formulario para crear un nuevo registro.
- `POST /{entidad}/new`: Procesa el formulario y guarda la nueva entidad en la base de datos.
- `GET /{entidad}/:id/edit`: Muestra el formulario precargado con los datos del registro a editar.
- `POST /{entidad}/:id/edit`: Procesa y actualiza los datos del registro.
- `POST /{entidad}/:id/delete`: Elimina el registro seleccionado.

## Detalles por Entidad

### 1. Profesores (`/profesores`)
- **Modelos Involucrados**: `Persona` y `Profesor`.
- **Lógica de Creación**: Para crear un Profesor, la aplicación primero registra los datos básicos en la tabla `Persona` (utilizando el DNI como clave primaria). Luego, inserta un registro en la tabla `Profesor` asociando el `legajo_docente` con el `dni_persona` previamente creado. Esto mantiene la integridad referencial.
- **Vistas**: `profesores.mustache` (listado) y `profesor_form.mustache` (creación y edición).

### 2. Alumnos (`/alumnos`)
- **Modelos Involucrados**: `Persona` y `Alumno`.
- **Lógica de Creación**: Al igual que con los profesores, se crea una `Persona` y luego un `Alumno`, asociando el legajo y el DNI. Adicionalmente, se captura el `tipo_alumno` (INGRESANTE o AVANZADO).
- **Vistas**: `alumnos.mustache` y `alumno_form.mustache`.

### 3. Carreras (`/carreras`)
- **Modelos Involucrados**: `Carrera`.
- **Gestión**: Entidad independiente. Captura ID, código, nombre y duración en años.
- **Vistas**: `carreras.mustache` y `carrera_form.mustache`.

### 4. Planes de Estudio (`/planes`)
- **Modelos Involucrados**: `PlanEstudio` y `Carrera`.
- **Gestión**: Depende de una Carrera existente. En el formulario de creación, se despliega una lista de las carreras disponibles en la base de datos (relación 1:N).
- **Vistas**: `planes.mustache` y `plan_form.mustache`.

### 5. Materias (`/materias`)
- **Modelos Involucrados**: `Materia` y `PlanEstudio`.
- **Gestión**: Cada materia requiere estar asociada a un plan de estudio. El formulario solicita al usuario que elija un plan existente mediante un select dinámico que consulta la base de datos.
- **Vistas**: `materias.mustache` y `materia_form.mustache`.

## Manejo de Transacciones y Errores

Para las entidades compuestas (Profesores y Alumnos), se han envuelto las operaciones de guardado en **Transacciones de Base de Datos** (`Base.openTransaction()` y `Base.commitTransaction()`). Esto garantiza que si falla la creación del Profesor o del Alumno, no quede un registro "huérfano" e inconsistente en la tabla `Persona`, ejecutando un `Base.rollbackTransaction()`.

## Estilos Globales
Se ha integrado un archivo de estilos `styles.css` en el directorio `src/main/resources/public/` que provee una interfaz gráfica unificada y moderna (Dark mode, tipografía Inter, efectos de "glassmorphism") para todos los ABM.
