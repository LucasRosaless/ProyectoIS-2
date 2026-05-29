# Sistema de Gestión Universitaria - Comisión Nro. 3

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![Spark](https://img.shields.io/badge/Spark-000000?style=for-the-badge&logo=apache-spark&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-07405E?style=for-the-badge&logo=sqlite&logoColor=white)

Bienvenido al repositorio oficial del **Sistema de Gestión Universitaria**. Este proyecto es una aplicación web robusta desarrollada en Java utilizando el framework Spark, diseñada para gestionar usuarios, profesores y datos académicos de manera eficiente.

---

## <img src="https://unpkg.com/boxicons@2.1.4/svg/regular/bx-file.svg" width="24" height="24" style="vertical-align: middle;"> Panel de Documentación Detallada

Accede a los detalles específicos de cada fase del desarrollo:

*   📘 **[Análisis de Requerimientos](https://github.com/LucasRosaless/ProyectoIS-2/blob/main/Requirements.md)**: Actores, casos de uso y requerimientos funcionales/no funcionales.
*   🎨 **[Diseño del Sistema](https://github.com/LucasRosaless/ProyectoIS-2/blob/main/design.md)**: Arquitectura, esquema de BD SQLite y diseño de UI.
*   🛡️ **[Auditoría y Gestión](https://github.com/LucasRosaless/ProyectoIS-2/blob/main/Auditoría.md)**: Metodología Scrum, seguimiento de Sprints y bitácora.

---

## <img src="https://unpkg.com/boxicons@2.1.4/svg/regular/bx-folder.svg" width="24" height="24" style="vertical-align: middle;"> Estructura del Proyecto

El proyecto sigue la convención estándar de **Maven**, organizada de la siguiente manera:

```text
ProyectoIS-2/
├── 📂 db/                # Archivos de base de datos SQLite (.db)
├── 📂 Docs/              # Documentación técnica y funcional
├── 📂 src/               # Código fuente del proyecto
│   ├── 📂 main/
│   │   ├── 📂 java/      # Lógica de negocio (Java)
│   │   │   └── 📂 com/is1/proyecto/
│   │   │       ├── 📄 App.java      # Clase principal (Rutas y Configuración)
│   │   │       ├── 📂 config/      # Configuraciones (Singletons, DB)
│   │   │       └── 📂 models/      # Modelos de ActiveJDBC (User, Profesor)
│   │   └── 📂 resources/ # Recursos estáticos y plantillas
│   │       ├── 📂 templates/       # Vistas Mustache (.mustache)
│   │       └── 📄 scheme.sql      # Script de creación de tablas
│   └── 📂 test/          # Pruebas unitarias e integración
├── 📄 pom.xml            # Configuración de dependencias de Maven
└── 📄 index.html         # Punto de entrada estático (opcional)
```

---

## <img src="https://unpkg.com/boxicons@2.1.4/svg/regular/bx-rocket.svg" width="24" height="24" style="vertical-align: middle;"> Guía de Ejecución

Para poner en marcha el proyecto localmente, asegúrate de tener instalado **Java 11** (o superior) y **Maven**.

### <img src="https://unpkg.com/boxicons@2.1.4/svg/regular/bx-wrench.svg" width="24" height="24" style="vertical-align: middle;"> Proceso con Maven

Para ejecutar el proyecto, sigue estos pasos en tu terminal:

1.  **Limpiar e Instalar Dependencias:**
    Este comando prepara el entorno, descarga las librerías necesarias y realiza la instrumentación de ActiveJDBC.
    ```bash
    mvn clean install
    ```

2.  **Compilar (Alternativo):**
    Si solo deseas compilar sin instalar en el repositorio local:
    ```bash
    mvn clean compile
    ```

3.  **Ejecutar la Aplicación:**
    Inicia el servidor web Spark (asegúrate de haber compilado primero).
    ```bash
    mvn exec:java
    ```

3.  **Ver el Proyecto:**
    Una vez que la terminal indique que el servidor está corriendo, abre tu navegador en:
    👉 [**http://localhost:4567/**](http://localhost:4567/)


---

## <img src="https://unpkg.com/boxicons@2.1.4/svg/regular/bx-group.svg" width="24" height="24" style="vertical-align: middle;"> Equipo de Desarrollo

| Nombre | Rol | GitHub |
| :--- | :--- | :--- |
| **Agustin Morosi** | Scrum Master | [@agusking420](https://github.com/agusking420) |
| **Lucas Rosales** | Programación & Requerimientos | [@LucasRosaless](https://github.com/LucasRosaless) |
| **Luca Porta** | Backend Developer | [@Luca0052](https://github.com/Luca0052) |
| **Ulises Leguizamon** | Frontend Designer | [@Ulisesle](https://github.com/Ulisesle) |

---
*Desarrollado para la cátedra de Estructuras de Datos - Proyecto IS-2*
