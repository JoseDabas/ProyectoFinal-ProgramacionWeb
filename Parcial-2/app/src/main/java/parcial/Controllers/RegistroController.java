package parcial.Controllers;

import io.javalin.Javalin;
import parcial.Class.Registro;
import parcial.Service.RegistroServices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RegistroController extends BaseController {
    public RegistroController(Javalin app) {
        super(app);
        registerTemplates();
    }

    @Override
    public void aplicarRutas() {
        app.get("/registro", ctx -> {
            List<Registro> registros = RegistroServices.getInstancia().findAll();
            Map<String, Object> model = new HashMap<>();
            model.put("registros", registros);
            ctx.render("public/templates/ListarRegistro.html", model);
        });
        app.get("/getRandomColor", ctx -> {
            // Genera un color aleatorio en formato hexadecimal
            String randomColor = getRandomColor();

            // Establece el tipo de contenido de la respuesta como JSON
            ctx.contentType("application/json");

            // Devuelve el color aleatorio como respuesta en formato JSON
            ctx.result("{\"color\": \"" + randomColor + "\"}");
        });

    }

    private static String getRandomColor() {
        Random random = new Random();
        // Genera tres valores aleatorios para los componentes rojo, verde y azul
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        // Convierte los valores a formato hexadecimal y los concatena
        return String.format("#%02x%02x%02x", r, g, b);
    }

}
