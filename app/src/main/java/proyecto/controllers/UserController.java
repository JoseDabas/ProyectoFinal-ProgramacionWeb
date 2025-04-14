package proyecto.controllers;

import io.javalin.Javalin;
import io.jsonwebtoken.SignatureAlgorithm;
import org.bson.types.ObjectId;
import proyecto.clases.Usuario;
import proyecto.services.UserServices;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import javax.crypto.SecretKey;

import java.util.Date;
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
            ctx.render("/public/templates/register.html");
        });

        app.post("/user/register", ctx -> {
            String username = ctx.formParam("usuario");
            String password = ctx.formParam("password");

            Usuario existingUser = UserServices.getInstance().findByUsername(username);

            if (existingUser != null) {
                ctx.render("/public/templates/register.html", Map.of("error", "El nombre de usuario ya existe"));
            } else {
                Usuario newUser = new Usuario(new ObjectId(), username, password, false);
                UserServices.getInstance().crear(newUser);
                ctx.sessionAttribute("username", newUser);
                ctx.redirect("/");
            }
        });

        app.get("user/login", ctx -> {
            ctx.render("/public/templates/Login.html");
        });

        app.post("user/login", ctx -> {
            String username = ctx.formParam("usuario");
            String password = ctx.formParam("password");

            Usuario user = UserServices.getInstance().findByUsername(username);
            if (user != null) {
                if (user.getPassword().equals(password)) {
                    if (ctx.formParam("rememberMe") != null) {
                        ctx.cookie("rememberedUser", user.getUsername(), 600);
                    }
                    ctx.sessionAttribute("username", user);
                    ctx.redirect("/");
                } else {
                    ctx.render("/public/templates/Login.html", Map.of("error", "Usuario o contraseña incorrectos"));
                }
            } else {
                ctx.render("/public/templates/Login.html", Map.of("error", "Usuario no existe"));
            }
        });

        app.get("/user/crear", ctx -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null || !usuario.isAdmin()) {
                ctx.redirect("/");
                return;
            }
            ctx.render("/public/templates/crear-usuario.html");
        });

        app.post("/user/crear", ctx -> {
            Usuario adminUser = ctx.sessionAttribute("username");
            if (adminUser == null || !adminUser.isAdmin()) {
                ctx.redirect("/");
                return;
            }

            String username = ctx.formParam("usuario");
            String password = ctx.formParam("password");
            boolean isAdmin = "on".equals(ctx.formParam("admin"));

            Usuario existingUser = UserServices.getInstance().findByUsername(username);

            if (existingUser != null) {
                ctx.render("/public/templates/crear-usuario.html", Map.of("error", "El nombre de usuario ya existe"));
            } else {
                Usuario newUser = new Usuario(new ObjectId(), username, password, isAdmin);
                UserServices.getInstance().crear(newUser);
                ctx.redirect("/user/list");
            }
        });

        app.post("/user/tokenJWS/{usuario}", ctx -> {
            String username = ctx.pathParam("usuario");
            Usuario user = UserServices.getInstance().findByUsername(username);
            if (user != null) {
                System.out.println("Usuario encontrado    " + user.getUsername());
                SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Genera una clave segura

                // Define la duración de la sesión en milisegundos
                long sessionDuration = 1000 * 60 * 60; // 1 hora
                Date expiration = new Date(System.currentTimeMillis() + sessionDuration);

                // Crea el token con una reclamación de expiración
                String token = Jwts.builder()
                        .setSubject(user.getUsername())
                        .setExpiration(expiration)
                        .signWith(key)
                        .compact();
                ctx.sessionAttribute("username", token);
                System.out.println("Token generado: " + token);
                ctx.redirect("/");
                ctx.result(token);

            } else {
                ctx.status(404);
            }
        });

        app.before("/user/list", ctx -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null || !usuario.isAdmin()) {
                ctx.redirect("/");
            }
        });

        app.get("/user/list", ctx -> {
            String pageParam = ctx.queryParam("page");
            int page = (pageParam != null) ? Integer.parseInt(pageParam) : 1;
            List<Usuario> usuarios = UserServices.getInstance().findAll(page, 5);
            long totalUsers = UserServices.getInstance().find().count();
            int totalPages = (int) Math.ceil((double) totalUsers / 5);
            ctx.render("/public/templates/user-list.html",
                    Map.of("usuarios", usuarios, "totalPages", totalPages, "currentPage", page));
        });

        app.get("/user/close", ctx -> {
            ctx.removeCookie("rememberedUser");
            ctx.req().getSession().invalidate();
            ctx.redirect("/");
        });

        app.post("/user/admin/{username}", ctx -> {
            String username = ctx.pathParam("username");
            Usuario user = UserServices.getInstance().findByUsername(username);
            user.setAdmin(true);
            UserServices.getInstance().update(user);
            ctx.redirect("/user/list");
        });

        app.post("/user/borrar/{username}", ctx -> {
            String username = ctx.pathParam("username");
            if (username.equals("admin")) {
                ctx.redirect("/user/list");
                return;
            }
            UserServices.getInstance().deleteByUsername(username);
            ctx.redirect("/user/list");
        });
    }
}
