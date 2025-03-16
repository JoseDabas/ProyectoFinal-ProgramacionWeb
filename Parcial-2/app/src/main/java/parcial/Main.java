package parcial;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import parcial.Class.Usuario;
import parcial.Controllers.EncuestaController;
import parcial.Controllers.HomeController;
import parcial.Controllers.RegistroController;
import parcial.Controllers.UserController;
import parcial.Service.BootStrapServices;
import parcial.Service.UserServices;

public class Main {
    public static void main(String[] args) {

        BootStrapServices.getInstancia().init();
        if (UserServices.getInstancia().find("admin") == null) {
            UserServices.getInstancia().insert(new Usuario("admin", "admin", true));
        }

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "public";
                staticFileConfig.location = Location.CLASSPATH;
                staticFileConfig.precompress = false;
                staticFileConfig.aliasCheck = null;
            });
        }).start("0.0.0.0", 7000);

        new HomeController(app).aplicarRutas();
        new UserController(app).aplicarRutas();
        new EncuestaController(app).aplicarRutas();
        new RegistroController(app).aplicarRutas();
    }

}
