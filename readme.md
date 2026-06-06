# Sistema de Gestión Universitaria - Comisión Nro. 3

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![Spark](https://img.shields.io/badge/Spark-000000?style=for-the-badge&logo=apache-spark&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-07405E?style=for-the-badge&logo=sqlite&logoColor=white)

Bienvenido al repositorio oficial del **Sistema de Gestión Universitaria**. Este proyecto es una aplicación web robusta desarrollada en Java utilizando el framework Spark, diseñada para gestionar usuarios, alumnos, profesores, cátedras y datos académicos (como inscripciones y notas) de manera eficiente.

---

## 📘 Panel de Documentación Detallada

Accede a los detalles específicos de cada fase del desarrollo y aspectos técnicos:

*   📋 **[Análisis de Requerimientos](Docs/Requirements.md)**: Definición del problema, actores y entidades del dominio, requerimientos funcionales/no funcionales y stack tecnológico seleccionado.
*   🎨 **[Diseño del Sistema](Docs/design.md)**: Arquitectura en capas (Mustache, Java y SQLite), diagrama de componentes y diagrama de clases UML del modelo de dominio.
*   🛡️ **[Auditoría y Gestión de Riesgos](Docs/Auditoría.md)**: Matriz de riesgos detallada (Técnicos, Organizacionales, de Planificación y Humanos) y análisis comparativo entre IA y el equipo.
*   🔧 **[Módulos ABM e Inscripción](Docs/Documentacion_ABM.md)**: Detalle técnico de los endpoints RESTful en SparkJava, flujo de transacciones con ActiveJDBC y la validación lógica de correlativas para inscripciones inteligentes.
*   🧪 **[Suite de Testing y Validación UML](Docs/Documentacion_Tests.md)**: Guía detallada de las pruebas unitarias y de integración (`ModelTest`, `ComprehensiveWorkflowTest`), y validación de alineación con el modelo de clases UML.

---

## 📂 Estructura del Proyecto

El proyecto sigue la convención estándar de **Maven**, organizada de la siguiente manera:

```text
ProyectoIS-2/
├── 📂 db/                      # Archivos de base de datos SQLite (.db)
│   ├── 📄 dev.db               # Base de datos de desarrollo
│   └── 📄 prod.db              # Base de datos de producción (esquema inicial)
├── 📂 Docs/                    # Documentación técnica, diseño y gestión del proyecto
│   ├── 📄 Auditoría.md         # Análisis y matriz de riesgos
│   ├── 📄 Documentacion_ABM.md # Guía de módulos CRUD e inscripciones
│   ├── 📄 Documentacion_Tests.md # Documentación detallada de la suite de testing
│   ├── 📄 Requirements.md      # Requerimientos y plazos del proyecto
│   └── 📄 design.md            # Arquitectura del sistema y diagramas UML
├── 📂 src/                     # Código fuente del proyecto
│   ├── 📂 main/
│   │   ├── 📂 java/            # Lógica de negocio (Java)
│   │   │   └── 📂 com/is1/proyecto/
│   │   │       ├── 📄 App.java      # Servidor web, definición de filtros, rutas y controladores (Spark)
│   │   │       ├── 📂 config/       # Configuración e inicialización de la BD (DBConfigSingleton)
│   │   │       └── 📂 models/       # Modelos del dominio con ORM ActiveJDBC (User, Persona, Alumno, etc.)
│   │   └── 📂 resources/       # Recursos estáticos y del servidor
│   │       ├── 📂 public/           # Hojas de estilo y archivos estáticos (styles.css)
│   │       ├── 📂 templates/        # Vistas de la aplicación (.mustache)
│   │       └── 📄 scheme.sql        # Script DDL de creación de tablas de la BD
│   └── 📂 test/                # Suite de pruebas automatizadas
│       └── 📂 java/
│           └── 📂 com/is1/proyecto/
│               ├── 📄 AppTest.java               # Pruebas básicas del entorno
│               ├── 📄 ComprehensiveWorkflowTest.java # Escenarios de integración (cursadas, correlativas, promedios)
│               └── 📄 ModelTest.java             # Pruebas unitarias de persistencia (CRUD) de los modelos
├── 📄 pom.xml                  # Configuración de Maven, dependencias y plugins (ActiveJDBC, Shading, etc.)
└── 📄 dependency-reduced-pom.xml # POM simplificado generado durante el empaquetado del JAR
```

---

## 🚀 Guía de Ejecución y Desarrollo

Para poner en marcha el proyecto localmente, asegúrate de tener instalado **Java 11** (o superior) y **Maven**.

> [!NOTE]
> Este proyecto utiliza **ActiveJDBC**, un ORM que requiere un paso de instrumentación de bytecode (ActiveJDBC Instrumentation) sobre las clases compiladas para poder interactuar correctamente con la base de datos SQLite.

### 🔧 Proceso con Maven

Ejecuta los siguientes comandos desde la terminal en el directorio raíz del proyecto:

1.  **Limpiar, Compilar e Instrumentar:**
    Prepara el entorno limpiando directorios antiguos, compilando y ejecutando la fase de instrumentación de ActiveJDBC de forma explícita:
    ```bash
    mvn clean process-classes
    ```

2.  **Compilar, Instrumentar y Ejecutar (En un solo comando):**
    Compila el proyecto, aplica la instrumentación obligatoria y levanta el servidor web Spark de forma inmediata:
    ```bash
    mvn compile process-classes exec:java
    ```

3.  **Limpiar e Instalar Dependencias:**
    Prepara el entorno completo de dependencias de Maven, compila, instrumenta e instala en el repositorio local:
    ```bash
    mvn clean install
    ```

4.  **Acceder a la Aplicación:**
    Una vez que la consola indique que el servidor web se encuentra activo, abre tu navegador en:
    👉 [**http://localhost:4567/**](http://localhost:4567/)

5.  **Ejecutar la Suite de Pruebas:**
    Para verificar que todas las reglas de negocio, validaciones del modelo UML, flujos de inscripción y base de datos estén funcionando correctamente:
    ```bash
    mvn clean test
    ```

6.  **Empaquetar en un JAR Ejecutable:**
    Para generar un JAR ejecutable "sombreado" (fat JAR) con todas las dependencias embebidas en `target/proye-is-1.0-SNAPSHOT.jar`:
    ```bash
    mvn clean package
    ```
    Para iniciar el servidor mediante el archivo JAR compilado:
    ```bash
    java -jar target/proye-is-1.0-SNAPSHOT.jar
    ```

---

## 👥 Equipo de Desarrollo

| Nombre | Rol | GitHub |
| :--- | :--- | :--- |
| **Agustin Morosi** | Scrum Master | [@agusking420](https://github.com/agusking420) |
| **Lucas Rosales** | Programación & Requerimientos | [@LucasRosaless](https://github.com/LucasRosaless) |
| **Luca Porta** | Backend Developer | [@Luca0052](https://github.com/Luca0052) |
| **Ulises Leguizamon** | Frontend Designer | [@Ulisesle](https://github.com/Ulisesle) |

---
*Desarrollado para la cátedra de Estructuras de Datos - Proyecto IS-2*
