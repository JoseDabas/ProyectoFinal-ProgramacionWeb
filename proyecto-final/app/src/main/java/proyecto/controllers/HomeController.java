package proyecto.controllers;

import io.javalin.Javalin;
import proyecto.clases.URL;
import proyecto.clases.Usuario;
import proyecto.services.URLServices;
import proyecto.services.UserServices;

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
    }
}
