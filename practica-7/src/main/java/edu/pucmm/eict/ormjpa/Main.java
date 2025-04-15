package edu.pucmm.eict.ormjpa;

// --- Tus imports existentes ---
import com.fasterxml.jackson.databind.node.TextNode;
import edu.pucmm.eict.ormjpa.controladores.EstudianteControlador;
import edu.pucmm.eict.ormjpa.controladores.EstudianteGrpcControlador; // Controlador puente
import edu.pucmm.eict.ormjpa.controladores.FotoControlador;
import edu.pucmm.eict.ormjpa.controladores.ProfesorControlador;
import edu.pucmm.eict.ormjpa.modelos.Estudiante;
import edu.pucmm.eict.ormjpa.modelos.Profesor;
import edu.pucmm.eict.ormjpa.servicios.BootStrapServices;
import edu.pucmm.eict.ormjpa.servicios.EstudianteServices;
import edu.pucmm.eict.ormjpa.servicios.ProfesorServices;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.openapi.plugin.OpenApiPlugin;
import io.javalin.openapi.plugin.redoc.ReDocPlugin;
import io.javalin.openapi.plugin.swagger.SwaggerPlugin;
import io.javalin.rendering.template.JavalinThymeleaf;
import io.javalin.security.RouteRole;

// --- Imports necesarios para gRPC ---
import edu.pucmm.eict.ormjpa.grpc.EstudianteServiceImpl; // Importa tu implementación
import edu.pucmm.eict.ormjpa.grpc.TempClasses.EstudianteServiceGrpc.EstudianteServiceImplBase;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.BindableService; // Añadimos esta importación
import java.io.IOException; // Necesario para startGrpcServer
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit; // Necesario para stopGrpcServer

import static io.javalin.apibuilder.ApiBuilder.*;

public class Main {

    // indica el modo de operacion para la base de datos.
    private static String modoConexion = "";
    private static Server grpcServer; // Referencia al servidor gRPC
    private static final int GRPC_PORT = 9090; // Puerto para gRPC

    enum Rules implements RouteRole {
        ANONYMOUS,
        USER,
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        String mensaje = "Software ORM - JPA y Servidor gRPC"; // Mensaje actualizado
        System.out.println(mensaje);
        if (args.length >= 1) {
            modoConexion = args[0];
            System.out.println("Modo de Operacion: " + modoConexion);
        }

        // Iniciando la base de datos.
        if (modoConexion.isEmpty()) {
            BootStrapServices.getInstancia().init();
        }

