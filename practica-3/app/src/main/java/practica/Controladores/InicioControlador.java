package practica.Controladores;

import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;

//import java.util.ArrayList;
import java.util.List;
//import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import practica.Servicios.*;
import practica.Entidades.*;

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

        app.before("/", ctx -> {
            if (ctx.cookie("rememberedUser") != null) {
                Usuario aux = UsuarioServices.getInstance().find(ctx.cookie("rememberedUser"));
                ctx.sessionAttribute("username", aux);
            }
        });

        app.get("/", ctx -> {
            String pageParam = ctx.queryParam("page");
            int page = (pageParam != null) ? Integer.parseInt(pageParam) : 1;
            List<Articulo> articulos = ArticuloServices.getInstance().findAllRecentPag(page, 5);
            List<Etiqueta> etiquetas = EtiquetaServices.getInstance().findAll();
            int totalArticles = ArticuloServices.getInstance().findAll().size();
            int totalPages = (int) Math.ceil((double) totalArticles / 5);
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Inicio");
            model.put("articulos", articulos);
            model.put("totalPages", totalPages);
            model.put("currentPage", page);
            model.put("etiquetas", etiquetas);
            ctx.render("/templates/index.html", model);
        });

        app.get("/tag", ctx -> {
            String pageParam = ctx.queryParam("page");
            int page = (pageParam != null) ? Integer.parseInt(pageParam) : 1;
            String tag = ctx.queryParam("tag");
            List<Articulo> articulos = ArticuloServices.getInstance().findAllByTagPag(tag, page, 5);
            List<Etiqueta> etiquetas = EtiquetaServices.getInstance().findAll();
            int totalArticles = ArticuloServices.getInstance().findAllByTag(tag).size();
            int totalPages = (int) Math.ceil((double) totalArticles / 5);
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Inicio");
            model.put("articulos", articulos);
            model.put("totalPages", totalPages);
            model.put("currentPage", page);
            model.put("etiquetas", etiquetas);
            ctx.render("/templates/index.html", model);
        });

    }
}
