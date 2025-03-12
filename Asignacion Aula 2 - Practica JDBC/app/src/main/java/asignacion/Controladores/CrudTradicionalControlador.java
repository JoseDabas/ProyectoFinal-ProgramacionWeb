package asignacion.Controladores;

import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;
import asignacion.Entidades.Estudiante;
import asignacion.Services.EstudianteServices;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

public class CrudTradicionalControlador extends BaseControlador {

    EstudianteServices estudianteServices = EstudianteServices.getInstancia();

    public CrudTradicionalControlador(Javalin app) {
        super(app);
        registerTemplates();
    }

    public void registerTemplates() {
        JavalinRenderer.register(new JavalinThymeleaf(), ".html");
    }

    @Override
    public void aplicarRutas() {
        app.get("/", ctx -> {
            ctx.redirect("/crud-simple/");
        });

        app.routes(() -> {
            path("/crud-simple/", () -> {
                get("/", ctx -> {
                    ctx.redirect("/crud-simple/listar");
                });

                get("/listar", ctx -> {
                    List<Estudiante> lista = estudianteServices.listarEstudiante();
                    Map<String, Object> modelo = new HashMap<>();
                    modelo.put("titulo", "Listado de Estudiante");
                    modelo.put("lista", lista);
                    ctx.render("/templates/crud-tradicional/listar.html", modelo);
                });

                get("/crear", ctx -> {
                    Map<String, Object> modelo = new HashMap<>();
                    modelo.put("titulo", "Formulario Creación Estudiante");
                    modelo.put("accion", "/crud-simple/crear");
                    ctx.render("/templates/crud-tradicional/crearEditarVisualizar.html", modelo);
                });

                post("/crear", ctx -> {
                    int matricula = ctx.formParamAsClass("matricula", Integer.class).get();
                    String nombre = ctx.formParam("nombre");
                    String carrera = ctx.formParam("carrera");
                    Estudiante tmp = new Estudiante(matricula, nombre, carrera);
                    estudianteServices.crearEstudiante(tmp);
                    ctx.redirect("/crud-simple/");
                });

                get("/visualizar/{matricula}", ctx -> {
                    Estudiante estudiante = estudianteServices
                            .getEstudiantePorMatricula(ctx.pathParamAsClass("matricula", Integer.class).get());
                    Map<String, Object> modelo = new HashMap<>();
                    modelo.put("titulo", "Formulario Visaulizar Estudiante " + estudiante.getMatricula());
                    modelo.put("estudiante", estudiante);
                    modelo.put("visualizar", true);
                    modelo.put("accion", "/crud-simple/");
                    ctx.render("/templates/crud-tradicional/crearEditarVisualizar.html", modelo);
                });

                get("/editar/{matricula}", ctx -> {
                    Estudiante estudiante = estudianteServices
                            .getEstudiantePorMatricula(ctx.pathParamAsClass("matricula", Integer.class).get());
                    Map<String, Object> modelo = new HashMap<>();
                    modelo.put("titulo", "Formulario Editar Estudiante " + estudiante.getMatricula());
                    modelo.put("estudiante", estudiante);
                    modelo.put("accion", "/crud-simple/editar");
                    ctx.render("/templates/crud-tradicional/crearEditarVisualizar.html", modelo);
                });

                post("/editar", ctx -> {
                    int matricula = ctx.formParamAsClass("matricula", Integer.class).get();
                    String nombre = ctx.formParam("nombre");
                    String carrera = ctx.formParam("carrera");
                    Estudiante tmp = new Estudiante(matricula, nombre, carrera);
                    estudianteServices.actualizarEstudiante(tmp);
                    ctx.redirect("/crud-simple/");
                });

                get("/eliminar/{matricula}", ctx -> {
                    estudianteServices.eliminarEstudiante(ctx.pathParamAsClass("matricula", Integer.class).get());
                    ctx.redirect("/crud-simple/");
                });

            });
        });
    }
}
