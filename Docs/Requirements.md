# Proyecto: Sistema de Gestión Universitaria

## 1. Problema que se quiere resolver
El proyecto surge de la necesidad de una institución universitaria de modernizar y optimizar sus procesos administrativos y académicos. El contexto actual presenta desafíos significativos en la gestión de datos de alumnos y docentes, así como en la administración de materias, carreras y sus respectivos planes de estudio. Un punto crítico es el manejo de las inscripciones, proceso que requiere una validación precisa de las correlatividades para asegurar el cumplimiento de las normativas académicas vigentes.

## 2. Usuarios y Actores del sistema
El ecosistema del sistema está compuesto por diversos perfiles que interactúan para mantener la operatividad institucional:

*   **Docentes:** Encargados de la gestión académica de las materias, seguimiento de alumnos y carga de calificaciones.
*   **Estudiantes:** Usuarios que gestionan su trayectoria académica, realizan inscripciones y consultan su progreso.
*   **Personal Administrativo:** Responsables de la configuración del sistema, gestión de usuarios y mantenimiento de la estructura de carreras y planes.

**Actores y Entidades del Dominio:**
*   **Persona:** Entidad base que contiene los datos comunes para estudiantes, profesores y usuarios del sistema.
*   **Carrera y Materia:** Estructuras que definen la oferta educativa.
*   **Plan de Estudio:** Documento dinámico que vincula materias y establece las reglas de progresión.
*   **Nota:** Registro de rendimiento académico en instancias parciales y finales.
*   **Sistema de Autenticación (Sist.aut):** Módulo encargado de la seguridad y el control de accesos.

## 3. Requerimientos Funcionales
El sistema debe proporcionar una solución integral que cubra las siguientes áreas operativas:

### Gestión de Usuarios y Perfiles
*   **Registro de Estudiantes:** Captura de datos personales e información de contacto, diferenciando entre alumnos ingresantes y aquellos con trayectoria avanzada.
*   **Gestión Docente:** Administración de legajos de profesores y vinculación con las materias que dictan.
*   **Administración de Accesos:** Asignación de roles administrativos y consulta detallada de datos de todos los usuarios.

### Estructura Académica
*   **Definición de Oferta:** Carga y actualización de materias y carreras.
*   **Configuración de Planes:** Creación de planes de estudio y asignación de materias con determinación de correlatividades obligatorias.
*   **Calendario Académico:** Asignación de materias a periodos específicos y gestión de cátedras.

### Procesos de Inscripción
*   **Validación de Requisitos:** Verificación automática de las condiciones necesarias para la inscripción.
*   **Inscripción Inteligente:** Gestión del alta en materias validando el cumplimiento del árbol de correlativas.

### Seguimiento y Evaluación
*   **Registro de Calificaciones:** Carga de notas de parciales, trabajos prácticos y exámenes finales.
*   **Análisis de Progreso:** Visualización del rendimiento académico, porcentaje de asistencia y alertas de avance.
*   **Consultas Académicas:** Reportes sobre materias cursadas, aprobadas y aquellas habilitadas para cursar según la situación actual del alumno.

### Gestión de Cátedras
*   **Asignación de Recursos:** Designación de docentes a materias y distribución de roles dentro de la cátedra (Jefe de Trabajos Prácticos, ayudantes, etc.).

## 4. Requerimientos No Funcionales
Para garantizar la calidad y sostenibilidad del software, se han definido los siguientes criterios:

*   **Usabilidad:** Diseño de una interfaz clara, intuitiva y minimalista que minimice la curva de aprendizaje para todos los usuarios.
*   **Confiabilidad y Robustez:** El sistema debe operar con un bajo índice de fallos críticos, asegurando la integridad de los datos académicos.
*   **Escalabilidad Horizontal:** Arquitectura diseñada para soportar el incremento gradual en la cantidad de alumnos, docentes y la complejidad de los planes de estudio.
*   **Rendimiento en Búsquedas:** Optimización de algoritmos de ordenamiento y filtrado para manejar grandes volúmenes de datos relacionados.
*   **Seguridad y Privacidad:** Sistema de gestión de roles con permisos estrictamente diferenciados y protocolos de autenticación seguros.
*   **Disponibilidad Operativa:** Alta disponibilidad garantizada durante los periodos de mayor demanda administrativa y horario laboral.
*   **Extensibilidad del Código:** Aplicación de patrones de diseño que faciliten la incorporación de nuevos módulos o integraciones en etapas futuras.

