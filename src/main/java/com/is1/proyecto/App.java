package com.is1.proyecto; // Define el paquete de la aplicación, debe coincidir con la estructura de carpetas.

// Importaciones necesarias para la aplicación Spark
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.Model;
import org.mindrot.jbcrypt.BCrypt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.is1.proyecto.config.DBConfigSingleton;
import com.is1.proyecto.models.Alumno;
import com.is1.proyecto.models.AsignacionDocente;
import com.is1.proyecto.models.Carrera;
import com.is1.proyecto.models.Catedra;
import com.is1.proyecto.models.Inscripcion;
import com.is1.proyecto.models.Materia;
import com.is1.proyecto.models.Nota;
import com.is1.proyecto.models.Persona;
import com.is1.proyecto.models.PlanEstudio;
import com.is1.proyecto.models.Profesor;
import com.is1.proyecto.models.User;

import spark.ModelAndView;
import static spark.Spark.after;
import static spark.Spark.before;
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

    private static String encode(String value) {
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }

    /**
     * Método principaor l que se ejecuta al iniciar la aplicación. Aquí se
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
            if (currentUsername == null || loggedIn == null || !loggedIn) {                // Redirige al login principal con un mensaje de error.
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
                halt();
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
                halt();
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
                halt();
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
            req.session().invalidate();            // Redirige al usuario a la página de login con un mensaje de éxito.
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
                halt();
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

        get("/login", (req, res) -> {
            res.redirect("/");
            return null;
        });

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


        // POST: Maneja el envío del formulario de creación de nueva cuenta.
        post("/user/new", (req, res) -> {
            String name = req.queryParams("name");
            String password = req.queryParams("password");
            String tipo_usuario = req.queryParams("tipo_usuario");

            // Validaciones básicas: campos no pueden ser nulos o vacíos.
            if (name == null || name.isEmpty() || password == null || password.isEmpty() || tipo_usuario == null || tipo_usuario.isEmpty()) {
                res.status(400); // Código de estado HTTP 400 (Bad Request).
                // Redirige al formulario de creación con un mensaje de error.
                res.redirect("/user/create?error=" + encode("Nombre y contraseña son requeridos."));
                return ""; // Retorna una cadena vacía ya que la respuesta ya fue redirigida.
            }

            try {
                Base.openTransaction();

                // Intenta crear y guardar la nueva cuenta en la base de datos.
                User ac = new User(); // Crea una nueva instancia del modelo User.
                // Hashea la contraseña de forma segura antes de guardarla.
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

                ac.set("name", name); // Asigna el nombre de usuario.
                ac.set("password", hashedPassword); // Asigna la contraseña hasheada.
                ac.set("tipo_usuario", tipo_usuario);
                ac.saveIt(); // Guarda el nuevo usuario en la tabla 'users'.

                // Obtener ID generado para construir datos únicos
                Object userIdObj = ac.getId();
                String userIdStr = userIdObj != null ? userIdObj.toString() : String.valueOf(System.currentTimeMillis() % 10000);
                String dummyDniStr = "DNI" + userIdStr;

                // Crear Persona vinculada
                Persona p = new Persona();
                p.set("dni", dummyDniStr);
                p.set("nombre", name);
                p.set("apellido", "Registrado");
                p.set("correo", name + "@universidad.edu.ar");
                p.set("user_login", name);
                p.set("pass_login", password);
                p.insert();

                // Crear registro según tipo de usuario para aparecer en listados ABM
                if ("profesor".equals(tipo_usuario)) {
                    Profesor prof = new Profesor();
                    prof.set("legajo_docente", "PROF" + String.format("%03d", Integer.parseInt(userIdStr)));
                    prof.set("dni_persona", dummyDniStr);
                    prof.insert();
                } else if ("alumno".equals(tipo_usuario)) {
                    Alumno alu = new Alumno();
                    int legajo = 20000 + Integer.parseInt(userIdStr);
                    alu.set("legajo", legajo);
                    alu.set("dni_persona", dummyDniStr);
                    alu.set("tipo_alumno", "INGRESANTE");
                    
                    // Buscar primer plan disponible en la base de datos
                    PlanEstudio plan = (PlanEstudio) PlanEstudio.findFirst("1=1");
                    if (plan != null) {
                        alu.set("id_plan", plan.getId());
                    } else {
                        alu.set("id_plan", 1);
                    }
                    alu.insert();
                } else if ("administrador".equals(tipo_usuario)) {
                    com.is1.proyecto.models.Administrador adm = new com.is1.proyecto.models.Administrador();
                    adm.set("dni_persona", dummyDniStr);
                    adm.set("cargo_administrative", "Administrador Registrado");
                    adm.insert();
                }

                Base.commitTransaction();

                res.status(201); // Código de estado HTTP 201 (Created) para una creación exitosa.
                // Redirige al formulario de creación con un mensaje de éxito.
                res.redirect("/user/create?message=" + encode("Cuenta creada exitosamente para " + name + "!"));
                return ""; // Retorna una cadena vacía.

            } catch (Exception e) {
                Base.rollbackTransaction();
                // Si ocurre cualquier error durante la operación de DB (ej. nombre de usuario
                // duplicado),
                // se captura aquí y se redirige con un mensaje de error.
                System.err.println("Error al registrar la cuenta: " + e.getMessage());
                e.printStackTrace(); // Imprime el stack trace para depuración.
                res.status(500); // Código de estado HTTP 500 (Internal Server Error).
                res.redirect("/user/create?error=" + encode("Error interno al crear la cuenta. Intente de nuevo."));
                return ""; // Retorna una cadena vacía.
            }
        });

        // --- INICIO ABM ---
        before((req, res) -> {
            String path = req.pathInfo();
            if (path.startsWith("/profesores") || path.startsWith("/alumnos") || path.startsWith("/carreras") || path.startsWith("/planes") || path.startsWith("/materias") || path.startsWith("/catedras") || path.startsWith("/inscripcion") || path.startsWith("/mis-carreras") || path.startsWith("/profesor") || path.startsWith("/alumno")) {
                if (req.session().attribute("loggedIn") == null) {
                    res.redirect("/");
                    halt();
                }
            }
        });
        
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
                    map.put("user_login", p.get("user_login"));
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
                String userLogin = req.queryParams("user_login");
                String passLogin = req.queryParams("pass_login");

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
                p.set("user_login", userLogin);
                p.set("pass_login", passLogin);
                if (isNewPersona) {
                    p.insert();
                } else {
                    p.saveIt();
                }

                Profesor prof = new Profesor();
                prof.set("legajo_docente", req.queryParams("legajo"));
                prof.set("dni_persona", dni);
                prof.insert();

                // Crear usuario de acceso al sistema
                if (userLogin != null && !userLogin.isEmpty() && passLogin != null && !passLogin.isEmpty()) {
                    User existingUser = (User) User.findFirst("name = ?", userLogin);
                    if (existingUser == null) {
                        User newUser = new User();
                        newUser.set("name", userLogin);
                        newUser.set("password", BCrypt.hashpw(passLogin, BCrypt.gensalt()));
                        newUser.set("tipo_usuario", "profesor");
                        newUser.saveIt();
                    }
                }

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
                    String userLogin = req.queryParams("user_login");
                    String passLogin = req.queryParams("pass_login");

                    Persona p = (Persona) Persona.findFirst("dni = ?", prof.get("dni_persona"));
                    if (p != null) {
                        String oldUserLogin = p.getString("user_login");
                        p.set("nombre", req.queryParams("nombre"));
                        p.set("apellido", req.queryParams("apellido"));
                        p.set("correo", req.queryParams("correo"));
                        p.set("user_login", userLogin);
                        if (passLogin != null && !passLogin.isEmpty()) {
                            p.set("pass_login", passLogin);
                        }
                        p.saveIt();

                        // Actualizar o crear usuario de acceso
                        if (userLogin != null && !userLogin.isEmpty()) {
                            User user = (User) User.findFirst("name = ?", oldUserLogin != null ? oldUserLogin : userLogin);
                            if (user != null) {
                                user.set("name", userLogin);
                                if (passLogin != null && !passLogin.isEmpty()) {
                                    user.set("password", BCrypt.hashpw(passLogin, BCrypt.gensalt()));
                                }
                                user.saveIt();
                            } else {
                                // Crear usuario si no existía
                                if (passLogin != null && !passLogin.isEmpty()) {
                                    User newUser = new User();
                                    newUser.set("name", userLogin);
                                    newUser.set("password", BCrypt.hashpw(passLogin, BCrypt.gensalt()));
                                    newUser.set("tipo_usuario", "profesor");
                                    newUser.saveIt();
                                }
                            }
                        }
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
                    map.put("user_login", p.get("user_login"));
                }
                lista.add(map);
            }
            model.put("alumnos", lista);
            return new ModelAndView(model, "alumnos.mustache");
        }, new MustacheTemplateEngine());

        get("/alumnos/new", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            // Cargar planes de estudio con nombre de carrera para el selector
            List<Map<String, Object>> planesLista = new ArrayList<>();
            for (Model plan : PlanEstudio.findAll()) {
                Map<String, Object> planMap = new HashMap<>(plan.toMap());
                Carrera carr = (Carrera) Carrera.findFirst("id_carrera = ?", plan.get("id_carrera"));
                planMap.put("nombre_carrera", carr != null ? carr.get("nombre") : "Sin carrera");
                planesLista.add(planMap);
            }
            model.put("planes", planesLista);
            return new ModelAndView(model, "alumno_form.mustache");
        }, new MustacheTemplateEngine());

        post("/alumnos/new", (req, res) -> {
            try {
                Base.openTransaction();
                String dni = req.queryParams("dni");
                String userLogin = req.queryParams("user_login");
                String passLogin = req.queryParams("pass_login");

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
                p.set("user_login", userLogin);
                p.set("pass_login", passLogin);
                if (isNewPersona) {
                    p.insert();
                } else {
                    p.saveIt();
                }

                Alumno alu = new Alumno();
                alu.set("legajo", req.queryParams("legajo"));
                alu.set("dni_persona", dni);
                alu.set("tipo_alumno", req.queryParams("tipo_alumno"));
                String idPlanParam = req.queryParams("id_plan");
                if (idPlanParam != null && !idPlanParam.isEmpty()) {
                    alu.set("id_plan", Integer.parseInt(idPlanParam));
                }
                alu.insert();

                // Crear usuario de acceso al sistema
                if (userLogin != null && !userLogin.isEmpty() && passLogin != null && !passLogin.isEmpty()) {
                    User existingUser = (User) User.findFirst("name = ?", userLogin);
                    if (existingUser == null) {
                        User newUser = new User();
                        newUser.set("name", userLogin);
                        newUser.set("password", BCrypt.hashpw(passLogin, BCrypt.gensalt()));
                        newUser.set("tipo_usuario", "alumno");
                        newUser.saveIt();
                    }
                }

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
            // Cargar planes de estudio con nombre de carrera y marcar el seleccionado
            List<Map<String, Object>> planesLista = new ArrayList<>();
            for (Model plan : PlanEstudio.findAll()) {
                Map<String, Object> planMap = new HashMap<>(plan.toMap());
                Carrera carr = (Carrera) Carrera.findFirst("id_carrera = ?", plan.get("id_carrera"));
                planMap.put("nombre_carrera", carr != null ? carr.get("nombre") : "Sin carrera");
                if (alu != null && plan.get("id_plan") != null && plan.get("id_plan").equals(alu.get("id_plan"))) {
                    planMap.put("selected", true);
                }
                planesLista.add(planMap);
            }
            model.put("planes", planesLista);
            return new ModelAndView(model, "alumno_form.mustache");
        }, new MustacheTemplateEngine());

        post("/alumnos/:id/edit", (req, res) -> {
            try {
                Base.openTransaction();
                Alumno alu = (Alumno) Alumno.findFirst("legajo = ?", req.params(":id"));
                if (alu != null) {
                    String userLogin = req.queryParams("user_login");
                    String passLogin = req.queryParams("pass_login");

                    alu.set("tipo_alumno", req.queryParams("tipo_alumno"));
                    String idPlanParam = req.queryParams("id_plan");
                    if (idPlanParam != null && !idPlanParam.isEmpty()) {
                        alu.set("id_plan", Integer.parseInt(idPlanParam));
                    }
                    alu.saveIt();
                    Persona p = (Persona) Persona.findFirst("dni = ?", alu.get("dni_persona"));
                    if (p != null) {
                        String oldUserLogin = p.getString("user_login");
                        p.set("nombre", req.queryParams("nombre"));
                        p.set("apellido", req.queryParams("apellido"));
                        p.set("correo", req.queryParams("correo"));
                        p.set("user_login", userLogin);
                        if (passLogin != null && !passLogin.isEmpty()) {
                            p.set("pass_login", passLogin);
                        }
                        p.saveIt();

                        // Actualizar o crear usuario de acceso
                        if (userLogin != null && !userLogin.isEmpty()) {
                            User user = (User) User.findFirst("name = ?", oldUserLogin != null ? oldUserLogin : userLogin);
                            if (user != null) {
                                user.set("name", userLogin);
                                if (passLogin != null && !passLogin.isEmpty()) {
                                    user.set("password", BCrypt.hashpw(passLogin, BCrypt.gensalt()));
                                }
                                user.saveIt();
                            } else {
                                // Crear usuario si no existía
                                if (passLogin != null && !passLogin.isEmpty()) {
                                    User newUser = new User();
                                    newUser.set("name", userLogin);
                                    newUser.set("password", BCrypt.hashpw(passLogin, BCrypt.gensalt()));
                                    newUser.set("tipo_usuario", "alumno");
                                    newUser.saveIt();
                                }
                            }
                        }
                    }
                }
                Base.commitTransaction();
                res.redirect("/alumnos");
            } catch (Exception e) {
                Base.rollbackTransaction();
                e.printStackTrace();
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
                Map<String, Object> cMap = new HashMap<>(c.toMap());
                if (p != null && c.get("id_carrera") != null && c.get("id_carrera").equals(p.get("id_carrera"))) {
                    cMap.put("selected", true);
                }
                lista.add(cMap);
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

        // ======================= INSCRIPCION A MATERIAS (Issue 29) =======================
        // GET: Muestra la página de inscripción con las materias del plan del alumno
        get("/inscripcion", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String currentUsername = req.session().attribute("currentUserUsername");
            Boolean loggedIn = req.session().attribute("loggedIn");
            String tipoUsuario = req.session().attribute("tipoUsuario");

            // Validar sesión de alumno
            if (currentUsername == null || loggedIn == null || !loggedIn) {
                res.redirect("/?error=Debes iniciar sesión para acceder a esta página.");
                halt();
                return null;
            }
            if ("administrador".equals(tipoUsuario) || "profesor".equals(tipoUsuario)) {
                res.redirect("/?error=Acceso no autorizado.");
                halt();
                return null;
            }

            model.put("username", currentUsername);

            // Mensajes de éxito/error
            String successMessage = req.queryParams("message");
            if (successMessage != null && !successMessage.isEmpty()) {
                model.put("successMessage", successMessage);
            }
            String errorMessage = req.queryParams("error");
            if (errorMessage != null && !errorMessage.isEmpty()) {
                model.put("errorMessage", errorMessage);
            }

            // Buscar Persona y Alumno asociados al usuario logueado
            Persona persona = (Persona) Persona.findFirst("user_login = ?", currentUsername);
            if (persona == null) {
                model.put("errorMessage", "No se encontraron datos personales vinculados a tu usuario.");
                return new ModelAndView(model, "inscripcion.mustache");
            }

            Alumno alumno = (Alumno) Alumno.findFirst("dni_persona = ?", persona.get("dni"));
            if (alumno == null) {
                model.put("errorMessage", "No se encontró un registro de alumno asociado a tu cuenta.");
                return new ModelAndView(model, "inscripcion.mustache");
            }

            Integer idPlan = alumno.getInteger("id_plan");
            if (idPlan == null) {
                model.put("errorMessage", "No tienes un plan de estudio asignado. Contacta a la administración.");
                return new ModelAndView(model, "inscripcion.mustache");
            }

            int legajoAlumno = alumno.getInteger("legajo");
            model.put("legajo", legajoAlumno);

            // Obtener info del plan y carrera para mostrar
            PlanEstudio plan = (PlanEstudio) PlanEstudio.findFirst("id_plan = ?", idPlan);
            if (plan != null) {
                model.put("plan_resolucion", plan.get("resolucion"));
                Carrera carrera = (Carrera) Carrera.findFirst("id_carrera = ?", plan.get("id_carrera"));
                if (carrera != null) {
                    model.put("carrera_nombre", carrera.get("nombre"));
                }
            }

            // Obtener todas las materias del plan del alumno
            List<Model> materias = Materia.find("id_plan = ?", idPlan);
            List<Map<String, Object>> materiasList = new ArrayList<>();

            for (Model materia : materias) {
                Map<String, Object> materiaMap = new HashMap<>();
                int idMateria = materia.getInteger("id_materia");
                materiaMap.put("id_materia", idMateria);
                materiaMap.put("codigo", materia.get("codigo"));
                materiaMap.put("nombre", materia.get("nombre"));
                materiaMap.put("periodo", materia.get("periodo"));

                // Determinar estado del alumno en esta materia
                // Buscar si el alumno tiene alguna inscripción activa (EN_CURSADA, REGULAR, APROBADA)
                List<Model> catedrasDeMateria = Catedra.find("id_materia = ?", idMateria);
                boolean yaInscripto = false;
                boolean aprobada = false;
                String estadoActual = null;

                for (Model cat : catedrasDeMateria) {
                    Inscripcion insc = (Inscripcion) Inscripcion.findFirst(
                        "legajo_alumno = ? AND id_catedra = ? AND (estado_inscripcion = 'EN_CURSADA' OR estado_inscripcion = 'REGULAR' OR estado_inscripcion = 'APROBADA')",
                        legajoAlumno, cat.getInteger("id_catedra"));
                    if (insc != null) {
                        estadoActual = insc.getString("estado_inscripcion");
                        yaInscripto = true;
                        if ("APROBADA".equals(estadoActual)) {
                            aprobada = true;
                        } else {
                            // Si no está aprobada, verificar si tiene notas registradas
                            boolean tieneNotas = Nota.findFirst("id_inscripcion = ?", insc.get("id_inscripcion")) != null;
                            if (!tieneNotas) {
                                materiaMap.put("puede_desinscribirse", true);
                                materiaMap.put("id_inscripcion", insc.get("id_inscripcion"));
                            }
                        }
                        break;
                    }
                }

                // Verificar correlativas previas
                boolean cumpleCorrelativas = true;
                List<String> correlativasFaltantes = new ArrayList<>();
                List<Map> correlativas = Base.findAll(
                    "SELECT id_materia_correlativa FROM Correlativas_previas WHERE id_materia = ?", idMateria);
                for (Map corr : correlativas) {
                    int idCorrelativa = ((Number) corr.get("id_materia_correlativa")).intValue();
                    // Verificar que el alumno tenga la correlativa APROBADA
                    boolean correlativaAprobada = false;
                    List<Model> catedrasCorr = Catedra.find("id_materia = ?", idCorrelativa);
                    for (Model catCorr : catedrasCorr) {
                        Inscripcion inscCorr = (Inscripcion) Inscripcion.findFirst(
                            "legajo_alumno = ? AND id_catedra = ? AND estado_inscripcion = 'APROBADA'",
                            legajoAlumno, catCorr.getInteger("id_catedra"));
                        if (inscCorr != null) {
                            correlativaAprobada = true;
                            break;
                        }
                    }
                    if (!correlativaAprobada) {
                        cumpleCorrelativas = false;
                        Materia materiaCorr = (Materia) Materia.findFirst("id_materia = ?", idCorrelativa);
                        if (materiaCorr != null) {
                            correlativasFaltantes.add(materiaCorr.getString("nombre"));
                        }
                    }
                }

                // Obtener cátedras disponibles para esta materia
                List<Map<String, Object>> catedrasList = new ArrayList<>();
                for (Model cat : catedrasDeMateria) {
                    Map<String, Object> catMap = new HashMap<>();
                    catMap.put("id_catedra", cat.getInteger("id_catedra"));
                    catMap.put("anio", cat.get("anio"));
                    catMap.put("comision", cat.get("comision"));
                    catedrasList.add(catMap);
                }
                materiaMap.put("catedras", catedrasList);
                materiaMap.put("tiene_catedras", !catedrasList.isEmpty());

                // Definir el estado para la UI
                if (aprobada) {
                    materiaMap.put("estado", "APROBADA");
                    materiaMap.put("es_aprobada", true);
                } else if (yaInscripto) {
                    materiaMap.put("estado", estadoActual);
                    materiaMap.put("ya_inscripto", true);
                } else if (!cumpleCorrelativas) {
                    materiaMap.put("estado", "FALTAN_CORRELATIVAS");
                    materiaMap.put("faltan_correlativas", true);
                    materiaMap.put("correlativas_faltantes", String.join(", ", correlativasFaltantes));
                } else {
                    materiaMap.put("estado", "DISPONIBLE");
                    materiaMap.put("disponible", true);
                }

                materiasList.add(materiaMap);
            }

            model.put("materias", materiasList);
            model.put("tiene_materias", !materiasList.isEmpty());

            return new ModelAndView(model, "inscripcion.mustache");
        }, new MustacheTemplateEngine());

        // POST: Procesa la inscripción de un alumno a una cátedra
        post("/inscripcion", (req, res) -> {
            String currentUsername = req.session().attribute("currentUserUsername");
            Boolean loggedIn = req.session().attribute("loggedIn");
            String tipoUsuario = req.session().attribute("tipoUsuario");

            if (currentUsername == null || loggedIn == null || !loggedIn) {
                res.redirect("/?error=" + encode("Debes iniciar sesión."));
                return "";
            }
            if ("administrador".equals(tipoUsuario) || "profesor".equals(tipoUsuario)) {
                res.redirect("/?error=" + encode("Acceso no autorizado."));
                return "";
            }

            String idCatedraParam = req.queryParams("id_catedra");
            if (idCatedraParam == null || idCatedraParam.isEmpty()) {
                res.redirect("/inscripcion?error=" + encode("Debe seleccionar una cátedra."));
                return "";
            }

            try {
                int idCatedra = Integer.parseInt(idCatedraParam);

                // Obtener el alumno desde la sesión
                Persona persona = (Persona) Persona.findFirst("user_login = ?", currentUsername);
                if (persona == null) {
                    res.redirect("/inscripcion?error=" + encode("No se encontraron datos personales."));
                    return "";
                }
                Alumno alumno = (Alumno) Alumno.findFirst("dni_persona = ?", persona.get("dni"));
                if (alumno == null) {
                    res.redirect("/inscripcion?error=" + encode("No se encontró registro de alumno."));
                    return "";
                }

                int legajoAlumno = alumno.getInteger("legajo");

                // Obtener la cátedra y materia para validaciones
                Catedra catedra = (Catedra) Catedra.findFirst("id_catedra = ?", idCatedra);
                if (catedra == null) {
                    res.redirect("/inscripcion?error=" + encode("Cátedra no encontrada."));
                    return "";
                }
                int idMateria = catedra.getInteger("id_materia");

                // VALIDACIÓN 1: Verificar que no esté ya inscripto en esta materia
                List<Model> catedrasDeMateria = Catedra.find("id_materia = ?", idMateria);
                for (Model cat : catedrasDeMateria) {
                    Inscripcion inscExistente = (Inscripcion) Inscripcion.findFirst(
                        "legajo_alumno = ? AND id_catedra = ? AND (estado_inscripcion = 'EN_CURSADA' OR estado_inscripcion = 'REGULAR' OR estado_inscripcion = 'APROBADA')",
                        legajoAlumno, cat.getInteger("id_catedra"));
                    if (inscExistente != null) {
                        res.redirect("/inscripcion?error=" + encode("Ya estás inscripto en esta materia (estado: " + inscExistente.getString("estado_inscripcion") + ")."));
                        return "";
                    }
                }

                // VALIDACIÓN 2: Verificar correlativas previas
                List<Map> correlativas = Base.findAll(
                    "SELECT id_materia_correlativa FROM Correlativas_previas WHERE id_materia = ?", idMateria);
                for (Map corr : correlativas) {
                    int idCorrelativa = ((Number) corr.get("id_materia_correlativa")).intValue();
                    boolean correlativaAprobada = false;
                    List<Model> catedrasCorr = Catedra.find("id_materia = ?", idCorrelativa);
                    for (Model catCorr : catedrasCorr) {
                        Inscripcion inscCorr = (Inscripcion) Inscripcion.findFirst(
                            "legajo_alumno = ? AND id_catedra = ? AND estado_inscripcion = 'APROBADA'",
                            legajoAlumno, catCorr.getInteger("id_catedra"));
                        if (inscCorr != null) {
                            correlativaAprobada = true;
                            break;
                        }
                    }
                    if (!correlativaAprobada) {
                        Materia materiaCorr = (Materia) Materia.findFirst("id_materia = ?", idCorrelativa);
                        String nombreCorr = materiaCorr != null ? materiaCorr.getString("nombre") : "Materia ID " + idCorrelativa;
                        res.redirect("/inscripcion?error=" + encode("No cumples con la correlativa: " + nombreCorr));
                        return "";
                    }
                }

                // Generar nuevo ID de inscripción
                List<Map> maxIdResult = Base.findAll("SELECT COALESCE(MAX(id_inscripcion), 0) as max_id FROM Inscripcion");
                int nuevoId = ((Number) maxIdResult.get(0).get("max_id")).intValue() + 1;

                // Crear la inscripción
                Inscripcion nuevaInscripcion = new Inscripcion();
                nuevaInscripcion.set("id_inscripcion", nuevoId);
                nuevaInscripcion.set("fecha_inscripcion", (int) (System.currentTimeMillis() / 1000));
                nuevaInscripcion.set("estado_inscripcion", "EN_CURSADA");
                nuevaInscripcion.set("legajo_alumno", legajoAlumno);
                nuevaInscripcion.set("id_catedra", idCatedra);
                nuevaInscripcion.insert();

                // Obtener nombre de la materia para el mensaje de éxito
                Materia mat = (Materia) Materia.findFirst("id_materia = ?", idMateria);
                String nombreMateria = mat != null ? mat.getString("nombre") : "la materia";

                res.redirect("/inscripcion?message=" + encode("¡Inscripción exitosa en " + nombreMateria + "!"));
                return "";

            } catch (Exception e) {
                e.printStackTrace();
                res.redirect("/inscripcion?error=" + encode("Error al procesar la inscripción: " + e.getMessage()));
                return "";
            }
        });

        // POST: Cancela la inscripción de un alumno a una cátedra (desuscribirse)
        post("/inscripcion/cancelar", (req, res) -> {
            String currentUsername = req.session().attribute("currentUserUsername");
            Boolean loggedIn = req.session().attribute("loggedIn");
            String tipoUsuario = req.session().attribute("tipoUsuario");

            if (currentUsername == null || loggedIn == null || !loggedIn) {
                res.redirect("/?error=" + encode("Debes iniciar sesión."));
                return "";
            }
            if ("administrador".equals(tipoUsuario) || "profesor".equals(tipoUsuario)) {
                res.redirect("/?error=" + encode("Acceso no autorizado."));
                return "";
            }

            String idInscripcionParam = req.queryParams("id_inscripcion");
            if (idInscripcionParam == null || idInscripcionParam.isEmpty()) {
                res.redirect("/inscripcion?error=" + encode("Falta el identificador de la inscripción."));
                return "";
            }

            try {
                int idInscripcion = Integer.parseInt(idInscripcionParam);

                // Obtener el alumno desde la sesión
                Persona persona = (Persona) Persona.findFirst("user_login = ?", currentUsername);
                if (persona == null) {
                    res.redirect("/inscripcion?error=" + encode("No se encontraron datos personales."));
                    return "";
                }
                Alumno alumno = (Alumno) Alumno.findFirst("dni_persona = ?", persona.get("dni"));
                if (alumno == null) {
                    res.redirect("/inscripcion?error=" + encode("No se encontró registro de alumno."));
                    return "";
                }

                int legajoAlumno = alumno.getInteger("legajo");

                // Buscar la inscripción y validar que pertenezca al alumno logueado
                Inscripcion insc = (Inscripcion) Inscripcion.findFirst("id_inscripcion = ?", idInscripcion);
                if (insc == null) {
                    res.redirect("/inscripcion?error=" + encode("Inscripción no encontrada."));
                    return "";
                }

                if (insc.getInteger("legajo_alumno") != legajoAlumno) {
                    res.redirect("/inscripcion?error=" + encode("No tienes permisos para cancelar esta inscripción."));
                    return "";
                }

                // VALIDACIÓN: Verificar si tiene notas registradas
                boolean tieneNotas = Nota.findFirst("id_inscripcion = ?", idInscripcion) != null;
                if (tieneNotas) {
                    res.redirect("/inscripcion?error=" + encode("No puedes desinscribirte porque ya tienes notas registradas en esta materia."));
                    return "";
                }

                // Eliminar la inscripción
                insc.delete();

                res.redirect("/inscripcion?message=" + encode("Te has desinscripto de la materia exitosamente."));
                return "";

            } catch (Exception e) {
                e.printStackTrace();
                res.redirect("/inscripcion?error=" + encode("Error al procesar la desuscripción: " + e.getMessage()));
                return "";
            }
        });

        // ======================= MIS CARRERAS (Vista Alumno) =======================
        // GET: Muestra las carreras disponibles y en cuáles el alumno está inscripto
        get("/mis-carreras", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String currentUsername = req.session().attribute("currentUserUsername");
            Boolean loggedIn = req.session().attribute("loggedIn");
            String tipoUsuario = req.session().attribute("tipoUsuario");

            // Validar sesión de alumno
            if (currentUsername == null || loggedIn == null || !loggedIn) {
                res.redirect("/?error=Debes iniciar sesión para acceder a esta página.");
                halt();
                return null;
            }
            if ("administrador".equals(tipoUsuario) || "profesor".equals(tipoUsuario)) {
                res.redirect("/?error=Acceso no autorizado.");
                halt();
                return null;
            }

            model.put("username", currentUsername);

            // Mensajes de éxito/error
            String successMessage = req.queryParams("message");
            if (successMessage != null && !successMessage.isEmpty()) {
                model.put("successMessage", successMessage);
            }
            String errorMessage = req.queryParams("error");
            if (errorMessage != null && !errorMessage.isEmpty()) {
                model.put("errorMessage", errorMessage);
            }

            // Buscar Persona y Alumno asociados al usuario logueado
            Persona persona = (Persona) Persona.findFirst("user_login = ?", currentUsername);
            if (persona == null) {
                model.put("errorMessage", "No se encontraron datos personales vinculados a tu usuario.");
                return new ModelAndView(model, "mis_carreras.mustache");
            }

            Alumno alumno = (Alumno) Alumno.findFirst("dni_persona = ?", persona.get("dni"));
            if (alumno == null) {
                model.put("errorMessage", "No se encontró un registro de alumno asociado a tu cuenta.");
                return new ModelAndView(model, "mis_carreras.mustache");
            }

            // Determinar la carrera del alumno a través de su plan de estudio
            Integer idPlanAlumno = alumno.getInteger("id_plan");
            Integer idCarreraAlumno = null;
            String planResolucionAlumno = null;

            if (idPlanAlumno != null) {
                PlanEstudio planAlumno = (PlanEstudio) PlanEstudio.findFirst("id_plan = ?", idPlanAlumno);
                if (planAlumno != null) {
                    idCarreraAlumno = planAlumno.getInteger("id_carrera");
                    planResolucionAlumno = planAlumno.getString("resolucion");
                }
            }

            // Obtener todas las carreras
            List<Model> todasLasCarreras = Carrera.findAll();
            List<Map<String, Object>> carrerasInscriptas = new ArrayList<>();
            List<Map<String, Object>> carrerasDisponibles = new ArrayList<>();

            for (Model c : todasLasCarreras) {
                Map<String, Object> carreraMap = new HashMap<>(c.toMap());
                int idCarrera = c.getInteger("id_carrera");

                if (idCarreraAlumno != null && idCarrera == idCarreraAlumno) {
                    // El alumno está inscripto en esta carrera
                    carreraMap.put("inscripta", true);
                    carreraMap.put("plan_resolucion", planResolucionAlumno);
                    carrerasInscriptas.add(carreraMap);
                } else {
                    // Carrera disponible para inscripción
                    carreraMap.put("disponible", true);
                    carrerasDisponibles.add(carreraMap);
                }
            }

            model.put("carreras_inscriptas", carrerasInscriptas);
            model.put("carreras_disponibles", carrerasDisponibles);
            model.put("tiene_inscriptas", !carrerasInscriptas.isEmpty());
            model.put("tiene_disponibles", !carrerasDisponibles.isEmpty());
            model.put("tiene_carreras", !todasLasCarreras.isEmpty());
            model.put("total_inscriptas", carrerasInscriptas.size());
            model.put("total_carreras", todasLasCarreras.size());
            model.put("total_disponibles", carrerasDisponibles.size());

            return new ModelAndView(model, "mis_carreras.mustache");
        }, new MustacheTemplateEngine());

        // POST: Inscribe al alumno en una carrera asignándole un plan de estudio de esa carrera
        post("/mis-carreras/inscribir", (req, res) -> {
            String currentUsername = req.session().attribute("currentUserUsername");
            Boolean loggedIn = req.session().attribute("loggedIn");
            String tipoUsuario = req.session().attribute("tipoUsuario");

            if (currentUsername == null || loggedIn == null || !loggedIn) {
                res.redirect("/?error=" + encode("Debes iniciar sesión."));
                return "";
            }
            if ("administrador".equals(tipoUsuario) || "profesor".equals(tipoUsuario)) {
                res.redirect("/?error=" + encode("Acceso no autorizado."));
                return "";
            }

            String idCarreraParam = req.queryParams("id_carrera");
            if (idCarreraParam == null || idCarreraParam.isEmpty()) {
                res.redirect("/mis-carreras?error=" + encode("Debe seleccionar una carrera."));
                return "";
            }

            try {
                int idCarrera = Integer.parseInt(idCarreraParam);

                // Obtener el alumno
                Persona persona = (Persona) Persona.findFirst("user_login = ?", currentUsername);
                if (persona == null) {
                    res.redirect("/mis-carreras?error=" + encode("No se encontraron datos personales."));
                    return "";
                }
                Alumno alumno = (Alumno) Alumno.findFirst("dni_persona = ?", persona.get("dni"));
                if (alumno == null) {
                    res.redirect("/mis-carreras?error=" + encode("No se encontró registro de alumno."));
                    return "";
                }

                // Verificar que la carrera exista
                Carrera carrera = (Carrera) Carrera.findFirst("id_carrera = ?", idCarrera);
                if (carrera == null) {
                    res.redirect("/mis-carreras?error=" + encode("Carrera no encontrada."));
                    return "";
                }

                // Verificar que el alumno no esté ya inscripto en esa carrera
                Integer idPlanActual = alumno.getInteger("id_plan");
                if (idPlanActual != null) {
                    PlanEstudio planActual = (PlanEstudio) PlanEstudio.findFirst("id_plan = ?", idPlanActual);
                    if (planActual != null && planActual.getInteger("id_carrera") == idCarrera) {
                        res.redirect("/mis-carreras?error=" + encode("Ya estás inscripto en esta carrera."));
                        return "";
                    }
                }

                // Buscar un plan de estudio vigente de esa carrera
                PlanEstudio planCarrera = (PlanEstudio) PlanEstudio.findFirst("id_carrera = ?", idCarrera);
                if (planCarrera == null) {
                    res.redirect("/mis-carreras?error=" + encode("No hay planes de estudio disponibles para esta carrera. Contacta a administración."));
                    return "";
                }

                // Asignar el plan de estudio al alumno
                alumno.set("id_plan", planCarrera.getInteger("id_plan"));
                alumno.saveIt();

                res.redirect("/mis-carreras?message=" + encode("¡Te inscribiste exitosamente en " + carrera.getString("nombre") + "!"));
                return "";

            } catch (Exception e) {
                e.printStackTrace();
                res.redirect("/mis-carreras?error=" + encode("Error al procesar la inscripción: " + e.getMessage()));
                return "";
            }
        });

        // ======================= ASIGNACIÓN PROFESOR - MATERIAS =======================
        // GET: Muestra las cátedras/materias y permite asignar un profesor
        get("/profesores/:id/materias", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String legajoDocente = req.params(":id");

            // Mensajes de éxito/error
            String successMessage = req.queryParams("message");
            if (successMessage != null && !successMessage.isEmpty()) {
                model.put("successMessage", successMessage);
            }
            String errorMessage = req.queryParams("error");
            if (errorMessage != null && !errorMessage.isEmpty()) {
                model.put("errorMessage", errorMessage);
            }

            // Buscar el profesor
            Profesor prof = (Profesor) Profesor.findFirst("legajo_docente = ?", legajoDocente);
            if (prof == null) {
                res.redirect("/profesores?error=Profesor no encontrado.");
                halt();
                return null;
            }

            model.put("legajo_docente", legajoDocente);

            // Obtener nombre del profesor
            Persona persona = (Persona) Persona.findFirst("dni = ?", prof.get("dni_persona"));
            if (persona != null) {
                model.put("profesor_nombre", persona.get("nombre"));
                model.put("profesor_apellido", persona.get("apellido"));
            }

            // Obtener todas las cátedras
            List<Model> todasCatedras = Catedra.findAll();
            List<Map<String, Object>> catedrasAsignadas = new ArrayList<>();
            List<Map<String, Object>> catedrasDisponibles = new ArrayList<>();

            // Obtener asignaciones existentes del profesor
            List<Model> asignaciones = AsignacionDocente.find("legajo_docente = ?", legajoDocente);
            // Crear un set de id_catedra asignadas para búsqueda rápida
            Map<Integer, String> catedrasAsignadasMap = new HashMap<>();
            for (Model asig : asignaciones) {
                catedrasAsignadasMap.put(asig.getInteger("id_catedra"), asig.getString("rol"));
            }

            for (Model cat : todasCatedras) {
                Map<String, Object> catMap = new HashMap<>();
                int idCatedra = cat.getInteger("id_catedra");
                catMap.put("id_catedra", idCatedra);
                catMap.put("anio", cat.get("anio"));
                catMap.put("comision", cat.get("comision"));
                catMap.put("legajo_docente", legajoDocente);

                // Obtener nombre de la materia
                Materia mat = (Materia) Materia.findFirst("id_materia = ?", cat.get("id_materia"));
                catMap.put("materia_nombre", mat != null ? mat.get("nombre") : "Sin materia");

                if (catedrasAsignadasMap.containsKey(idCatedra)) {
                    catMap.put("asignada", true);
                    catMap.put("rol", catedrasAsignadasMap.get(idCatedra));
                    catedrasAsignadas.add(catMap);
                } else {
                    catMap.put("disponible", true);
                    catedrasDisponibles.add(catMap);
                }
            }

            model.put("catedras_asignadas", catedrasAsignadas);
            model.put("catedras_disponibles", catedrasDisponibles);
            model.put("tiene_asignadas", !catedrasAsignadas.isEmpty());
            model.put("tiene_disponibles", !catedrasDisponibles.isEmpty());
            model.put("tiene_catedras", !todasCatedras.isEmpty());
            model.put("total_asignadas", catedrasAsignadas.size());
            model.put("total_catedras", todasCatedras.size());
            model.put("total_disponibles", catedrasDisponibles.size());

            return new ModelAndView(model, "profesor_materias.mustache");
        }, new MustacheTemplateEngine());

        // POST: Asigna un profesor a una cátedra con un rol específico
        post("/profesores/:id/materias/asignar", (req, res) -> {
            String legajoDocente = req.params(":id");
            String idCatedraParam = req.queryParams("id_catedra");
            String rol = req.queryParams("rol");

            if (idCatedraParam == null || idCatedraParam.isEmpty() || rol == null || rol.isEmpty()) {
                res.redirect("/profesores/" + legajoDocente + "/materias?error=" + encode("Debe seleccionar una cátedra y un rol."));
                return "";
            }

            try {
                int idCatedra = Integer.parseInt(idCatedraParam);

                // Verificar que el profesor exista
                Profesor prof = (Profesor) Profesor.findFirst("legajo_docente = ?", legajoDocente);
                if (prof == null) {
                    res.redirect("/profesores?error=" + encode("Profesor no encontrado."));
                    return "";
                }

                // Verificar que la cátedra exista
                Catedra catedra = (Catedra) Catedra.findFirst("id_catedra = ?", idCatedra);
                if (catedra == null) {
                    res.redirect("/profesores/" + legajoDocente + "/materias?error=" + encode("Cátedra no encontrada."));
                    return "";
                }

                // Verificar que no esté ya asignado
                AsignacionDocente existente = (AsignacionDocente) AsignacionDocente.findFirst(
                    "legajo_docente = ? AND id_catedra = ?", legajoDocente, idCatedra);
                if (existente != null) {
                    res.redirect("/profesores/" + legajoDocente + "/materias?error=" + encode("El profesor ya está asignado a esta cátedra."));
                    return "";
                }

                // Crear la asignación
                AsignacionDocente nuevaAsignacion = new AsignacionDocente();
                nuevaAsignacion.set("legajo_docente", legajoDocente);
                nuevaAsignacion.set("id_catedra", idCatedra);
                nuevaAsignacion.set("rol", rol);
                nuevaAsignacion.set("fecha_asignacion", (int) (System.currentTimeMillis() / 1000));
                nuevaAsignacion.insert();

                // Obtener nombre de materia para el mensaje
                Materia mat = (Materia) Materia.findFirst("id_materia = ?", catedra.get("id_materia"));
                String nombreMateria = mat != null ? mat.getString("nombre") : "la materia";

                res.redirect("/profesores/" + legajoDocente + "/materias?message=" + encode("¡Profesor asignado exitosamente a " + nombreMateria + " (" + rol + ")!"));
                return "";

            } catch (Exception e) {
                e.printStackTrace();
                res.redirect("/profesores/" + legajoDocente + "/materias?error=" + encode("Error al procesar la asignación: " + e.getMessage()));
                return "";
            }
        });

        // ======================= PORTAL ALUMNO: MIS MATERIAS Y NOTAS =======================
        get("/alumno/materias", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String currentUsername = req.session().attribute("currentUserUsername");
            Boolean loggedIn = req.session().attribute("loggedIn");
            String tipoUsuario = req.session().attribute("tipoUsuario");

            if (currentUsername == null || loggedIn == null || !loggedIn) {
                res.redirect("/?error=Debes iniciar sesión para acceder a esta página.");
                halt();
                return null;
            }
            if ("administrador".equals(tipoUsuario) || "profesor".equals(tipoUsuario)) {
                res.redirect("/?error=Acceso no autorizado.");
                halt();
                return null;
            }

            model.put("username", currentUsername);

            // Buscar Persona y Alumno
            Persona persona = (Persona) Persona.findFirst("user_login = ?", currentUsername);
            if (persona == null) {
                model.put("errorMessage", "No se encontraron datos personales vinculados a tu usuario.");
                return new ModelAndView(model, "alumno_materias_list.mustache");
            }

            Alumno alumno = (Alumno) Alumno.findFirst("dni_persona = ?", persona.get("dni"));
            if (alumno == null) {
                model.put("errorMessage", "No se encontró un registro de alumno asociado a tu cuenta.");
                return new ModelAndView(model, "alumno_materias_list.mustache");
            }

            int legajoAlumno = alumno.getInteger("legajo");
            model.put("legajo_alumno", legajoAlumno);

            // Buscar Plan de estudio y Carrera
            Integer idPlan = alumno.getInteger("id_plan");
            if (idPlan != null) {
                PlanEstudio plan = (PlanEstudio) PlanEstudio.findFirst("id_plan = ?", idPlan);
                if (plan != null) {
                    model.put("tiene_plan", true);
                    model.put("plan_resolucion", plan.getString("resolucion"));
                    model.put("plan_anio", plan.get("anio_vigencia"));
                    
                    Carrera carrera = (Carrera) Carrera.findFirst("id_carrera = ?", plan.get("id_carrera"));
                    model.put("carrera_nombre", carrera != null ? carrera.getString("nombre") : "Sin Nombre");
                }
            } else {
                model.put("tiene_plan", false);
            }

            // Buscar inscripciones a materias del alumno
            List<Model> inscripciones = Inscripcion.find("legajo_alumno = ?", legajoAlumno);
            List<Map<String, Object>> materiasInscriptas = new ArrayList<>();

            for (Model ins : inscripciones) {
                Map<String, Object> item = new HashMap<>();
                int idInscripcion = ins.getInteger("id_inscripcion");
                String estado = ins.getString("estado_inscripcion");
                item.put("id_inscripcion", idInscripcion);
                item.put("estado_inscripcion", estado);
                
                // Tipo de CSS para el badge
                if (estado != null) {
                    item.put("estado_css", estado.toLowerCase());
                } else {
                    item.put("estado_css", "en_cursada");
                }

                // Cátedra y Materia
                int idCatedra = ins.getInteger("id_catedra");
                Catedra cat = (Catedra) Catedra.findFirst("id_catedra = ?", idCatedra);
                if (cat != null) {
                    item.put("anio", cat.get("anio"));
                    item.put("comision", cat.get("comision"));
                    
                    Materia mat = (Materia) Materia.findFirst("id_materia = ?", cat.get("id_materia"));
                    item.put("materia_nombre", mat != null ? mat.getString("nombre") : "Sin Nombre");
                } else {
                    item.put("materia_nombre", "Cátedra Inexistente");
                    item.put("anio", "-");
                    item.put("comision", "-");
                }

                // Notas asociadas a esta inscripción
                List<Model> notasList = Nota.find("id_inscripcion = ?", idInscripcion);
                List<Map<String, Object>> notasMapList = new ArrayList<>();
                for (Model n : notasList) {
                    Map<String, Object> notaMap = new HashMap<>();
                    notaMap.put("valor", n.getInteger("valor"));
                    String tipo = n.getString("tipo_nota");
                    notaMap.put("tipo_nota", tipo);
                    
                    // Formatear fecha
                    Integer fechaInt = n.getInteger("fecha");
                    if (fechaInt != null) {
                        java.util.Date d = new java.util.Date(fechaInt * 1000L);
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                        notaMap.put("fecha_formateada", sdf.format(d));
                    } else {
                        notaMap.put("fecha_formateada", "-");
                    }

                    if ("PARCIAL".equalsIgnoreCase(tipo)) {
                        notaMap.put("tipo_css", "parcial");
                    } else if ("TP".equalsIgnoreCase(tipo)) {
                        notaMap.put("tipo_css", "tp");
                    } else {
                        notaMap.put("tipo_css", "final");
                    }
                    notasMapList.add(notaMap);
                }
                item.put("notas", notasMapList);
                materiasInscriptas.add(item);
            }

            model.put("materias_inscriptas", materiasInscriptas);
            model.put("tiene_materias", !materiasInscriptas.isEmpty());

            return new ModelAndView(model, "alumno_materias_list.mustache");
        }, new MustacheTemplateEngine());

        // ======================= PORTAL DOCENTE: MIS MATERIAS Y NOTAS =======================
        // GET: Muestra la lista de materias/cátedras asignadas al profesor logueado
        get("/profesor/materias", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String currentUsername = req.session().attribute("currentUserUsername");
            Boolean loggedIn = req.session().attribute("loggedIn");
            String tipoUsuario = req.session().attribute("tipoUsuario");

            if (currentUsername == null || loggedIn == null || !loggedIn || !"profesor".equals(tipoUsuario)) {
                res.redirect("/?error=Acceso no autorizado.");
                halt();
                return null;
            }

            model.put("username", currentUsername);

            Persona p = (Persona) Persona.findFirst("user_login = ?", currentUsername);
            if (p == null) {
                model.put("errorMessage", "No se encontraron datos personales vinculados a tu usuario.");
                return new ModelAndView(model, "profesor_materias_list.mustache");
            }

            Profesor prof = (Profesor) Profesor.findFirst("dni_persona = ?", p.get("dni"));
            if (prof == null) {
                model.put("errorMessage", "No se encontró un registro de profesor asociado a tu cuenta.");
                return new ModelAndView(model, "profesor_materias_list.mustache");
            }

            String legajoDocente = prof.getString("legajo_docente");
            model.put("legajo_docente", legajoDocente);

            // Buscar materias asignadas a este profesor
            List<Model> asignaciones = AsignacionDocente.find("legajo_docente = ?", legajoDocente);
            List<Map<String, Object>> materiasAsignadas = new ArrayList<>();

            for (Model asig : asignaciones) {
                Map<String, Object> item = new HashMap<>();
                int idCatedra = asig.getInteger("id_catedra");
                item.put("id_catedra", idCatedra);
                item.put("rol", asig.getString("rol"));

                Catedra cat = (Catedra) Catedra.findFirst("id_catedra = ?", idCatedra);
                if (cat != null) {
                    item.put("anio", cat.get("anio"));
                    item.put("comision", cat.get("comision"));
                    
                    Materia mat = (Materia) Materia.findFirst("id_materia = ?", cat.get("id_materia"));
                    item.put("materia_nombre", mat != null ? mat.getString("nombre") : "Sin Nombre");
                } else {
                    item.put("materia_nombre", "Cátedra Inexistente");
                }
                materiasAsignadas.add(item);
            }

            model.put("materias_asignadas", materiasAsignadas);
            model.put("tiene_materias", !materiasAsignadas.isEmpty());

            return new ModelAndView(model, "profesor_materias_list.mustache");
        }, new MustacheTemplateEngine());

        // GET: Muestra los alumnos y sus notas de una cátedra específica
        get("/profesor/materias/:id_catedra/notas", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String currentUsername = req.session().attribute("currentUserUsername");
            Boolean loggedIn = req.session().attribute("loggedIn");
            String tipoUsuario = req.session().attribute("tipoUsuario");
            String idCatedraParam = req.params(":id_catedra");

            if (currentUsername == null || loggedIn == null || !loggedIn || !"profesor".equals(tipoUsuario)) {
                res.redirect("/?error=Acceso no autorizado.");
                halt();
                return null;
            }

            // Mensajes de éxito/error de los query parameters
            String successMessage = req.queryParams("message");
            if (successMessage != null && !successMessage.isEmpty()) {
                model.put("successMessage", successMessage);
            }
            String errorMessage = req.queryParams("error");
            if (errorMessage != null && !errorMessage.isEmpty()) {
                model.put("errorMessage", errorMessage);
            }

            model.put("username", currentUsername);
            int idCatedra = Integer.parseInt(idCatedraParam);
            model.put("id_catedra", idCatedra);

            // Obtener datos de la cátedra
            Catedra cat = (Catedra) Catedra.findFirst("id_catedra = ?", idCatedra);
            if (cat == null) {
                res.redirect("/profesor/materias?error=Cátedra no encontrada.");
                halt();
                return null;
            }
            model.put("anio", cat.get("anio"));
            model.put("comision", cat.get("comision"));

            Materia mat = (Materia) Materia.findFirst("id_materia = ?", cat.get("id_materia"));
            model.put("materia_nombre", mat != null ? mat.getString("nombre") : "Sin Nombre");

            // Buscar alumnos inscritos en esta cátedra
            List<Model> inscripciones = Inscripcion.find("id_catedra = ?", idCatedra);
            List<Map<String, Object>> alumnosInscriptos = new ArrayList<>();

            for (Model ins : inscripciones) {
                Map<String, Object> alumMap = new HashMap<>();
                int idInscripcion = ins.getInteger("id_inscripcion");
                alumMap.put("id_inscripcion", idInscripcion);
                int legajo = ins.getInteger("legajo_alumno");
                alumMap.put("legajo", legajo);

                // Persona del Alumno
                Alumno alum = (Alumno) Alumno.findFirst("legajo = ?", legajo);
                if (alum != null) {
                    Persona pers = (Persona) Persona.findFirst("dni = ?", alum.get("dni_persona"));
                    if (pers != null) {
                        alumMap.put("nombre", pers.getString("nombre"));
                        alumMap.put("apellido", pers.getString("apellido"));
                    } else {
                        alumMap.put("nombre", "Sin");
                        alumMap.put("apellido", "Nombre");
                    }
                }

                // Buscar notas del alumno en esta inscripción
                List<Model> notasList = Nota.find("id_inscripcion = ?", idInscripcion);
                List<Map<String, Object>> notasMapList = new ArrayList<>();
                for (Model n : notasList) {
                    Map<String, Object> notaMap = new HashMap<>();
                    notaMap.put("id_nota", n.getInteger("id_nota"));
                    notaMap.put("valor", n.getInteger("valor"));
                    
                    String tipo = n.getString("tipo_nota");
                    notaMap.put("tipo_nota", tipo);
                    
                    // Tipo CSS class for color badges
                    if ("PARCIAL".equalsIgnoreCase(tipo)) {
                        notaMap.put("tipo_css", "parcial");
                    } else if ("TP".equalsIgnoreCase(tipo)) {
                        notaMap.put("tipo_css", "tp");
                    } else {
                        notaMap.put("tipo_css", "final");
                    }
                    
                    notasMapList.add(notaMap);
                }
                alumMap.put("notas", notasMapList);
                alumnosInscriptos.add(alumMap);
            }

            model.put("alumnos_inscriptos", alumnosInscriptos);
            model.put("tiene_alumnos", !alumnosInscriptos.isEmpty());

            return new ModelAndView(model, "profesor_cargar_notas.mustache");
        }, new MustacheTemplateEngine());

        // POST: Carga una nueva calificación para un alumno en una cátedra
        post("/profesor/materias/:id_catedra/notas/agregar", (req, res) -> {
            String currentUsername = req.session().attribute("currentUserUsername");
            Boolean loggedIn = req.session().attribute("loggedIn");
            String tipoUsuario = req.session().attribute("tipoUsuario");
            String idCatedraParam = req.params(":id_catedra");

            if (currentUsername == null || loggedIn == null || !loggedIn || !"profesor".equals(tipoUsuario)) {
                res.redirect("/?error=" + encode("Acceso no autorizado."));
                return "";
            }

            String idInscripcionParam = req.queryParams("id_inscripcion");
            String tipoNota = req.queryParams("tipo_nota");
            String valorParam = req.queryParams("valor");

            if (idInscripcionParam == null || tipoNota == null || valorParam == null) {
                res.redirect("/profesor/materias/" + idCatedraParam + "/notas?error=" + encode("Faltan datos requeridos."));
                return "";
            }

            try {
                int idInscripcion = Integer.parseInt(idInscripcionParam);
                int valor = Integer.parseInt(valorParam);

                if (valor < 1 || valor > 10) {
                    res.redirect("/profesor/materias/" + idCatedraParam + "/notas?error=" + encode("La nota debe estar entre 1 y 10."));
                    return "";
                }

                // Generar nuevo ID de nota (siguiendo el mismo patrón manual)
                List<Map> maxIdResult = Base.findAll("SELECT COALESCE(MAX(id_nota), 0) as max_id FROM Nota");
                int nuevoId = ((Number) maxIdResult.get(0).get("max_id")).intValue() + 1;

                Nota nuevaNota = new Nota();
                nuevaNota.set("id_nota", nuevoId);
                nuevaNota.set("valor", valor);
                nuevaNota.set("tipo_nota", tipoNota);
                nuevaNota.set("fecha", (int) (System.currentTimeMillis() / 1000));
                nuevaNota.set("id_inscripcion", idInscripcion);
                nuevaNota.insert();

                res.redirect("/profesor/materias/" + idCatedraParam + "/notas?message=" + encode("Nota cargada con éxito."));
                return "";
            } catch (Exception e) {
                e.printStackTrace();
                res.redirect("/profesor/materias/" + idCatedraParam + "/notas?error=" + encode("Error al cargar la nota: " + e.getMessage()));
                return "";
            }
        });

        // POST: Elimina una nota cargada por error
        post("/profesor/materias/:id_catedra/notas/:id_nota/delete", (req, res) -> {
            String currentUsername = req.session().attribute("currentUserUsername");
            Boolean loggedIn = req.session().attribute("loggedIn");
            String tipoUsuario = req.session().attribute("tipoUsuario");
            String idCatedraParam = req.params(":id_catedra");
            String idNotaParam = req.params(":id_nota");

            if (currentUsername == null || loggedIn == null || !loggedIn || !"profesor".equals(tipoUsuario)) {
                res.redirect("/?error=" + encode("Acceso no autorizado."));
                return "";
            }

            try {
                int idNota = Integer.parseInt(idNotaParam);
                Nota n = (Nota) Nota.findFirst("id_nota = ?", idNota);
                if (n != null) {
                    n.delete();
                    res.redirect("/profesor/materias/" + idCatedraParam + "/notas?message=" + encode("Nota eliminada con éxito."));
                } else {
                    res.redirect("/profesor/materias/" + idCatedraParam + "/notas?error=" + encode("La nota no existe."));
                }
                return "";
            } catch (Exception e) {
                e.printStackTrace();
                res.redirect("/profesor/materias/" + idCatedraParam + "/notas?error=" + encode("Error al eliminar la nota: " + e.getMessage()));
                return "";
            }
        });

        // GET: Muestra la lista integral de alumnos inscriptos en todas las cátedras del profesor
        get("/profesor/alumnos", (req, res) -> {
            Map<String, Object> model = new HashMap<>();
            String currentUsername = req.session().attribute("currentUserUsername");
            Boolean loggedIn = req.session().attribute("loggedIn");
            String tipoUsuario = req.session().attribute("tipoUsuario");

            if (currentUsername == null || loggedIn == null || !loggedIn || !"profesor".equals(tipoUsuario)) {
                res.redirect("/?error=Acceso no autorizado.");
                halt();
                return null;
            }

            model.put("username", currentUsername);

            Persona p = (Persona) Persona.findFirst("user_login = ?", currentUsername);
            if (p == null) {
                model.put("errorMessage", "No se encontraron datos personales vinculados a tu usuario.");
                return new ModelAndView(model, "profesor_alumnos_list.mustache");
            }

            Profesor prof = (Profesor) Profesor.findFirst("dni_persona = ?", p.get("dni"));
            if (prof == null) {
                model.put("errorMessage", "No se encontró un registro de profesor asociado a tu cuenta.");
                return new ModelAndView(model, "profesor_alumnos_list.mustache");
            }

            String legajoDocente = prof.getString("legajo_docente");
            model.put("legajo_docente", legajoDocente);

            // Buscar cátedras asignadas a este profesor
            List<Model> asignaciones = AsignacionDocente.find("legajo_docente = ?", legajoDocente);
            List<Map<String, Object>> catedrasList = new ArrayList<>();
            List<Integer> idCatedras = new ArrayList<>();

            for (Model asig : asignaciones) {
                int idCatedra = asig.getInteger("id_catedra");
                Catedra cat = (Catedra) Catedra.findFirst("id_catedra = ?", idCatedra);
                if (cat != null) {
                    Map<String, Object> catMap = new HashMap<>();
                    catMap.put("id_catedra", idCatedra);
                    catMap.put("comision", cat.get("comision"));
                    catMap.put("anio", cat.get("anio"));
                    
                    Materia mat = (Materia) Materia.findFirst("id_materia = ?", cat.get("id_materia"));
                    String materiaNombre = mat != null ? mat.getString("nombre") : "Sin Nombre";
                    catMap.put("materia_nombre", materiaNombre);
                    catedrasList.add(catMap);
                    idCatedras.add(idCatedra);
                }
            }
            model.put("catedras", catedrasList);

            // Buscar alumnos inscriptos en todas estas cátedras
            List<Map<String, Object>> alumnosInscriptos = new ArrayList<>();
            if (!idCatedras.isEmpty()) {
                for (int idCatedra : idCatedras) {
                    Catedra cat = (Catedra) Catedra.findFirst("id_catedra = ?", idCatedra);
                    if (cat == null) continue;
                    Materia mat = (Materia) Materia.findFirst("id_materia = ?", cat.get("id_materia"));
                    String materiaNombre = mat != null ? mat.getString("nombre") : "Sin Nombre";

                    List<Model> inscripciones = Inscripcion.find("id_catedra = ?", idCatedra);
                    for (Model ins : inscripciones) {
                        Map<String, Object> alumMap = new HashMap<>();
                        int idInscripcion = ins.getInteger("id_inscripcion");
                        String estado = ins.getString("estado_inscripcion");
                        alumMap.put("id_inscripcion", idInscripcion);
                        alumMap.put("estado_inscripcion", estado);
                        alumMap.put("id_catedra", idCatedra);
                        alumMap.put("materia_nombre", materiaNombre);
                        alumMap.put("comision", cat.get("comision"));
                        alumMap.put("anio", cat.get("anio"));

                        int legajo = ins.getInteger("legajo_alumno");
                        alumMap.put("legajo", legajo);

                        // Datos del Alumno y su Persona
                        Alumno alum = (Alumno) Alumno.findFirst("legajo = ?", legajo);
                        if (alum != null) {
                            alumMap.put("tipo_alumno", alum.get("tipo_alumno"));
                            Persona pers = (Persona) Persona.findFirst("dni = ?", alum.get("dni_persona"));
                            if (pers != null) {
                                alumMap.put("nombre", pers.getString("nombre"));
                                alumMap.put("apellido", pers.getString("apellido"));
                                alumMap.put("dni", pers.getString("dni"));
                                alumMap.put("correo", pers.getString("correo"));
                                alumMap.put("telefono", pers.getString("telefono"));
                            } else {
                                alumMap.put("nombre", "Sin");
                                alumMap.put("apellido", "Nombre");
                                alumMap.put("dni", "-");
                                alumMap.put("correo", "-");
                                alumMap.put("telefono", "-");
                            }

                            // Carrera y Plan de estudio
                            Integer idPlan = alum.getInteger("id_plan");
                            if (idPlan != null) {
                                PlanEstudio plan = (PlanEstudio) PlanEstudio.findFirst("id_plan = ?", idPlan);
                                if (plan != null) {
                                    alumMap.put("plan_resolucion", plan.getString("resolucion"));
                                    Carrera carrera = (Carrera) Carrera.findFirst("id_carrera = ?", plan.get("id_carrera"));
                                    alumMap.put("carrera_nombre", carrera != null ? carrera.getString("nombre") : "Sin Carrera");
                                } else {
                                    alumMap.put("plan_resolucion", "-");
                                    alumMap.put("carrera_nombre", "Sin Carrera");
                                }
                            } else {
                                alumMap.put("plan_resolucion", "-");
                                alumMap.put("carrera_nombre", "Sin Carrera");
                            }
                        } else {
                            alumMap.put("nombre", "Sin");
                            alumMap.put("apellido", "Alumno");
                            alumMap.put("tipo_alumno", "AVANZADO");
                            alumMap.put("dni", "-");
                            alumMap.put("correo", "-");
                            alumMap.put("telefono", "-");
                            alumMap.put("plan_resolucion", "-");
                            alumMap.put("carrera_nombre", "Sin Carrera");
                        }

                        // Notas asociadas a esta inscripción
                        List<Model> notasList = Nota.find("id_inscripcion = ?", idInscripcion);
                        List<Map<String, Object>> notasMapList = new ArrayList<>();
                        double sumGrades = 0;
                        int gradesCount = 0;

                        for (Model n : notasList) {
                            Map<String, Object> notaMap = new HashMap<>();
                            int valor = n.getInteger("valor");
                            notaMap.put("valor", valor);
                            String tipo = n.getString("tipo_nota");
                            notaMap.put("tipo_nota", tipo);
                            
                            if ("PARCIAL".equalsIgnoreCase(tipo)) {
                                notaMap.put("tipo_css", "parcial");
                            } else if ("TP".equalsIgnoreCase(tipo)) {
                                notaMap.put("tipo_css", "tp");
                            } else {
                                notaMap.put("tipo_css", "final");
                            }
                            
                            notasMapList.add(notaMap);
                            sumGrades += valor;
                            gradesCount++;
                        }
                        alumMap.put("notas", notasMapList);

                        // Calcular promedio
                        double promedio = 0.0;
                        if (gradesCount > 0) {
                            promedio = sumGrades / gradesCount;
                        }
                        alumMap.put("promedio", String.format(java.util.Locale.US, "%.2f", promedio));
                        alumMap.put("promedio_raw", promedio);
                        alumMap.put("tiene_notas", gradesCount > 0);

                        // Determinar riesgo (Promedio < 5 o estado LIBRE)
                        boolean alRiesgo = "LIBRE".equals(estado) || (gradesCount > 0 && promedio < 5.0);
                        alumMap.put("al_riesgo", alRiesgo);

                        alumnosInscriptos.add(alumMap);
                    }
                }
            }
            model.put("alumnos_inscriptos", alumnosInscriptos);
            model.put("tiene_alumnos", !alumnosInscriptos.isEmpty());

            return new ModelAndView(model, "profesor_alumnos_list.mustache");
        }, new MustacheTemplateEngine());

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

                String tipoUsuario = ac.getString("tipo_usuario");

                req.session().attribute("tipoUsuario", tipoUsuario);

                if ("administrador".equals(tipoUsuario)) {
                    res.redirect("/dashboard-admin");
                } else if ("profesor".equals(tipoUsuario)) {
                    res.redirect("/dashboard-profesor");
                } else {
                    res.redirect("/dashboard-alumno");
                }

                halt();
                return null;

            } else {
                // Contraseña incorrecta.
                res.status(401); // Unauthorized.
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
            String tipoUsuario = req.queryParams("tipo_usuario");

            // --- Validaciones básicas ---
            if (name == null || name.isEmpty() || password == null || password.isEmpty()) {
                res.status(400); // Bad Request.
                return objectMapper.writeValueAsString(Map.of("error", "Nombre y contraseña son requeridos."));
            }

            try {
                // --- Creación y guardado del usuario usando el modelo ActiveJDBC ---
                User newUser = new User(); // Crea una nueva instancia de tu modelo User.
                String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
                newUser.set("name", name); // Asigna el nombre al campo 'name'.
                newUser.set("password", hashedPassword); // Asigna la contraseña hasheada.
                if (tipoUsuario != null && !tipoUsuario.isEmpty()) {
                    newUser.set("tipo_usuario", tipoUsuario);
                }
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

        spark.Spark.exception(Exception.class, (exception, req, res) -> {
            System.err.println("Excepcion capturada globalmente: " + exception.getClass().getName() + " - " + exception.getMessage());
            exception.printStackTrace();

            // Si es un error por caracteres mal formados en Query Params, redirigir al mismo path limpio
            String exMessage = exception.getMessage();
            if (exception.getClass().getName().contains("BadMessageException") || 
                (exMessage != null && (exMessage.contains("Unable to parse URI query") || exMessage.contains("Not valid UTF8")))) {
                res.redirect(req.pathInfo());
                halt();
                return;
            }

            res.status(500);
            res.type("text/html");
            Map<String, Object> model = new HashMap<>();
            model.put("errorCode", "500");
            model.put("errorMessage", "Error interno del servidor");
            try {
                res.body(new MustacheTemplateEngine().render(
                    new ModelAndView(model, "error.mustache")
                ));
            } catch (Exception ex) {
                res.body("Error interno del servidor.");
            }
        });

        spark.Spark.notFound((req, res) -> {
            res.type("text/html");
            Map<String, Object> model = new HashMap<>();
            model.put("errorCode", "404");
            model.put("errorMessage", "Página no encontrada");
            return new MustacheTemplateEngine().render(
                new ModelAndView(model, "error.mustache")
            );
        });

        spark.Spark.internalServerError((req, res) -> {
            res.type("text/html");
            Map<String, Object> model = new HashMap<>();
            model.put("errorCode", "500");
            model.put("errorMessage", "Error interno del servidor");
            return new MustacheTemplateEngine().render(
                new ModelAndView(model, "error.mustache")
            );
        });

    } // Fin del método main
} // Fin de la clase App
