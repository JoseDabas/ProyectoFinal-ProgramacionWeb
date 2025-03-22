package practica.Controladores;

import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;
//import practica.Blog.Blog;

import practica.Entidades.*;
import practica.Servicios.*;

import java.io.IOException;
import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
//import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class UsuarioControlador extends BaseControlador {
    public UsuarioControlador(Javalin app) {
        super(app);
        registerTemplates();
    }

    public void registerTemplates() {
        JavalinRenderer.register(new JavalinThymeleaf(), ".html");
    }

    @Override
    public void aplicarRutas() {

        this.app.before("/", AuthControlador::checkRememberMe); // Autenticación con cookies

        this.app.get("/user/register", (ctx) -> {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("titulo", "Registrar");
            ctx.render("/templates/Registrar.html", model);
        });

        this.app.post("/user/register", (ctx) -> {
            String user = ctx.formParam("usuario");
            String name = ctx.formParam("nombre");
            String pass = ctx.formParam("password");

            Usuario existingUser = UsuarioServices.getInstance().find(user);
            if (existingUser != null) {
                ctx.render("/templates/Registrar.html", Map.of("error", "El nombre de usuario ya existe"));
            } else {
                Usuario temp = new Usuario(user, name, pass, false, true);
                UsuarioServices.getInstance().create(temp);
                ctx.sessionAttribute("username", temp); // Guardamos el usuario en la sesión
                ctx.redirect("/");
            }
        });

        this.app.get("/user/login", (ctx) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Log in");
            ctx.render("/templates/IniciarSesion.html", model);
        });

        this.app.post("/user/login", ctx -> {
            String user = ctx.formParam("usuario");
            String pass = ctx.formParam("password");

            Usuario aux = UsuarioServices.getInstance().find(user);
            if (aux != null) {
                if (aux.getPassword().equalsIgnoreCase(pass)) {
                    if (ctx.formParam("rememberMe") != null) {
                        ctx.cookie("rememberedUser", aux.getUsername(), 600);
                    }
                    ctx.sessionAttribute("username", aux);
                    try {
                        Class.forName("org.postgresql.Driver");
                        String dbUrl = System.getenv("JDBC_DATABASE_URL");
                        Connection connection = DriverManager.getConnection(dbUrl);
                        String sql = "INSERT INTO log_login (username, login_time) VALUES (?, ?)";
                        PreparedStatement statement = connection.prepareStatement(sql);
                        statement.setString(1, aux.getUsername());
                        statement.setObject(2, LocalDateTime.now());
                        statement.executeUpdate();
                    } catch (ClassNotFoundException | SQLException e) {
                        e.printStackTrace();
                    }
                    ctx.redirect("/");
                }

                else {
                    ctx.render("/templates/IniciarSesion.html", Map.of("error", "Usuario o contraseña incorrectos"));
                }
            } else {
                ctx.render("/templates/IniciarSesion.html", Map.of("error", "Usuario no existe"));
            }
        });

        this.app.before("/user/list", (ctx) -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null || !usuario.isAdministrator()) {
                ctx.redirect("/");
            }
        });

        this.app.get("/user/list/{page}", ctx -> {
            int page = Integer.parseInt(ctx.pathParam("page"));
            List<Usuario> usuarios = UsuarioServices.getInstance().findAll(page, 5);
            int totalUsers = UsuarioServices.getInstance().findAll().size();
            int totalPages = (int) Math.ceil((double) totalUsers / 5);
            ctx.render("/templates/ListaUsuarios.html",
                    Map.of("usuarios", usuarios, "totalPages", totalPages, "currentPage", page));
        });

        this.app.before("/user/crear", (ctx) -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null || !usuario.isAdministrator()) {
                ctx.redirect("/");
            }
        });

        this.app.get("/user/crear", ctx -> {
            Map<String, Object> model = new HashMap<>();

            model.put("titulo", "Crear");
            ctx.render("/templates/CrearUsuario.html", model);
        });

        this.app.post("/user/crear", ctx -> {
            System.out.println("Formulario recibido");
            String user = ctx.formParam("usuario");
            String name = ctx.formParam("nombre");
            String pass = ctx.formParam("password");
            boolean autor = ctx.formParam("Autor") != null;
            boolean admin = ctx.formParam("Admin") != null;

            Usuario existingUser = UsuarioServices.getInstance().find(user);
            if (existingUser != null) {
                ctx.render("/templates/CrearUsuario.html", Map.of("error", "El nombre de usuario ya existe"));
            } else {
                Foto foto = null;
                // Verificamos si se ha subido un archivo de foto
                if (ctx.uploadedFile("foto") != null) {
                    try {
                        // Leemos el archivo subido
                        byte[] bytes = ctx.uploadedFile("foto").content().readAllBytes();
                        // Convertimos el archivo a Base64
                        String encodedString = Base64.getEncoder().encodeToString(bytes);
                        // Obtenemos el tipo MIME del archivo
                        String mimeType = ctx.uploadedFile("foto").contentType();
                        // Creamos el objeto Foto
                        foto = new Foto(ctx.uploadedFile("foto").filename(), mimeType, encodedString);
                        // Guardamos la foto en la base de datos
                        FotoServices.getInstance().create(foto);
                        System.out.println("Foto guardada: " + foto.getNombre());
                    } catch (IOException e) {
                        e.printStackTrace();
                        ctx.render("/templates/CrearUsuario.html", Map.of("error", "Error al procesar la foto"));
                        return; // Terminamos la ejecución si hay un error
                    }
                } else {
                    System.out.println("No se recibio ninguna foto.");
                }

                // Creamos el usuario y asociamos la foto si existe
                Usuario temp = new Usuario(user, name, pass, admin, autor);
                temp.setFoto(foto); // Asociamos la foto al usuario

                // Guardamos el usuario en la base de datos
                UsuarioServices.getInstance().create(temp);

                // Redirigimos a la lista de usuarios
                ctx.redirect("/user/list/1");
            }
        });

        this.app.get("/user/close", (ctx) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Cerrar Sesion");
            ctx.render("/templates/CerrarSesion.html", model);
        });

        this.app.post("/user/close", (ctx) -> {
            ctx.removeCookie("rememberedUser");
            ctx.req().getSession().invalidate();
            ctx.redirect("/");
        });

    }
}
