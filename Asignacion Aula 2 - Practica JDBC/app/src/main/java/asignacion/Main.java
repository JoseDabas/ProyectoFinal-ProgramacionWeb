package asignacion;

import io.javalin.rendering.template.JavalinThymeleaf;
import io.javalin.Javalin;
import asignacion.Controladores.CrudTradicionalControlador;
import asignacion.Services.BootStrapServices;

public class Main {
    public static void main(String[] args) {
        BootStrapServices.getInstancia().init();

        Javalin app = Javalin.create(config -> {
            config.fileRenderer(new JavalinThymeleaf()); // Configurar Thymeleaf
        }).start(8080);

        new CrudTradicionalControlador(app).aplicarRutas();
    }
}
