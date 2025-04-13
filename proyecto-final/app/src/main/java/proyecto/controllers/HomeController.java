package proyecto.controllers;

import io.javalin.Javalin;
import proyecto.clases.URL;
import proyecto.clases.Usuario;
import proyecto.services.URLServices;
import proyecto.services.UserServices;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class HomeController extends BaseController {
    public HomeController(Javalin app) {
        super(app);
        registerTemplates();
    }

    @Override
    public void aplicarRutas() {
        app.before("/", ctx -> {
            if (ctx.cookie("rememberedUser") != null) {
                Usuario user = UserServices.getInstance().findByUsername(ctx.cookie("rememberedUser"));
                ctx.sessionAttribute("username", user);
            }
        });

        app.get("/", ctx -> {
            List<URL> urls = URLServices.getInstance().find().stream().toList();
            Map<String, Object> model = Map.of("urls", urls);
            ctx.render("public/templates/index.html", model);
        });

        app.get("/service-worker.js", ctx -> {
            ctx.contentType("application/javascript");
            // Lee el archivo service-worker.js de recursos y envíalo como respuesta
            InputStream inputStream = getClass().getResourceAsStream("/public/service-worker.js");
            if (inputStream != null) {
                ctx.result(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
            } else {
                ctx.status(404);
            }
        });

        app.get("/manifest.json", ctx -> {
            ctx.contentType("application/json");
            InputStream inputStream = getClass().getResourceAsStream("/public/manifest.json");
            if (inputStream != null) {
                ctx.result(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
            } else {
                ctx.status(404);
            }
        });
    }
}
