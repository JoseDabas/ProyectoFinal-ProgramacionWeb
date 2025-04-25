package proyecto;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import java.io.IOException;

import org.bson.types.ObjectId;
import proyecto.clases.Usuario;
import proyecto.controllers.HomeController;
import proyecto.controllers.URLController;
import proyecto.controllers.UserController;
import proyecto.grpc.UrlServiceImpl;
import proyecto.services.UserServices;

//import io.github.cdimascio.dotenv.Dotenv;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        // Cargar variables de entorno desde el archivo .env

        // Establecer las propiedades del sistema para MongoDB
        System.setProperty("URL_MONGO",
                "mongodb+srv://josearieldabas01:HL4OcEYAGqynX5Jj@josedatabase.7dkjm.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0");
        System.setProperty("DB_NOMBRE", "proyecto_final");

        System.setProperty("https.protocols", "TLSv1.2");

        // Mostrar las variables para depuración
        System.out.println("URL MongoDB: " + System.getProperty("URL_MONGO"));
        System.out.println("Base de datos: " + System.getProperty("DB_NOMBRE"));

        if (UserServices.getInstance().findByUsername("admin") == null) {
            UserServices.getInstance().crear(new Usuario(new ObjectId(), "admin", "admin", true));
        }
        new Thread(() -> {
            try {
                Server server = ServerBuilder.forPort(50051)
                        .addService(new UrlServiceImpl())
                        .build();
                server.start();
                server.awaitTermination();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        Javalin app = Javalin.create(config -> {

            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "public/templates";
                staticFileConfig.location = Location.CLASSPATH;
                staticFileConfig.precompress = false;
                staticFileConfig.aliasCheck = null;
            });

            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "public";
                staticFileConfig.location = Location.CLASSPATH;
                staticFileConfig.precompress = false;
                staticFileConfig.aliasCheck = null;
            });

        }).start(7000);

        new HomeController(app).aplicarRutas();
        new UserController(app).aplicarRutas();
        new URLController(app).aplicarRutas();
    }

}