        // creando los objetos por defecto.
        try {
            for (int i = 0; i < 10; i++) { // Reducido a 10 para el ejemplo
                if (EstudianteServices.getInstancia().find(i) == null) {
                    EstudianteServices.getInstancia().crear(new Estudiante(i, "nombre " + i));
                }
                if (ProfesorServices.getInstancia().find(i) == null) { // Asumiendo ID entero para profesor
                    ProfesorServices.getInstancia().crear(new Profesor("Profesor " + i));
                }
            }
        } catch (jakarta.persistence.EntityExistsException e) {
            System.out.println("Advertencia: Algunos datos de ejemplo ya existían. " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error creando datos de ejemplo: " + e.getMessage());
        }

        // Iniciar servidor gRPC y registrar hook de apagado
        try {
            startGrpcServer();
            System.out.println("Servidor gRPC iniciado en el puerto: " + GRPC_PORT);

            // Agregar hook de apagado para gRPC
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.err.println("*** Apagando servidor gRPC debido a cierre de JVM ***");
                try {
                    stopGrpcServer();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                    Thread.currentThread().interrupt();
                }
                System.err.println("*** Servidor gRPC apagado ***");
            }));
        } catch (IOException e) {
            System.err.println("!!!!!!!!!! FALLO AL INICIAR SERVIDOR GRPC !!!!!!!!!");
            e.printStackTrace();
        }

        // Creando la instancia del servidor Javalin
        Javalin app = Javalin.create(config -> {
            // Configuración para archivos estáticos
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.hostedPath = "/";
                staticFileConfig.directory = "/publico";
                staticFileConfig.location = Location.CLASSPATH;
            });

            // Configuración del renderizador de plantillas
            config.fileRenderer(new JavalinThymeleaf());

            // Configuración de rutas API
            config.router.apiBuilder(() -> {
                // API REST original
                path("/api", () -> {
                    path("/estudiante", () -> {
                        get(EstudianteControlador::listarEstudiantes);
                        post(EstudianteControlador::crearEstudiante);
                        put(EstudianteControlador::actualizarEstudiante);
                        path("/{matricula}", () -> {
                            get(EstudianteControlador::estudiantePorMatricula);
                            delete(EstudianteControlador::eliminarEstudiante);
                        });
                    });
                    path("/profesor", () -> {
                        get(ProfesorControlador::listarProfesores);
                        post(ProfesorControlador::crearProfesor);
                        put(ProfesorControlador::actualizarProfesor);
                        path("/{id}", () -> {
                            get(ProfesorControlador::profesorPorId);
                            delete(ProfesorControlador::eliminarProfesor);
                        });
                    });
                });

                // Nuevo puente REST-gRPC
                path("/grpc-bridge", () -> {
                    path("/estudiante", () -> {
                        get(EstudianteGrpcControlador::listarEstudiantes);
                        post(EstudianteGrpcControlador::crearEstudiante);
                        path("/{matricula}", () -> {
                            get(EstudianteGrpcControlador::consultarEstudiante);
                            delete(EstudianteGrpcControlador::eliminarEstudiante);
                        });
                    });
                });

                // Rutas para manejo de fotos
                path("/fotos", () -> {
                    get(ctx -> {
                        ctx.redirect("/fotos/listar");
                    });
                    get("/listar", FotoControlador::listarFotos);
                    post("/procesarFoto", FotoControlador::procesarFotos);
                    get("/visualizar/{id}", FotoControlador::visualizarFotos);
                    get("/eliminar/{id}", FotoControlador::eliminarFotos);
                });
            });

            // Configuración OpenAPI
            config.registerPlugin(new OpenApiPlugin(openApiConf -> {
                openApiConf
                        .withRoles(Rules.ANONYMOUS)
                        .withDefinitionConfiguration((version, openApiDefinition) -> {
                            openApiDefinition
                                    .withInfo(openApiInfo -> openApiInfo
                                            .description("API para gestión de estudiantes y profesores")
                                            .version("1.0.0")
                                            .title("API ORM-JPA con soporte gRPC"));
                        });
            }));

            // Configuración Swagger y ReDoc
            config.registerPlugin(new SwaggerPlugin(swaggerConfiguration -> {
            }));
            config.registerPlugin(new ReDocPlugin(reDocConfiguration -> {
            }));

        }).start(getHerokuAssignedPort()); // Inicia Javalin en su puerto

        // Configurar CORS después de iniciar Javalin
        app.before(ctx -> {
            ctx.header("Access-Control-Allow-Origin", "*");
            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");

            if (ctx.method().equals("OPTIONS")) {
                ctx.status(200).result("");
            }
        });

        // Endpoint de inicio Javalin
        app.get("/", ctx -> {
            ctx.redirect("/grpc-client");
        });

        // Endpoint para servir el cliente gRPC HTML desde templates
        app.get("/grpc-client", ctx -> {
            Map<String, Object> model = new HashMap<>();
            model.put("baseUrl", ctx.url().split("/grpc-client")[0]);
            model.put("titulo", "Cliente gRPC - Gestión de Estudiantes");
            ctx.render("/templates/ClienteGRPC.html", model);
        });

        // Alias para el cliente
        app.get("/cliente", ctx -> {
            ctx.redirect("/grpc-client");
        });

        app.get("/grpc", ctx -> {
            ctx.redirect("/grpc-client");
        });

        // Endpoint de prueba Javalin
        app.get("/prueba", ctx -> {
            EstudianteServices.getInstancia().pruebaActualizacion();
            ctx.result("Bien!...");
        });

        // Manejo de excepciones Javalin
        app.exception(Exception.class, (exception, ctx) -> {
            ctx.status(500);
            ctx.html("<h1>Error no recuperado:" + exception.getMessage() + "</h1>");
            exception.printStackTrace();
        });

        System.out.println("Servidor Javalin iniciado en el puerto: " + app.port());
        System.out.println("Accede al cliente gRPC en: http://localhost:" + app.port() + "/grpc-client");
    }

    /**
     * Inicia el servidor gRPC en el puerto definido.
     * 
     * @throws IOException Si el puerto ya está en uso.
     */
    private static void startGrpcServer() throws IOException {
        // En vez de usar directamente EstudianteServiceImpl, comentamos esto por ahora
        // para poder compilar y ejecutar la aplicación sin el servidor gRPC
        grpcServer = ServerBuilder.forPort(GRPC_PORT)
                // .addService(new EstudianteServiceImpl()) // Será implementado correctamente
                // después
                .build()
                .start();
    }

    /**
     * Detiene el servidor gRPC de forma ordenada.
     * 
     * @throws InterruptedException Si el hilo es interrumpido mientras espera.
     */
    private static void stopGrpcServer() throws InterruptedException {
        if (grpcServer != null) {
            // Inicia el apagado, no acepta nuevas llamadas pero procesa las existentes
            grpcServer.shutdown();
            // Espera hasta 30 segundos para que las llamadas terminen, luego fuerza el
            // cierre
            if (!grpcServer.awaitTermination(30, TimeUnit.SECONDS)) {
                System.err.println("Servidor gRPC no terminó en 30 segundos, forzando apagado...");
                grpcServer.shutdownNow(); // Fuerza el apagado inmediato
            }
        }
    }

    /**
     * Metodo para indicar el puerto en Heroku
     * 
     * @return puerto
     */
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 7000; // Retorna el puerto por defecto en caso de no estar en Heroku.
    }

    /**
     * Nos indica el modo de conexión.
     * 
     * @return modo de conexión
     */
    public static String getModoConexion() {
        return modoConexion;
    }
}