## 5. Equipo de Trabajo (Grupo 6)
El equipo está conformado por cuatro integrantes con roles definidos para cubrir todas las áreas del ciclo de vida del desarrollo:

*   Cantidad de integrantes: 4
*   Detalle de Integrantes:
    *   **Lucas Rosales** - Programación General y Gestión de Requerimientos.
    *   **Agustin Morosi** - Programación y Coordinación (Scrum Master).
    *   **Luca Porta** - Programación de Lógica de Negocio (Backend).
    *   **Ulises Leguizamon** - Diseño de Interfaz y Experiencia de Usuario (Frontend).

## 6. Tecnologías Elegidas y Justificación
Se ha optado por un stack tecnológico que prioriza la robustez y la facilidad de mantenimiento:

*   **Java:** Lenguaje principal para el desarrollo de la lógica de negocio por su robustez y tipado fuerte.
*   **Mustache:** Motor de plantillas elegido para la generación de vistas dinámicas, permitiendo una separación limpia entre la lógica y la presentación.
*   **SQLite:** Motor de base de datos portable y eficiente que facilita el manejo de la persistencia sin configuraciones complejas de servidor.
*   **HTML5 y CSS3:** Estándares utilizados para la construcción de la arquitectura estructural y el diseño visual responsive de la interfaz.
*   **Herramientas de Desarrollo:** Visual Studio Code como entorno principal, potenciado con Gemini Code Assistant para la optimización del código.
*   **Análisis de Datos:** Uso de Notebook LM para el procesamiento de requerimientos y estructuración de la documentación técnica.
*   **Metodología de Gestión:** Implementación de Scrum para una entrega incremental y de calidad.

## 7. Plazo Estimado
El proyecto se desarrollará en el marco de un cuatrimestre universitario con la siguiente planificación temporal:

*   **Fecha de Inicio:** 15 de Marzo de 2026
*   **Fecha de Entrega Final:** 12 de Junio de 2026
*   **Extensión Temporal:** 90 días naturales (aproximadamente 13 semanas).
*   **Modelo de Trabajo:** El cronograma se divide en Sprints cortos con entregas funcionales y actualizaciones de estado semanales.

## 8. Cambios de Alcance Ocurridos
*   **Estado al 13/04/2026:** No se han reportado cambios significativos en el alcance definido inicialmente. Sin embargo, se mantiene una vigilancia activa sobre la complejidad del motor de correlatividades, lo que podría derivar en ajustes técnicos menores en el futuro próximo.

## 9. Problemas Encontrados
Durante las primeras etapas del proyecto se han identificado los siguientes desafíos:

*   **Compatibilidad de Entorno:** Diferencias de configuración en los entornos de desarrollo locales de los integrantes (versiones de Java y dependencias).
*   **Comprensión del Dominio:** El análisis inicial de las reglas de negocio académicas y las jerarquías de planes de estudio presentó una curva de aprendizaje pronunciada.

## 10. Forma de Organización del Equipo
Para asegurar la fluidez y el cumplimiento de objetivos, el equipo se organiza bajo los siguientes parámetros:

*   **Metodología:** Adopción de marcos ágiles con Sprints de frecuencia semanal para la revisión de avances.
*   **Esquema de Liderazgo:** Se implementa un liderazgo rotativo semanal para asegurar que todos los miembros desarrollen capacidades de gestión y coordinación.
*   **Canales de Comunicación:** Comunicación sincrónica mediante Discord y Slack, complementada con reuniones presenciales o virtuales semanales para la sincronización de tareas.
*   **Gestión de Tareas:** Uso de Notion para el seguimiento de tareas pendientes y Notebook LM para la centralización del conocimiento técnico.
*   **Control de Versiones:** Repositorio centralizado en GitHub para la integración continua del código.
