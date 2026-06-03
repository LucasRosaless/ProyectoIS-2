package com.is1.proyecto; // Define el paquete de la aplicación, debe coincidir con la estructura de carpetas.

// Importaciones necesarias para la aplicación Spark
import java.util.ArrayList; // Utilidad para serializar/deserializar objetos Java a/desde JSON.
import java.util.HashMap; // Importa los métodos estáticos principales de Spark (get, post, before, after, etc.).
import java.util.List; // Clase central de ActiveJDBC para gestionar la conexión a la base de datos.
import java.util.Map; // Base model para ActiveJDBC.

import org.javalite.activejdbc.Base; // Utilidad para hashear y verificar contraseñas de forma segura.
import org.javalite.activejdbc.Model; // Representa un modelo de datos y el nombre de la vista a renderizar.
import org.mindrot.jbcrypt.BCrypt; // Motor de plantillas Mustache para Spark.

import com.fasterxml.jackson.databind.ObjectMapper; // Para crear mapas de datos (modelos para las plantillas).
import com.is1.proyecto.config.DBConfigSingleton; // Modelo para la tabla profesores.
import com.is1.proyecto.models.Alumno;
import com.is1.proyecto.models.Carrera;
import com.is1.proyecto.models.Materia;
import com.is1.proyecto.models.Persona;
import com.is1.proyecto.models.PlanEstudio;
import com.is1.proyecto.models.Profesor;
import com.is1.proyecto.models.User;

import spark.ModelAndView; // Interfaz Map, utilizada para Map.of() o HashMap.
import static spark.Spark.after; // Clase Singleton para la configuración de la base de datos.
import static spark.Spark.before; // Modelo de ActiveJDBC que representa la tabla 'users'.
import static spark.Spark.get;
import static spark.Spark.halt;
import static spark.Spark.port;
import static spark.Spark.post;
import spark.template.mustache.MustacheTemplateEngine;

/**
 * Clase principal de la aplicación Spark. Configura las rutas, filtros y el
 * inicio del servidor web.
 */
public class App {

