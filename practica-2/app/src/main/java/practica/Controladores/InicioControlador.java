package practica.Controladores;

import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;
import practica.Blog.Blog;
import practica.Encapsulaciones.Articulo;
import practica.Encapsulaciones.Etiqueta;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
//import java.util.concurrent.atomic.AtomicBoolean;

public class InicioControlador extends BaseControlador {

    public InicioControlador(Javalin app) {
        super(app);
        this.registerTemplates();
    }

    public void registerTemplates() {
        JavalinRenderer.register(new JavalinThymeleaf(), new String[] { ".html" });
    }

    @Override
    public void aplicarRutas() {

        app.get("/", ctx -> {
            Map<String, Object> model = new HashMap<>();
            ArrayList<Articulo> articulos = Blog.getInstance().getArticulos();
            ArrayList<Etiqueta> etiquetas = Blog.getInstance().getEtiquetas();
            articulos.sort(Comparator.comparing(Articulo::getFecha).reversed());
            model.put("titulo", "Inicio");
            model.put("articulos", articulos);
            model.put("etiquetas", etiquetas);
            ctx.render("/templates/index.html", model);
        });
    }
}
