package parcial.Controllers;

import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;
import parcial.Class.Usuario;
import parcial.Service.UserServices;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserController extends BaseController {
    public UserController(Javalin app) {
        super(app);
        registerTemplates();
    }

    @Override
    public void aplicarRutas() {
        app.get("/user/register", ctx -> {
            Map<String, Object> model = new HashMap<>();

            model.put("titulo", "Registrar");
            ctx.render("/public/templates/Registrar.html", model);
        });

        app.post("/user/register", ctx -> {
            String user = ctx.formParam("usuario");
            String name = ctx.formParam("nombre");
            String pass = ctx.formParam("password");

            Usuario existingUser = UserServices.getInstancia().findUserByUsername(user);
            if (existingUser != null) {
                ctx.render("/templates/Registrar.html", Map.of("error", "El nombre de usuario ya existe"));
            } else {
                Usuario temp = new Usuario(user, pass, false);
                UserServices.getInstancia().insert(temp);
                ctx.sessionAttribute("username", temp);
                ctx.redirect("/");
            }
        });
        app.get("/user/crear", ctx -> {
            Map<String, Object> model = new HashMap<>();

            model.put("titulo", "Crear");
            ctx.render("/public/templates/CrearUsuario.html", model);
        });

        app.post("/user/crear", ctx -> {
            String usuario = ctx.formParam("usuario");
            String password = ctx.formParam("password");
            boolean admin = ctx.formParam("Admin") != null;

            Usuario existingUser = UserServices.getInstancia().findUserByUsername(usuario);
            if (existingUser != null) {
                ctx.render("/templates/Registrar.html", Map.of("error", "El nombre de usuario ya existe"));
            } else {
                Usuario temp = new Usuario(usuario, password, admin);
                UserServices.getInstancia().insert(temp);
                ctx.redirect("/user/list");
            }
        });

        // Ruta GET para la página de inicio de sesión
        app.get("/user/login", ctx -> {
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Log in");

            // Verificar si el usuario tiene una cookie de "recordar usuario"
            String rememberedUser = ctx.cookie("rememberedUser");
            if (rememberedUser != null) {
                // Si la cookie existe, intentar iniciar sesión automáticamente
                Usuario aux = UserServices.getInstancia().find(rememberedUser);
                if (aux != null) {
                    // El usuario existe, establecer la sesión y redirigir
                    ctx.sessionAttribute("username", aux);
                    ctx.redirect("/");
                    return;
                }
            }

            // Si no hay cookie o el usuario es inválido, mostrar la página de inicio de
            // sesión
            ctx.render("/public/templates/Login.html", model);
        });

        app.post("/user/login", ctx -> {
            String user = ctx.formParam("usuario");
            String pass = ctx.formParam("password");

            Usuario aux = UserServices.getInstancia().find(user);
            if (aux != null) {
                if (aux.getPassword().equalsIgnoreCase(pass)) {
                    // Usuario autenticado correctamente

                    // Manejar la casilla de verificación "recordarme"
                    if (ctx.formParam("rememberMe") != null) {
                        // Establecer una cookie que dura 30 dias (2592000 segundos)

                        ctx.cookie("rememberedUser", aux.getUsername(), 2592000);
                    }

                    // Establecer atributo de sesión y redirigir
                    ctx.sessionAttribute("username", aux);
                    ctx.redirect("/");
                } else {
                    // Contraseña incorrecta
                    ctx.render("/public/templates/Login.html", Map.of("error", "Usuario o contraseña incorrectos"));
                }
            } else {
                // El usuario no existe
                ctx.render("/public/templates/Login.html", Map.of("error", "Usuario no existe"));
            }
        });

        app.before("/user/list", ctx -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null || !usuario.isAdministrator()) {
                ctx.redirect("/");
            }
        });

        app.get("/user/list", ctx -> {
            // Obtener parámetro de página y convertirlo a entero
            String pageParam = ctx.queryParam("page");
            int page = (pageParam != null && !pageParam.isEmpty()) ? Integer.parseInt(pageParam) : 1;

            // Obtener datos paginados
            List<Usuario> usuarios = UserServices.getInstancia().findAll(page, 5);
            int totalUsers = UserServices.getInstancia().findAll().size();
            int totalPages = (int) Math.ceil((double) totalUsers / 5);

            // Crear lista de números de página para la plantilla
            List<Integer> pageNumbers = new ArrayList<>();
            for (int i = 1; i <= totalPages; i++) {
                pageNumbers.add(i);
            }

            // Renderizar la plantilla con todos los datos necesarios
            ctx.render("/public/templates/ListaUsuario.html",
                    Map.of(
                            "usuarios", usuarios,
                            "totalPages", totalPages,
                            "currentPage", page,
                            "pageNumbers", pageNumbers));
        });

        app.get("/user/close", ctx -> {
            ctx.removeCookie("rememberedUser");
            ctx.req().getSession().invalidate();
            ctx.redirect("/");
        });

        app.get("/user/editar/{username}", ctx -> {
            Usuario user = UserServices.getInstancia().find(ctx.pathParam("username"));
            if (user == null) {
                ctx.redirect("/");
            }
            Map<String, Object> model = new HashMap<>();
            model.put("usuario", user);
            ctx.render("/public/templates/EditarUsuario.html", model);
        });

        app.post("user/editar/{username}", ctx -> {
            Usuario user = UserServices.getInstancia().find(ctx.pathParam("username"));
            if (user == null) {
                ctx.redirect("/");
            }
            String usuario = ctx.formParam("usuario");
            String password = ctx.formParam("password");
            boolean admin = ctx.formParam("Admin") != null;

            user.setUsername(usuario);
            user.setPassword(password);
            /*
             * if(admin){
             * user.setAdministrator(true);
             * }
             * else{
             * user.setAdministrator(false);
             * }
             */
            user.setAdministrator(admin);

            UserServices.getInstancia().update(user);
            ctx.redirect("/user/list");
        });

        app.post("/user/borrar/{username}", ctx -> {
            Usuario user = UserServices.getInstancia().find(ctx.pathParam("username"));
            if (user == null) {
                ctx.redirect("/");
            }
            UserServices.getInstancia().delete(user);
            ctx.redirect("/user/list");
        });
    }
}