    // Instancia estática y final de ObjectMapper para la
    // serialización/deserialización JSON.
    // Se inicializa una sola vez para ser reutilizada en toda la aplicación.
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Método principal que se ejecuta al iniciar la aplicación. Aquí se
     * configuran todas las rutas y filtros de Spark.
     */
    public static void main(String[] args) {
        port(4567); // Configura el puerto en el que la aplicación Spark escuchará las peticiones
        // (por defecto es 4567).
        spark.Spark.staticFiles.location("/public");

        // Obtener la instancia única del singleton de configuración de la base de
        // datos.
        DBConfigSingleton dbConfig = DBConfigSingleton.getInstance();

        // --- Filtro 'before' para gestionar la conexión a la base de datos ---
        // Este filtro se ejecuta antes de cada solicitud HTTP.
        before((req, res) -> {
            try {
                // Abre una conexión a la base de datos utilizando las credenciales del
                // singleton.
                dbConfig.openConnection();
                System.out.println(req.url());

            } catch (Exception e) {
                // Si ocurre un error al abrir la conexión, se registra y se detiene la
                // solicitud
                // con un código de estado 500 (Internal Server Error) y un mensaje JSON.
                System.err.println("Error al abrir conexión con ActiveJDBC: " + e.getMessage());
                halt(500, "{\"error\": \"Error interno del servidor: Fallo al conectar a la base de datos.\"}"
                        + e.getMessage());
            }
        });

        // --- Filtro 'after' para cerrar la conexión a la base de datos ---
        // Este filtro se ejecuta después de que cada solicitud HTTP ha sido procesada.
        after((req, res) -> {
            try {
                // Cierra la conexión a la base de datos para liberar recursos.
                dbConfig.closeConnection();
            } catch (Exception e) {
                // Si ocurre un error al cerrar la conexión, se registra.
                System.err.println("Error al cerrar conexión con ActiveJDBC: " + e.getMessage());
            }
        });

        // --- Rutas GET para renderizar formularios y páginas HTML ---
        // GET: Muestra el formulario de creación de cuenta.
        // Soporta la visualización de mensajes de éxito o error pasados como query
        // parameters.
        get("/user/create", (req, res) -> {
            Map<String, Object> model = new HashMap<>(); // Crea un mapa para pasar datos a la plantilla.

            // Obtener y añadir mensaje de éxito de los query parameters (ej.
            // ?message=Cuenta creada!)
            String successMessage = req.queryParams("message");
            if (successMessage != null && !successMessage.isEmpty()) {
                model.put("successMessage", successMessage);
            }

            // Obtener y añadir mensaje de error de los query parameters (ej. ?error=Campos
            // vacíos)
            String errorMessage = req.queryParams("error");
            if (errorMessage != null && !errorMessage.isEmpty()) {
                model.put("errorMessage", errorMessage);
            }

            // Renderiza la plantilla 'user_form.mustache' con los datos del modelo.
            return new ModelAndView(model, "user_form.mustache");
        }, new MustacheTemplateEngine()); // Especifica el motor de plantillas para esta ruta.

        // GET: Ruta para mostrar el dashboard (panel de control) del usuario.
        // Requiere que el usuario esté autenticado y redirige a su panel correspondiente según su rol.
        get("/dashboard", (req, res) -> {
            // Intenta obtener el nombre de usuario, la bandera de login y el tipo de usuario de la sesión.
            String currentUsername = req.session().attribute("currentUserUsername");
            Boolean loggedIn = req.session().attribute("loggedIn");
            String tipoUsuario = req.session().attribute("tipoUsuario");

            // 1. Verificar si el usuario ha iniciado sesión.
            if (currentUsername == null || loggedIn == null || !loggedIn) {
                System.out.println("DEBUG: Acceso no autorizado a /dashboard. Redirigiendo a la pantalla de login.");
                // Redirige al login principal con un mensaje de error.
                res.redirect("/?error=Debes iniciar sesión para acceder a esta página.");
                return null; // Importante retornar null después de una redirección.
            }

            // 2. Redirección basada en el rol/tipo de usuario
            if ("administrador".equals(tipoUsuario)) {
                res.redirect("/dashboard-admin");
            } else if ("profesor".equals(tipoUsuario)) {
                res.redirect("/dashboard-profesor");
            } else {
                res.redirect("/dashboard-alumno");
            }
            return null;
        });

        get("/dashboard-admin", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String currentUsername = req.session().attribute("currentUserUsername");
            Boolean loggedIn = req.session().attribute("loggedIn");
            String tipoUsuario = req.session().attribute("tipoUsuario");

            if (currentUsername == null || loggedIn == null || !loggedIn || !"administrador".equals(tipoUsuario)) {
                res.redirect("/?error=Acceso no autorizado.");
                return null;
            }

            model.put("username", currentUsername);
            return new ModelAndView(model, "admin_dashboard.mustache");
        }, new MustacheTemplateEngine());

