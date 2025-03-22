package asignacion;

import asignacion.Controladores.ApiControlador;
import asignacion.Controladores.CrudTradicionalControlador;
import asignacion.Services.MongoDbConexion;
import io.github.cdimascio.dotenv.Dotenv;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;

public class Main {

    private static Dotenv dotenv;

    public static void main(String[] args) {
        System.out.println("CRUD Javalin MongoDB");

        // Cargar configuración desde archivo .env
        cargarConfiguracion();

        // Probar conexión a MongoDB
        probarConexionMongoDB();

        // Creando la instancia del servidor y configurando.
        Javalin app = Javalin.create(config -> {
            // configurando los documentos estaticos.
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "/publico";
                staticFileConfig.location = Location.CLASSPATH;
                staticFileConfig.precompress = false;
                staticFileConfig.aliasCheck = null;
            });

            // Habilitando el CORS. Ver: https://javalin.io/plugins/cors#getting-started
            // para más opciones.
            config.plugins.enableCors(corsContainer -> {
                corsContainer.add(corsPluginConfig -> {
                    corsPluginConfig.anyHost();
                });
            });

            // habilitando el plugins de las rutas definidas.
            config.plugins.enableRouteOverview("/rutas");

        });

        app.get("/", context -> {
            context.result("Proyecto CRUD MongoDB");
        });

        // Iniciando la aplicación
        int puerto = getPuertoDinamico();
        app.start(puerto);
        System.out.println("Servidor iniciado en el puerto: " + puerto);

        // incluyendo los controladores.
        new ApiControlador(app).aplicarRutas();
        new CrudTradicionalControlador(app).aplicarRutas();
    }

    /**
     * Carga las variables de entorno desde el archivo .env
     */
    private static void cargarConfiguracion() {
        try {
            dotenv = Dotenv.configure()
                    .ignoreIfMissing() // No lanzar error si no existe el archivo .env
                    .load();
            System.out.println("Archivo .env cargado correctamente");
        } catch (Exception e) {
            System.err.println("Error al cargar el archivo .env: " + e.getMessage());
            System.out.println("Usando variables de entorno del sistema o valores predeterminados");
        }
    }

    private static void probarConexionMongoDB() {
        try {
            MongoDbConexion conexion = MongoDbConexion.getInstance();
            boolean conectado = conexion.probarConexion();

            if (conectado) {
                System.out.println("Conexión exitosa a MongoDB");
            } else {
                System.err.println("No se pudo conectar a MongoDB. La aplicación puede tener problemas.");
            }
        } catch (Exception e) {
            System.err.println("Error al conectar con MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Obtiene el puerto dinámico para la aplicación
     * Prioridad: 1. Variable PORT del sistema, 2. Variable PORT del .env, 3. Puerto
     * por defecto (7000)
     * 
     * @return Puerto para iniciar el servidor
     */
    static int getPuertoDinamico() {
        // Verificar primero en variables de sistema (para compatibilidad con Heroku)
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }

        // Verificar en archivo .env
        if (dotenv != null && dotenv.get("PORT") != null) {
            try {
                return Integer.parseInt(dotenv.get("PORT"));
            } catch (NumberFormatException e) {
                System.err.println("Error al convertir PORT desde .env: " + e.getMessage());
            }
        }

        // Puerto por defecto
        return 7000;
    }
}