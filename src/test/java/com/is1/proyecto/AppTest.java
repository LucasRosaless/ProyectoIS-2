package com.is1.proyecto;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import spark.Spark;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {

    private static HttpClient client;

    @BeforeAll
    public static void setUpClass() {
        // Iniciar la aplicación Spark
        App.main(new String[]{});
        Spark.awaitInitialization(); // Esperar a que el servidor embebido inicie completamente

        // Configurar el HttpClient con un CookieManager para mantener la sesión (JSESSIONID)
        CookieManager cookieManager = new CookieManager();
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

        client = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .build();
    }

    @AfterAll
    public static void tearDownClass() {
        // Detener el servidor Spark al finalizar todos los tests
        Spark.stop();
        Spark.awaitStop();
    }

    @Test
    void testHomepageLoads() throws Exception {
        // Arrange
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/"))
                .GET()
                .build();

        // Act
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Assert
        assertEquals(200, response.statusCode(), "La página de inicio debería cargar con un código 200 OK");
        assertTrue(response.body().contains("Iniciar Sesión"), "La página debería contener el formulario de login");
    }

    @Test
    void testUserRegistrationAndLoginFlow() throws Exception {
        // 1. Registrar un nuevo usuario (POST /user/new)
        String uniqueUser = "testweb_" + System.currentTimeMillis();
        String formData = "name=" + URLEncoder.encode(uniqueUser, StandardCharsets.UTF_8) +
                          "&password=" + URLEncoder.encode("password123", StandardCharsets.UTF_8) +
                          "&tipo_usuario=" + URLEncoder.encode("alumno", StandardCharsets.UTF_8);

        HttpRequest registerRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/user/new"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formData))
                .build();

        HttpResponse<String> registerResponse = client.send(registerRequest, HttpResponse.BodyHandlers.ofString());
        
        // Verifica si redirecciona (302) o devuelve 201
        // Como App.java usa res.status(201) y luego redirect, el código principal que devuelve podría ser atrapado por redirect.
        // Pero el HttpClient por defecto NO sigue redirecciones automáticamente a menos que se lo digamos.
        assertTrue(registerResponse.statusCode() == 302 || registerResponse.statusCode() == 201, 
            "El registro debe devolver redirección o éxito");

        // 2. Iniciar sesión con el nuevo usuario (POST /login)
        String loginData = "username=" + URLEncoder.encode(uniqueUser, StandardCharsets.UTF_8) +
                           "&password=" + URLEncoder.encode("password123", StandardCharsets.UTF_8);

        HttpRequest loginRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/login"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(loginData))
                .build();

        HttpResponse<String> loginResponse = client.send(loginRequest, HttpResponse.BodyHandlers.ofString());

        // El login exitoso debería retornar un 302 hacia /dashboard-alumno (ya que registramos tipo alumno)
        assertEquals(302, loginResponse.statusCode(), "El login debe redirigir al dashboard");
        String locationHeader = loginResponse.headers().firstValue("Location").orElse("");
        assertTrue(locationHeader.endsWith("/dashboard-alumno"), "Debería redirigir al dashboard del alumno");

        // 3. Acceder al Dashboard protegido para verificar la sesión
        HttpRequest dashboardRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/dashboard-alumno"))
                .GET()
                .build();

        HttpResponse<String> dashboardResponse = client.send(dashboardRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, dashboardResponse.statusCode(), "El acceso al dashboard debería ser 200 OK porque hay sesión activa");
        assertTrue(dashboardResponse.body().contains(uniqueUser), "El dashboard debería mostrar el nombre del usuario");

        // 4. Cerrar sesión (GET /logout)
        HttpRequest logoutRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/logout"))
                .GET()
                .build();

        HttpResponse<String> logoutResponse = client.send(logoutRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, logoutResponse.statusCode(), "El logout debe redirigir al inicio");

        // 5. Verificar que ya no se puede acceder al Dashboard (Debe redirigir al login)
        HttpResponse<String> dashboardAfterLogout = client.send(dashboardRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(302, dashboardAfterLogout.statusCode(), "Sin sesión, debe redirigir fuera del dashboard");
        assertTrue(dashboardAfterLogout.headers().firstValue("Location").orElse("").contains("/?error="), 
            "Debe redirigir a la raíz con un error de sesión");
    }

    @Test
    void testProtectedRoutesWithoutSession() throws Exception {
        // Usar un cliente sin cookies para simular alguien no logueado
        HttpClient anonClient = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:4567/profesores"))
                .GET()
                .build();

        HttpResponse<String> response = anonClient.send(request, HttpResponse.BodyHandlers.ofString());

        // Como /profesores está protegida por un before filter, debe redirigir al home (302)
        assertEquals(302, response.statusCode(), "Las rutas ABM protegidas deben redirigir si no hay sesión");
        assertTrue(response.headers().firstValue("Location").orElse("").endsWith("/"));
    }
}