        get("/dashboard-profesor", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String currentUsername = req.session().attribute("currentUserUsername");
            Boolean loggedIn = req.session().attribute("loggedIn");
            String tipoUsuario = req.session().attribute("tipoUsuario");

            if (currentUsername == null || loggedIn == null || !loggedIn || !"profesor".equals(tipoUsuario)) {
                res.redirect("/?error=Acceso no autorizado.");
                return null;
            }

            model.put("username", currentUsername);
            return new ModelAndView(model, "profesor_dashboard.mustache");
        }, new MustacheTemplateEngine());

        get("/dashboard-alumno", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String currentUsername = req.session().attribute("currentUserUsername");
            Boolean loggedIn = req.session().attribute("loggedIn");
            String tipoUsuario = req.session().attribute("tipoUsuario");

            if (currentUsername == null || loggedIn == null || !loggedIn || "administrador".equals(tipoUsuario) || "profesor".equals(tipoUsuario)) {
                res.redirect("/?error=Acceso no autorizado.");
                return null;
            }

            model.put("username", currentUsername);
            return new ModelAndView(model, "alumno_dashboard.mustache");
        }, new MustacheTemplateEngine());

        // GET: Ruta para cerrar la sesión del usuario.
        get("/logout", (req, res) -> {
            // Invalida completamente la sesión del usuario.
            // Esto elimina todos los atributos guardados en la sesión y la marca como
            // inválida.
            // La cookie JSESSIONID en el navegador también será gestionada para
            // invalidarse.
            req.session().invalidate();

            System.out.println("DEBUG: Sesión cerrada. Redirigiendo a /login.");

            // Redirige al usuario a la página de login con un mensaje de éxito.
            res.redirect("/");

            return null; // Importante retornar null después de una redirección.
        });

        // GET: Ruta para mostrar el perfil del usuario logueado.
        get("/profile", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String currentUsername = req.session().attribute("currentUserUsername");
            Boolean loggedIn = req.session().attribute("loggedIn");
            String tipoUsuario = req.session().attribute("tipoUsuario");

            // 1. Verificar si el usuario ha iniciado sesión.
            if (currentUsername == null || loggedIn == null || !loggedIn) {
                res.redirect("/?error=Debes iniciar sesión para acceder a esta página.");
                return null;
            }

            model.put("username", currentUsername);
            model.put("tipo_usuario", tipoUsuario);

            // Determinar la ruta de retorno al dashboard y flags de rol para la visualización.
            String backUrl = "/dashboard";
            if ("administrador".equals(tipoUsuario)) {
                backUrl = "/dashboard-admin";
                model.put("isAdmin", true);
            } else if ("profesor".equals(tipoUsuario)) {
                backUrl = "/dashboard-profesor";
                model.put("isProfesor", true);
            } else {
                backUrl = "/dashboard-alumno";
                model.put("isAlumno", true);
            }
            model.put("backUrl", backUrl);

            // Intentar buscar los datos de Persona asociados a este user_login
            Persona p = (Persona) Persona.findFirst("user_login = ?", currentUsername);
            if (p != null) {
                model.put("persona", p.toMap());
                
                // Si es alumno o profesor, buscar legajo y tipo
                if ("profesor".equals(tipoUsuario)) {
                    Profesor prof = (Profesor) Profesor.findFirst("dni_persona = ?", p.get("dni"));
                    if (prof != null) {
                        model.put("legajo", prof.get("legajo_docente"));
                    }
                } else if ("alumno".equals(tipoUsuario)) {
                    Alumno alu = (Alumno) Alumno.findFirst("dni_persona = ?", p.get("dni"));
                    if (alu != null) {
                        model.put("legajo", alu.get("legajo"));
                        model.put("tipo_alumno", alu.get("tipo_alumno"));
                    }
                }
            }

            return new ModelAndView(model, "profile.mustache");
        }, new MustacheTemplateEngine());

        // GET: Muestra el formulario de inicio de sesión (login).
        // Nota: Esta ruta debería ser capaz de leer también mensajes de error/éxito de
        // los query params
        // si se la usa como destino de redirecciones. (Tu código de /user/create ya lo
        // hace, aplicar similar).
        get("/", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String errorMessage = req.queryParams("error");
            if (errorMessage != null && !errorMessage.isEmpty()) {
                model.put("errorMessage", errorMessage);
            }
            String successMessage = req.queryParams("message");
            if (successMessage != null && !successMessage.isEmpty()) {
                model.put("successMessage", successMessage);
            }
            return new ModelAndView(model, "login.mustache");
        }, new MustacheTemplateEngine()); // Especifica el motor de plantillas para esta ruta.

        // GET: Ruta de alias para el formulario de creación de cuenta.
        // En una aplicación real, probablemente querrías unificar con '/user/create'
        // para evitar duplicidad.
        get("/user/new", (req, res) -> {
            return new ModelAndView(new HashMap<>(), "user_form.mustache"); // No pasa un modelo específico, solo el
            // formulario.
        }, new MustacheTemplateEngine()); // Especifica el motor de plantillas para esta ruta.

        // --- Rutas POST para manejar envíos de formularios y APIs ---
        // POST: Maneja el envío del formulario de creación de nueva cuenta.
        post("/user/new", (req, res) -> {
            String name = req.queryParams("name");
            String password = req.queryParams("password");
            String tipo_usuario = req.queryParams("tipo_usuario");

            // Validaciones básicas: campos no pueden ser nulos o vacíos.
            if (name == null || name.isEmpty() || password == null || password.isEmpty() || tipo_usuario == null || tipo_usuario.isEmpty()) {
                res.status(400); // Código de estado HTTP 400 (Bad Request).
                // Redirige al formulario de creación con un mensaje de error.
                res.redirect("/user/create?error=Nombre y contraseña son requeridos.");
                return ""; // Retorna una cadena vacía ya que la respuesta ya fue redirigida.
            }

            try {
                // Intenta crear y guardar la nueva cuenta en la base de datos.
                User ac = new User(); // Crea una nueva instancia del modelo User.
                // Hashea la contraseña de forma segura antes de guardarla.
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                ac.set("name", name); // Asigna el nombre de usuario.
                ac.set("password", hashedPassword); // Asigna la contraseña hasheada.
                ac.set("tipo_usuario", tipo_usuario);
                ac.saveIt(); // Guarda el nuevo usuario en la tabla 'users'.

                res.status(201); // Código de estado HTTP 201 (Created) para una creación exitosa.
                // Redirige al formulario de creación con un mensaje de éxito.
                res.redirect("/user/create?message=Cuenta creada exitosamente para " + name + "!");
                return ""; // Retorna una cadena vacía.

            } catch (Exception e) {
                // Si ocurre cualquier error durante la operación de DB (ej. nombre de usuario
                // duplicado),
                // se captura aquí y se redirige con un mensaje de error.
                System.err.println("Error al registrar la cuenta: " + e.getMessage());
                e.printStackTrace(); // Imprime el stack trace para depuración.
                res.status(500); // Código de estado HTTP 500 (Internal Server Error).
                res.redirect("/user/create?error=Error interno al crear la cuenta. Intente de nuevo.");
                return ""; // Retorna una cadena vacía.
            }
        });

        // --- INICIO ABM ---
        // ======================= PROFESORES =======================
        get("/profesores", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Map<String, Object>> lista = new ArrayList<>();
            for (Model prof : Profesor.findAll()) {
                Map<String, Object> map = new HashMap<>(prof.toMap());
                Persona p = (Persona) Persona.findFirst("dni = ?", prof.get("dni_persona"));
                if (p != null) {
                    map.put("nombre", p.get("nombre"));
                    map.put("apellido", p.get("apellido"));
                    map.put("correo", p.get("correo"));
                }
                lista.add(map);
            }
            model.put("profesores", lista);
            return new ModelAndView(model, "profesores.mustache");
        }, new MustacheTemplateEngine());

        get("/profesores/new", (req, res) -> {
            return new ModelAndView(new HashMap<>(), "profesor_form.mustache");
        }, new MustacheTemplateEngine());

        post("/profesores/new", (req, res) -> {
            try {
                Base.openTransaction();
                String dni = req.queryParams("dni");
                Persona p = (Persona) Persona.findFirst("dni = ?", dni);
                boolean isNewPersona = false;
                if (p == null) {
                    p = new Persona();
                    p.set("dni", dni);
                    isNewPersona = true;
                }
                p.set("nombre", req.queryParams("nombre"));
                p.set("apellido", req.queryParams("apellido"));
                p.set("correo", req.queryParams("correo"));
                if (isNewPersona) {
                    p.insert();
                } else {
                    p.saveIt();
                }

                Profesor prof = new Profesor();
                prof.set("legajo_docente", req.queryParams("legajo"));
                prof.set("dni_persona", dni);
                prof.insert();
                Base.commitTransaction();
                res.redirect("/profesores");
            } catch (Exception e) {
                Base.rollbackTransaction();
                e.printStackTrace();
                res.redirect("/profesores/new?error=Error");
            }
            return "";
        });

        get("/profesores/:id/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            Profesor prof = (Profesor) Profesor.findFirst("legajo_docente = ?", req.params(":id"));
            if (prof != null) {
                model.put("profesor", prof.toMap());
                Persona p = (Persona) Persona.findFirst("dni = ?", prof.get("dni_persona"));
                if (p != null) {
                    model.put("persona", p.toMap());
                }
            }
            return new ModelAndView(model, "profesor_form.mustache");
        }, new MustacheTemplateEngine());

        post("/profesores/:id/edit", (req, res) -> {
            try {
                Base.openTransaction();
                Profesor prof = (Profesor) Profesor.findFirst("legajo_docente = ?", req.params(":id"));
                if (prof != null) {
                    Persona p = (Persona) Persona.findFirst("dni = ?", prof.get("dni_persona"));
                    if (p != null) {
                        p.set("nombre", req.queryParams("nombre"));
                        p.set("apellido", req.queryParams("apellido"));
                        p.set("correo", req.queryParams("correo"));
                        p.saveIt();
                    }
                }
                Base.commitTransaction();
                res.redirect("/profesores");
            } catch (Exception e) {
                Base.rollbackTransaction();
                res.redirect("/profesores");
            }
            return "";
        });

        post("/profesores/:id/delete", (req, res) -> {
            try {
                Profesor prof = (Profesor) Profesor.findFirst("legajo_docente = ?", req.params(":id"));
                if (prof != null) {
                    prof.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            res.redirect("/profesores");
            return "";
        });

        // ======================= ALUMNOS =======================
        get("/alumnos", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Map<String, Object>> lista = new ArrayList<>();
            for (Model alu : Alumno.findAll()) {
                Map<String, Object> map = new HashMap<>(alu.toMap());
                Persona p = (Persona) Persona.findFirst("dni = ?", alu.get("dni_persona"));
                if (p != null) {
                    map.put("nombre", p.get("nombre"));
                    map.put("apellido", p.get("apellido"));
                    map.put("correo", p.get("correo"));
                }
                lista.add(map);
            }
            model.put("alumnos", lista);
            return new ModelAndView(model, "alumnos.mustache");
        }, new MustacheTemplateEngine());

        get("/alumnos/new", (req, res) -> {
            return new ModelAndView(new HashMap<>(), "alumno_form.mustache");
        }, new MustacheTemplateEngine());

        post("/alumnos/new", (req, res) -> {
            try {
                Base.openTransaction();
                String dni = req.queryParams("dni");
                Persona p = (Persona) Persona.findFirst("dni = ?", dni);
                boolean isNewPersona = false;
                if (p == null) {
                    p = new Persona();
                    p.set("dni", dni);
                    isNewPersona = true;
                }
                p.set("nombre", req.queryParams("nombre"));
                p.set("apellido", req.queryParams("apellido"));
                p.set("correo", req.queryParams("correo"));
                if (isNewPersona) {
                    p.insert();
                } else {
                    p.saveIt();
                }

                Alumno alu = new Alumno();
                alu.set("legajo", req.queryParams("legajo"));
                alu.set("dni_persona", dni);
                alu.set("tipo_alumno", req.queryParams("tipo_alumno"));
                alu.insert();
                Base.commitTransaction();
                res.redirect("/alumnos");
            } catch (Exception e) {
                Base.rollbackTransaction();
                e.printStackTrace();
                res.redirect("/alumnos/new?error=Error");
            }
            return "";
        });

        get("/alumnos/:id/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            Alumno alu = (Alumno) Alumno.findFirst("legajo = ?", req.params(":id"));
            if (alu != null) {
                model.put("alumno", alu.toMap());
                Persona p = (Persona) Persona.findFirst("dni = ?", alu.get("dni_persona"));
                if (p != null) {
                    model.put("persona", p.toMap());
                }
            }
            return new ModelAndView(model, "alumno_form.mustache");
        }, new MustacheTemplateEngine());

        post("/alumnos/:id/edit", (req, res) -> {
            try {
                Base.openTransaction();
                Alumno alu = (Alumno) Alumno.findFirst("legajo = ?", req.params(":id"));
                if (alu != null) {
                    alu.set("tipo_alumno", req.queryParams("tipo_alumno"));
                    alu.saveIt();
                    Persona p = (Persona) Persona.findFirst("dni = ?", alu.get("dni_persona"));
                    if (p != null) {
                        p.set("nombre", req.queryParams("nombre"));
                        p.set("apellido", req.queryParams("apellido"));
                        p.set("correo", req.queryParams("correo"));
                        p.saveIt();
                    }
                }
                Base.commitTransaction();
                res.redirect("/alumnos");
            } catch (Exception e) {
                Base.rollbackTransaction();
                res.redirect("/alumnos");
            }
            return "";
        });

        post("/alumnos/:id/delete", (req, res) -> {
            try {
                Alumno alu = (Alumno) Alumno.findFirst("legajo = ?", req.params(":id"));
                if (alu != null) {
                    alu.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            res.redirect("/alumnos");
            return "";
        });

        // ======================= CARRERAS =======================
        get("/carreras", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Map<String, Object>> lista = new ArrayList<>();
            for (Model c : Carrera.findAll()) {
                lista.add(c.toMap());
            }
            model.put("carreras", lista);
            return new ModelAndView(model, "carreras.mustache");
        }, new MustacheTemplateEngine());

        get("/carreras/new", (req, res) -> {
            return new ModelAndView(new HashMap<>(), "carrera_form.mustache");
        }, new MustacheTemplateEngine());

        post("/carreras/new", (req, res) -> {
            try {
                Carrera c = new Carrera();
                c.set("id_carrera", req.queryParams("id_carrera"));
                c.set("codigo", req.queryParams("codigo"));
                c.set("nombre", req.queryParams("nombre"));
                c.set("duracion_anios", req.queryParams("duracion_anios"));
                c.insert();
                res.redirect("/carreras");
            } catch (Exception e) {
                e.printStackTrace();
                res.redirect("/carreras/new?error=Error");
            }
            return "";
        });

        get("/carreras/:id/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            Carrera c = (Carrera) Carrera.findFirst("id_carrera = ?", req.params(":id"));
            if (c != null) {
                model.put("carrera", c.toMap());
            }
            return new ModelAndView(model, "carrera_form.mustache");
        }, new MustacheTemplateEngine());

        post("/carreras/:id/edit", (req, res) -> {
            try {
                Carrera c = (Carrera) Carrera.findFirst("id_carrera = ?", req.params(":id"));
                if (c != null) {
                    c.set("codigo", req.queryParams("codigo"));
                    c.set("nombre", req.queryParams("nombre"));
                    c.set("duracion_anios", req.queryParams("duracion_anios"));
                    c.saveIt();
                }
                res.redirect("/carreras");
            } catch (Exception e) {
                res.redirect("/carreras");
            }
            return "";
        });

        post("/carreras/:id/delete", (req, res) -> {
            try {
                Carrera c = (Carrera) Carrera.findFirst("id_carrera = ?", req.params(":id"));
                if (c != null) {
                    c.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            res.redirect("/carreras");
            return "";
        });

        // ======================= PLAN DE ESTUDIO =======================
        get("/planes", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Map<String, Object>> lista = new ArrayList<>();
            for (Model p : PlanEstudio.findAll()) {
                lista.add(p.toMap());
            }
            model.put("planes", lista);
            return new ModelAndView(model, "planes.mustache");
        }, new MustacheTemplateEngine());

        get("/planes/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Map<String, Object>> lista = new ArrayList<>();
            for (Model c : Carrera.findAll()) {
                lista.add(c.toMap());
            }
            model.put("carreras", lista);
            return new ModelAndView(model, "plan_form.mustache");
        }, new MustacheTemplateEngine());

        post("/planes/new", (req, res) -> {
            try {
                PlanEstudio p = new PlanEstudio();
                p.set("id_plan", req.queryParams("id_plan"));
                p.set("resolucion", req.queryParams("resolucion"));
                p.set("anio_vigencia", req.queryParams("anio_vigencia"));
                p.set("estado", req.queryParams("estado"));
                p.set("id_carrera", req.queryParams("id_carrera"));
                p.insert();
                res.redirect("/planes");
            } catch (Exception e) {
                e.printStackTrace();
                res.redirect("/planes/new?error=Error");
            }
            return "";
        });

        get("/planes/:id/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            PlanEstudio p = (PlanEstudio) PlanEstudio.findFirst("id_plan = ?", req.params(":id"));
            if (p != null)
                model.put("plan", p.toMap());
            List<Map<String, Object>> lista = new ArrayList<>();
            for (Model c : Carrera.findAll()) {
                lista.add(c.toMap());
            }
            model.put("carreras", lista);
            return new ModelAndView(model, "plan_form.mustache");
        }, new MustacheTemplateEngine());

        post("/planes/:id/edit", (req, res) -> {
            try {
                PlanEstudio p = (PlanEstudio) PlanEstudio.findFirst("id_plan = ?", req.params(":id"));
                if (p != null) {
                    p.set("resolucion", req.queryParams("resolucion"));
                    p.set("anio_vigencia", req.queryParams("anio_vigencia"));
                    p.set("estado", req.queryParams("estado"));
                    p.set("id_carrera", req.queryParams("id_carrera"));
                    p.saveIt();
                }
                res.redirect("/planes");
            } catch (Exception e) {
                res.redirect("/planes");
            }
            return "";
        });

        post("/planes/:id/delete", (req, res) -> {
            try {
                PlanEstudio p = (PlanEstudio) PlanEstudio.findFirst("id_plan = ?", req.params(":id"));
                if (p != null) {
                    p.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            res.redirect("/planes");
            return "";
        });

        // ======================= MATERIAS =======================
        get("/materias", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Map<String, Object>> lista = new ArrayList<>();
            for (Model m : Materia.findAll()) {
                lista.add(m.toMap());
            }
            model.put("materias", lista);
            return new ModelAndView(model, "materias.mustache");
        }, new MustacheTemplateEngine());

        get("/materias/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            List<Map<String, Object>> lista = new ArrayList<>();
            for (Model p : PlanEstudio.findAll()) {
                lista.add(p.toMap());
            }
            model.put("planes", lista);
            return new ModelAndView(model, "materia_form.mustache");
        }, new MustacheTemplateEngine());

        post("/materias/new", (req, res) -> {
            try {
                Materia m = new Materia();
                m.set("id_materia", req.queryParams("id_materia"));
                m.set("codigo", req.queryParams("codigo"));
                m.set("nombre", req.queryParams("nombre"));
                m.set("periodo", req.queryParams("periodo"));
                m.set("id_plan", req.queryParams("id_plan"));
                m.insert();
                res.redirect("/materias");
            } catch (Exception e) {
                e.printStackTrace();
                res.redirect("/materias/new?error=Error");
            }
            return "";
        });

        get("/materias/:id/edit", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            Materia m = (Materia) Materia.findFirst("id_materia = ?", req.params(":id"));
            if (m != null)
                model.put("materia", m.toMap());
            List<Map<String, Object>> lista = new ArrayList<>();
            for (Model p : PlanEstudio.findAll()) {
                lista.add(p.toMap());
            }
            model.put("planes", lista);
            return new ModelAndView(model, "materia_form.mustache");
        }, new MustacheTemplateEngine());

        post("/materias/:id/edit", (req, res) -> {
            try {
                Materia m = (Materia) Materia.findFirst("id_materia = ?", req.params(":id"));
                if (m != null) {
                    m.set("codigo", req.queryParams("codigo"));
                    m.set("nombre", req.queryParams("nombre"));
                    m.set("periodo", req.queryParams("periodo"));
                    m.set("id_plan", req.queryParams("id_plan"));
                    m.saveIt();
                }
                res.redirect("/materias");
            } catch (Exception e) {
                res.redirect("/materias");
            }
            return "";
        });

        post("/materias/:id/delete", (req, res) -> {
            try {
                Materia m = (Materia) Materia.findFirst("id_materia = ?", req.params(":id"));
                if (m != null) {
                    m.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            res.redirect("/materias");
            return "";
        });

        // --- FIN ABM ---
        // POST: Maneja el envío del formulario de inicio de sesión.
        post("/login", (req, res) -> {
            Map<String, Object> model = new HashMap<>(); // Modelo para la plantilla de login o dashboard.

            String username = req.queryParams("username");
            String plainTextPassword = req.queryParams("password");

            // Validaciones básicas: campos de usuario y contraseña no pueden ser nulos o
            // vacíos.
            if (username == null || username.isEmpty() || plainTextPassword == null || plainTextPassword.isEmpty()) {
                res.status(400); // Bad Request.
                model.put("errorMessage", "El nombre de usuario y la contraseña son requeridos.");
                return new ModelAndView(model, "login.mustache"); // Renderiza la plantilla de login con error.
            }

            // Busca la cuenta en la base de datos por el nombre de usuario.
            User ac = (User) User.findFirst("name = ?", username);

            // Si no se encuentra ninguna cuenta con ese nombre de usuario.
            if (ac == null) {
                res.status(401); // Unauthorized.
                model.put("errorMessage", "Usuario o contraseña incorrectos."); // Mensaje genérico por seguridad.
                return new ModelAndView(model, "login.mustache"); // Renderiza la plantilla de login con error.
            }

            // Obtiene la contraseña hasheada almacenada en la base de datos.
            String storedHashedPassword = ac.getString("password");

            // Compara la contraseña en texto plano ingresada con la contraseña hasheada
            // almacenada.
            // BCrypt.checkpw hashea la plainTextPassword con el salt de
            // storedHashedPassword y compara.
            if (BCrypt.checkpw(plainTextPassword, storedHashedPassword)) {
                // Autenticación exitosa.
                res.status(200); // OK.

                // --- Gestión de Sesión ---
                req.session(true).attribute("currentUserUsername", username); // Guarda el nombre de usuario en la
                // sesión.
                req.session().attribute("userId", ac.getId()); // Guarda el ID de la cuenta en la sesión (útil).
                req.session().attribute("loggedIn", true); // Establece una bandera para indicar que el usuario está
                // logueado.

                System.out.println("DEBUG: Login exitoso para la cuenta: " + username);
                System.out.println("DEBUG: ID de Sesión: " + req.session().id());

                String tipoUsuario = ac.getString("tipo_usuario");

                req.session().attribute("tipoUsuario", tipoUsuario);

                if ("administrador".equals(tipoUsuario)) {
                    res.redirect("/dashboard-admin");
                } else if ("profesor".equals(tipoUsuario)) {
                    res.redirect("/dashboard-profesor");
                } else {
                    res.redirect("/dashboard-alumno");
                }

                return null;

            } else {
                // Contraseña incorrecta.
                res.status(401); // Unauthorized.
                System.out.println("DEBUG: Intento de login fallido para: " + username);
                model.put("errorMessage", "Usuario o contraseña incorrectos."); // Mensaje genérico por seguridad.
                return new ModelAndView(model, "login.mustache"); // Renderiza la plantilla de login con error.
            }
        }, new MustacheTemplateEngine()); // Especifica el motor de plantillas para esta ruta POST.

        // POST: Endpoint para añadir usuarios (API que devuelve JSON, no HTML).
        // Advertencia: Esta ruta tiene un propósito diferente a las de formulario HTML.
        post("/add_users", (req, res) -> {
            res.type("application/json"); // Establece el tipo de contenido de la respuesta a JSON.

            // Obtiene los parámetros 'name' y 'password' de la solicitud.
            String name = req.queryParams("name");
            String password = req.queryParams("password");

            // --- Validaciones básicas ---
            if (name == null || name.isEmpty() || password == null || password.isEmpty()) {
                res.status(400); // Bad Request.
                return objectMapper.writeValueAsString(Map.of("error", "Nombre y contraseña son requeridos."));
            }

            try {
                // --- Creación y guardado del usuario usando el modelo ActiveJDBC ---
                User newUser = new User(); // Crea una nueva instancia de tu modelo User.
                // ¡ADVERTENCIA DE SEGURIDAD CRÍTICA!
                // En una aplicación real, las contraseñas DEBEN ser hasheadas (ej. con BCrypt)
                // ANTES de guardarse en la base de datos, NUNCA en texto plano.
                // (Nota: El código original tenía la contraseña en texto plano aquí.
                // Se recomienda usar `BCrypt.hashpw(password, BCrypt.gensalt())` como en la
                // ruta '/user/new').
                newUser.set("name", name); // Asigna el nombre al campo 'name'.
                newUser.set("password", password); // Asigna la contraseña al campo 'password'.
                newUser.saveIt(); // Guarda el nuevo usuario en la tabla 'users'.

                res.status(201); // Created.
                // Devuelve una respuesta JSON con el mensaje y el ID del nuevo usuario.
                return objectMapper.writeValueAsString(
                        Map.of("message", "Usuario '" + name + "' registrado con éxito.", "id", newUser.getId()));

            } catch (Exception e) {
                // Si ocurre cualquier error durante la operación de DB, se captura aquí.
                System.err.println("Error al registrar usuario: " + e.getMessage());
                e.printStackTrace(); // Imprime el stack trace para depuración.
                res.status(500); // Internal Server Error.
                return objectMapper
                        .writeValueAsString(Map.of("error", "Error interno al registrar usuario: " + e.getMessage()));
            }
        });

    } // Fin del método main
} // Fin de la clase App
