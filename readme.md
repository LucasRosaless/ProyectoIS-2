# 🎓 Sistema de Gestión Universitaria - Comisión Nro. 3

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![Spark](https://img.shields.io/badge/Spark-000000?style=for-the-badge&logo=apache-spark&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-07405E?style=for-the-badge&logo=sqlite&logoColor=white)

Bienvenido al repositorio oficial del **Sistema de Gestión Universitaria**. Este proyecto es una aplicación web robusta desarrollada en Java utilizando el framework Spark, diseñada para gestionar usuarios, profesores y datos académicos de manera eficiente.

> 🔗 **Acceso Rápido:** [http://localhost:4567/](http://localhost:4567/)

---

## 📂 Estructura del Proyecto

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

## 🚀 Guía de Ejecución

Para poner en marcha el proyecto localmente, asegúrate de tener instalado **Java 11** (o superior) y **Maven**.

### 🛠️ Proceso con Maven

Sigue estos comandos en tu terminal dentro de la carpeta raíz del proyecto:

1.  **Limpiar y Compilar:**
    Prepara el entorno y compila las clases (incluyendo la instrumentación de ActiveJDBC).
    ```bash
    mvn clean compile
    ```

2.  **Ejecutar la Aplicación:**
    Inicia el servidor web Spark.
    ```bash
    mvn exec:java
    ```

3.  **Ver el Proyecto:**
    Una vez que la terminal indique que el servidor está corriendo, abre tu navegador en:
    👉 [**http://localhost:4567/**](http://localhost:4567/)

---

## 📑 Panel de Documentación Detallada

Accede a los detalles específicos de cada fase del desarrollo:

*   📘 **[Análisis de Requerimientos](https://github.com/LucasRosaless/ProyectoIS-2/blob/main/Requirements.md)**: Actores, casos de uso y requerimientos funcionales/no funcionales.
*   🎨 **[Diseño del Sistema](https://github.com/LucasRosaless/ProyectoIS-2/blob/main/design.md)**: Arquitectura, esquema de BD SQLite y diseño de UI.
*   🛡️ **[Auditoría y Gestión](https://github.com/LucasRosaless/ProyectoIS-2/blob/main/Auditoría.md)**: Metodología Scrum, seguimiento de Sprints y bitácora.

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
