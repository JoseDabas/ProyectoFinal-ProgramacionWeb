package practica.Controladores;

import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;
import practica.Blog.Blog;
import practica.Encapsulaciones.Usuario;

import java.util.ArrayList;
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

        this.app.get("/user/register", (ctx) -> {
            Map<String, Object> model = new HashMap<String, Object>();
            model.put("titulo", "Registrar");
            ctx.render("/templates/Registrar.html", model);
        });

        this.app.post("/user/register", (ctx) -> {
            String user = ctx.formParam("usuario");
            String name = ctx.formParam("nombre");
            String pass = ctx.formParam("password");

            Usuario existingUser = Blog.getInstance().findUserByUsername(user);
            if (existingUser != null) {
                ctx.render("/templates/Registrar.html", Map.of("error", "El nombre de usuario ya existe"));
            } else {
                Usuario temp = new Usuario(user, name, pass, false, true);
                Blog.getInstance().agregarUsuario(temp);
                ctx.sessionAttribute("username", temp); // Guardamos el usuario en la sesión
                ctx.redirect("/");
            }
        });

        this.app.get("/user/login", (ctx) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Log in");
            ctx.render("/templates/IniciarSesion.html", model);
        });

        this.app.post("/user/login", (ctx) -> {
            String user = ctx.formParam("usuario");
            String pass = ctx.formParam("password");

            Usuario aux = Blog.getInstance().findUserByUsername(user);

            if (aux != null && aux.getPassword().equalsIgnoreCase(pass)) {
                ctx.sessionAttribute("username", aux); // Guardamos el usuario en la sesión
                ctx.redirect("/"); // Redirigir al inicio si el inicio de sesión es exitoso
            } else {
                // Si no se encuentra el usuario o las contraseñas no coinciden
                ctx.render("/templates/IniciarSesion.html", Map.of("error", "Usuario o contraseña incorrectos"));
            }
        });

        this.app.before("/user/list", (ctx) -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null || !usuario.isAdministrator()) {
                ctx.redirect("/");
            }
        });

        this.app.get("/user/list", (ctx) -> {
            ArrayList<Usuario> usuarios = Blog.getInstance().getUsuarios();
            ctx.render("/templates/ListaUsuarios.html", Map.of("usuarios", usuarios));
        });

        this.app.before("/user/crear", (ctx) -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null || !usuario.isAdministrator()) {
                ctx.redirect("/");
            }
        });

        this.app.get("/user/crear", (ctx) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Crear");
            ctx.render("/templates/CrearUsuario.html", model);
        });

        this.app.post("/user/crear", ctx -> {
            String user = ctx.formParam("usuario");
            String name = ctx.formParam("nombre");
            String pass = ctx.formParam("password");
            boolean autor = ctx.formParam("Autor") != null;
            boolean admin = ctx.formParam("Admin") != null;

            Usuario existingUser = Blog.getInstance().findUserByUsername(user);
            if (existingUser != null) {
                ctx.render("/templates/Registrar.html", Map.of("error", "El nombre de usuario ya existe"));
            } else {
                Usuario temp = new Usuario(user, name, pass, admin, autor);
                Blog.getInstance().agregarUsuario(temp);
                ctx.redirect("/user/list");
            }
        });

        this.app.get("/user/close", (ctx) -> {
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Cerrar Sesion");
            ctx.render("/templates/CerrarSesion.html", model);
        });

        this.app.post("/user/close", (ctx) -> {
            ctx.req().getSession().invalidate();
            ctx.redirect("/");
        });
    }
}
