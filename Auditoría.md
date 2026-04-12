### Análisis de Riesgos (IA vs. Equipo)

| Tipo de Riesgo | Descripción | Probabilidad | Impacto | Identificado por |
| :--- | :--- | :--- | :--- | :--- |
| **Técnico** | Caída del sistema por alta concurrencia o problemas de rendimiento durante los períodos críticos de inscripción a materias. | Alta | Crítico | IA |
| **Técnico** | Complejidad inesperada y fallos en el algoritmo de validación de correlativas y requisitos del plan de estudio. | Media | Alto | IA |
| **Técnico** | Vulnerabilidades en la seguridad y el control de acceso según los roles (alumno, docente, administrativo). | Baja | Crítico | IA |
| **Técnico** | Fallos, limitaciones o incompatibilidades en la infraestructura de hardware de la universidad que afecten el sistema. | Baja | Alto | Equipo |
| **Organizacional**| Resistencia al cambio por parte del personal o docentes al adoptar el nuevo sistema. | Media | Medio | IA |
| **Organizacional**| Cambios de último momento en las normativas de la universidad respecto a los planes de estudio. | Baja | Alto | IA |
| **Organizacional**| Dificultad de adaptación o curva de aprendizaje pronunciada por parte del personal de la universidad al nuevo software. | Media | Medio | Equipo |
| **Planificación** | Subestimación del tiempo de desarrollo necesario para el módulo de reportes y progreso del alumno. | Alta | Medio | IA |
| **Planificación** | Retrasos por dependencias entre módulos (ej. no poder hacer inscripciones sin haber cargado carreras primero). | Media | Alto | IA |
| **Planificación** | Planificación deficiente, mala asignación de tareas u omisión de actividades clave del ciclo de vida (ej. omitir pruebas). | Media | Alto | Equipo |
| **Humano** | Falta de disponibilidad o abandono de un miembro clave del equipo de desarrollo durante el cuatrimestre. | Baja | Crítico | IA |
| **Humano** | Falta de experiencia o conocimientos técnicos previos en el equipo respecto a las herramientas a utilizar. | Alta | Crítico | Equipo |


### Conclusión de la auditoría (Comparación):
Al realizar la comparación, notamos que la IA identificó riesgos técnicos y de planificación muy específicos ligados a la arquitectura del sistema y la lógica de negocio (como problemas de concurrencia en inscripciones, fallos en el algoritmo de correlativas y dependencias entre módulos) que el equipo no había contemplado. Por otro lado, el equipo encontró riesgos que la IA pasó por alto debido a que están estrechamente relacionados con nuestro contexto real y físico: las deficiencias en la infraestructura de hardware de la universidad y nuestra propia falta de experiencia técnica. 
Respecto a la calidad del análisis, consideramos que fue excelente y sumamente enriquecedor, ya que la IA nos aportó escenarios críticos y perspectivas de la industria que mejoraron sustancialmente nuestra matriz de riesgos, mientras que el equipo le otorgó el realismo necesario sobre nuestro entorno de desarrollo.

