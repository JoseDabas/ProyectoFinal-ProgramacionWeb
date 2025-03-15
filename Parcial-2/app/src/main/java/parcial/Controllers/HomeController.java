package parcial.Controllers;

import io.javalin.Javalin;
//import io.javalin.rendering.JavalinRenderer;
//import io.javalin.rendering.template.JavalinThymeleaf;

import java.util.HashMap;
import java.util.Map;

public class HomeController extends BaseController {

    public HomeController(Javalin app) {
        super(app);
        registerTemplates();
    }

    @Override
    public void aplicarRutas() {
        app.get("/", ctx -> {
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Inicio");
            ctx.render("public/templates/index.html", model);
        });
    }

}
