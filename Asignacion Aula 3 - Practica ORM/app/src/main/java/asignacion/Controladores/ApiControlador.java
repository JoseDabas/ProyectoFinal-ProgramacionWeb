package asignacion.Controladores;

import asignacion.Entidades.Estudiante;
import asignacion.Services.EstudianteServices;
import asignacion.Util.BaseControlador;
import asignacion.Util.NoExisteEstudianteException;
import io.javalin.Javalin;

import static io.javalin.apibuilder.ApiBuilder.*;

public class ApiControlador extends BaseControlador {

    private EstudianteServices estudianteServices = EstudianteServices.getInstancia();

    public ApiControlador(Javalin app) {
        super(app);
    }

    @Override
    public void aplicarRutas() {
        app.routes(() -> {
            path("/api", () -> {
                /**
                 * Ejemplo de una API REST, implementando el CRUD
                 * ir a
                 */
                path("/estudiante", () -> {
                    after(ctx -> {
                        ctx.header("Content-Type", "application/json");
                    });

                    get("/", ctx -> {
                        ctx.json(estudianteServices.listarEstudiante());
                    });

                    get("/{matricula}", ctx -> {
                        ctx.json(estudianteServices
                                .getEstudiantePorMatricula(ctx.pathParamAsClass("matricula", Integer.class).get()));
                    });

                    post("/", ctx -> {
                        // parseando la informacion del POJO debe venir en formato json.
                        Estudiante tmp = ctx.bodyAsClass(Estudiante.class);
                        // creando.
                        ctx.json(estudianteServices.crearEstudiante(tmp));
                    });

                    put("/", ctx -> {
                        // parseando la informacion del POJO.
                        Estudiante tmp = ctx.bodyAsClass(Estudiante.class);
                        // creando.
                        ctx.json(estudianteServices.actualizarEstudiante(tmp));

                    });

                    delete("/{matricula}", ctx -> {
                        // creando.
                        ctx.json(estudianteServices
                                .eliminandoEstudiante(ctx.pathParamAsClass("matricula", Integer.class).get()));
                    });
                });
            });
        });

        app.exception(NoExisteEstudianteException.class, (exception, ctx) -> {
            ctx.status(404);
            ctx.json("" + exception.getLocalizedMessage());
        });
    }
}