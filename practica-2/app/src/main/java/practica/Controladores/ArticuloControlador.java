package practica.Controladores;

import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import practica.Blog.Blog;
import practica.Encapsulaciones.Articulo;
import practica.Encapsulaciones.Comentario;
import practica.Encapsulaciones.Etiqueta;
import practica.Encapsulaciones.Usuario;

public class ArticuloControlador extends BaseControlador {

    public ArticuloControlador(Javalin app) {
        super(app);
        registerTemplates();
    }

    public void registerTemplates() {
        JavalinRenderer.register(new JavalinThymeleaf(), ".html");
    }

    @Override
    public void aplicarRutas() {
        app.before("/articulo/crear", (ctx) -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null) {
                ctx.redirect("/user/login");
                return;
            }
        });

        app.get("/articulo/crear", (ctx) -> {
            ArrayList<Etiqueta> etiquetas = Blog.getInstance().getEtiquetas();
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Crear Articulo");
            model.put("etiquetas", etiquetas);
            ctx.render("/templates/CrearArticulo.html", model);
        });

        app.post("/articulo/crear", ctx -> {
            String titulo = ctx.formParam("titulo");
            String cuerpo = ctx.formParam("cuerpo");
            String[] etiquetasExistentes = ctx.formParams("etiquetasExistentes").toArray(new String[0]);
            String nuevasEtiquetas = ctx.formParam("nuevasEtiquetas");
            Usuario usuario = ctx.sessionAttribute("username");

            if (usuario == null) {
                ctx.redirect("/user/login");
                return;
            }

            if (titulo == null || titulo.isEmpty() || cuerpo == null || cuerpo.isEmpty()) {
                ctx.render("/templates/CrearArticulo.html", Map.of("error", "El título y el cuerpo son obligatorios"));
                return;
            }

            ArrayList<Etiqueta> etiq = new ArrayList<>();

            // Agregar etiquetas existentes
            for (String etiquetaIdStr : etiquetasExistentes) {
                try {
                    long etiquetaId = Long.parseLong(etiquetaIdStr);
                    Etiqueta etiqueta = Blog.getInstance().findEtiquetaById(etiquetaId);
                    if (etiqueta != null) {
                        etiq.add(etiqueta);
                    }
                } catch (NumberFormatException e) {
                    // Manejar el error de conversión si es necesario
                }
            }

            // Agregar nuevas etiquetas
            if (nuevasEtiquetas != null && !nuevasEtiquetas.isEmpty()) {
                String[] nuevasEtiquetasArray = nuevasEtiquetas.split(",");
                for (String etiquetaNombre : nuevasEtiquetasArray) {
                    Etiqueta nuevaEtiqueta = new Etiqueta(Blog.getGenEt(), etiquetaNombre.trim());
                    Blog.getInstance().agregarEtiqueta(nuevaEtiqueta);
                    etiq.add(nuevaEtiqueta);
                }
            }

            Articulo temp = new Articulo(Blog.getGenArt(), titulo, cuerpo, usuario, new Date(), etiq);

            Blog.getInstance().agregarArticulo(temp);
            ctx.redirect("/");
        });

        app.get("/articulo/detalle/{id}", ctx -> {
            long id = Long.parseLong(ctx.pathParam("id"));
            Articulo articulo = Blog.getInstance().findArticuloById(id);
            Map<String, Object> model = new HashMap<>();
            model.put("articulo", articulo);
            model.put("comentarios", articulo.getListaComentarios());
            ctx.render("/templates/MostrarArticulo.html", model);
        });

        app.post("/articulo/comentar", ctx -> {
            String comentario = ctx.formParam("comentario");
            Usuario usuario = ctx.sessionAttribute("username");
            long idArticulo = Long.parseLong(ctx.formParam("id"));
            Articulo articulo = Blog.getInstance().findArticuloById(idArticulo);

            if (usuario != null && comentario != null && !comentario.isEmpty()) {
                Comentario temp = new Comentario(Blog.getGenCom(), comentario, usuario, articulo);
                articulo.agregarComentario(temp);
                Blog.getInstance().agregarComentario(temp);
            }
            ctx.redirect("/articulo/detalle/" + idArticulo);
        });

        app.post("/articulo/borrar", ctx -> {
            long id = Long.parseLong(ctx.formParam("id"));
            Blog.getInstance().eliminarArticulo(id);
            ctx.redirect("/");
        });

        app.before("/articulo/editar/lista", ctx -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null) {
                ctx.redirect("/user/login");
                return;
            }
        });

        app.get("/articulo/editar/lista", ctx -> {
            Map<String, Object> model = new HashMap<>();
            Usuario aux = ctx.sessionAttribute("username");
            ArrayList<Articulo> articulosUser = new ArrayList<>();

            for (Articulo articulo : Blog.getInstance().getArticulos()) {
                if (articulo.getAutor().getUsername() != null
                        && articulo.getAutor().getUsername().equals(aux.getUsername())) {
                    articulosUser.add(articulo);
                }
            }
            model.put("titulo", "Lista de Articulos del Usuario");
            model.put("articulos", articulosUser);
            ctx.render("/templates/ListaArticulosUsuario.html", model);
        });

        app.before("/articulo/editar/{id}", ctx -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null) {
                ctx.redirect("/user/login");
                return;
            }
        });

        app.get("/articulo/editar/{id}", ctx -> {
            long id = Long.parseLong(ctx.pathParam("id"));
            Articulo articulo = Blog.getInstance().findArticuloById(id);
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Editar Articulo");
            model.put("articulo", articulo);
            ctx.render("/templates/EditarArticulo.html", model);
        });

        app.post("/articulo/editar", ctx -> {
            long idArticulo = Long.parseLong(ctx.formParam("id"));
            Articulo articulo = Blog.getInstance().findArticuloById(idArticulo);
            articulo.setCuerpo(ctx.formParam("cuerpo"));
            articulo.setTitulo(ctx.formParam("titulo"));
            ctx.redirect("/");
        });
    }
}