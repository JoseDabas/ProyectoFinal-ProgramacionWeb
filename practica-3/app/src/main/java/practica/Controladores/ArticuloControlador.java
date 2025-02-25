package practica.Controladores;

import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;

import practica.Entidades.*;
import practica.Servicios.*;

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
        this.app.before("/articulo/crear", (ctx) -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null) {
                ctx.redirect("/user/login");
                return;
            }
        });

        this.app.get("/{page}", ctx -> {

            int page = Integer.parseInt(ctx.pathParam("page"));

            List<Articulo> articulos = ArticuloServices.getInstance().findAllRecentPag(page, 5);
            List<Etiqueta> etiquetas = EtiquetaServices.getInstance().findAll();

            int totalArticulos = ArticuloServices.getInstance().findAllRecent().size();
            int totalPages = (int) Math.ceil((double) totalArticulos / 5);

            // Crear el modelo para la vista
            Map<String, Object> model = new HashMap<>();
            model.put("articulos", articulos);
            model.put("totalPages", totalPages);
            model.put("currentPage", page);
            model.put("etiquetas", etiquetas);

            // Renderizar la vista con Thymeleaf y pasar los datos al modelo
            ctx.render("/templates/index.html", model);
        });

        this.app.get("/articulo/crear", ctx -> {
            List<Etiqueta> etiquetas = EtiquetaServices.getInstance().findAll();
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Crear Articulo");
            model.put("etiquetas", etiquetas);
            ctx.render("/templates/CrearArticulo.html", model);
        });

        this.app.post("/articulo/crear", ctx -> {
            String titulo = ctx.formParam("titulo");
            String cuerpo = ctx.formParam("cuerpo");
            List<Etiqueta> etiq = new ArrayList<>();
            Usuario usuario = ctx.sessionAttribute("username");
            String idsEtiquetasExistentesStr = ctx.formParam("etiquetasExistentes");
            String nuevasEtiquetasStr = ctx.formParam("nuevasEtiquetas");
            List<String> idsEtiquetasExistentes = idsEtiquetasExistentesStr != null
                    ? new ArrayList<>(Arrays.asList(idsEtiquetasExistentesStr.split(",")))
                    : new ArrayList<>();
            List<String> nuevasEtiquetas = nuevasEtiquetasStr != null
                    ? new ArrayList<>(Arrays.asList(nuevasEtiquetasStr.split(",")))
                    : new ArrayList<>();
            for (String id : idsEtiquetasExistentes) {
                Etiqueta etiquetaExistente = EtiquetaServices.getInstance().findAll().stream()
                        .filter(e -> e.getId() == Long.parseLong(id))
                        .findFirst()
                        .orElse(null);
                if (etiquetaExistente != null) {
                    etiq.add(etiquetaExistente);
                }
            }

            for (String etiquetaStr : nuevasEtiquetas) {
                if (etiquetaStr.isEmpty()) {
                    continue;
                }
                Etiqueta nuevaEtiqueta = new Etiqueta(etiquetaStr);
                EtiquetaServices.getInstance().create(nuevaEtiqueta);
                etiq.add(nuevaEtiqueta);
            }
            Articulo temp = new Articulo(titulo, cuerpo, usuario, new Date(), etiq);
            ArticuloServices.getInstance().create(temp);
            ctx.redirect("/");
        });

        this.app.get("/articulo/detalle/{id}", ctx -> {
            long id = Long.parseLong(ctx.pathParam("id"));
            List<Comentario> listaComents = ComentarioServices.getInstance().findAllByArticulo(id);
            Articulo articulo = ArticuloServices.getInstance().buscar(id);

            Map<String, Object> model = new HashMap<>();
            model.put("articulo", articulo);
            model.put("listaComentarios", listaComents);
            ctx.render("/templates/MostrarArticulo.html", model);
        });

        this.app.post("/articulo/comentar", ctx -> {
            String comentario = ctx.formParam("comentario");
            Usuario usuario = ctx.sessionAttribute("username");
            long idArticulo = Long.parseLong(ctx.formParam("id"));
            Articulo articulo = ArticuloServices.getInstance().find(idArticulo);
            Hibernate.initialize(articulo.getListaComentarios()); // Initialize the collection
            Comentario temp = new Comentario(comentario, UsuarioServices.getInstance().find(usuario.getUsername()),
                    articulo);
            articulo.agregarComentario(temp);
            ComentarioServices.getInstance().create(temp);
            ArticuloServices.getInstance().edit(articulo);
            ctx.redirect("/articulo/detalle/" + idArticulo);
        });

        this.app.post("/articulo/borrar", ctx -> {
            long id = Long.parseLong(ctx.formParam("id"));
            Articulo articulo = ArticuloServices.getInstance().find(id);
            ArticuloServices.getInstance().remove(articulo);
            ctx.redirect("/");
        });

        this.app.before("/articulo/editar/lista", ctx -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null) {
                ctx.redirect("/user/login");
                return;
            }
        });

        this.app.get("/articulo/editar/lista", ctx -> {
            Map<String, Object> model = new HashMap<>();
            Usuario aux = ctx.sessionAttribute("username");
            ArrayList<Articulo> articulosUser = new ArrayList<>();

            for (Articulo articulo : ArticuloServices.getInstance().findAllRecent()) {
                if (articulo.getAutor().getUsername().equals(aux.getUsername())) {
                    articulosUser.add(articulo);
                }
            }
            model.put("titulo", "Lista de Articulos del Usuario");
            model.put("articulos", articulosUser);
            ctx.render("/templates/ListaArticulosUsuario.html", model);
        });

        this.app.before("/articulo/editar/{id}", ctx -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null) {
                ctx.redirect("/user/login");
                return;
            }
        });

        this.app.get("/articulo/editar/{id}", ctx -> {
            long id = Long.parseLong(ctx.pathParam("id"));
            Articulo articulo = ArticuloServices.getInstance().buscar(id);
            if (articulo == null) {
                ctx.status(404).result("Artículo no encontrado");
                return;
            }
            List<Etiqueta> etiquetas = EtiquetaServices.getInstance().findAll();
            Map<String, Object> model = new HashMap<>();
            model.put("titulo", "Editar Articulo");
            model.put("articulo", articulo);
            model.put("etiquetas", etiquetas);
            ctx.render("/templates/EditarArticulo.html", model);
        });

        app.post("articulo/editar", ctx -> {
            long idArticulo = Long.parseLong(ctx.formParam("id"));
            Articulo articulo = ArticuloServices.getInstance().find(idArticulo);
            List<Etiqueta> etiq = new ArrayList<>();
            String idsEtiquetasExistentesStr = ctx.formParam("etiquetasExistentes");
            String nuevasEtiquetasStr = ctx.formParam("nuevasEtiquetas");
            List<String> idsEtiquetasExistentes = idsEtiquetasExistentesStr != null
                    ? new ArrayList<>(Arrays.asList(idsEtiquetasExistentesStr.split(",")))
                    : new ArrayList<>();
            List<String> nuevasEtiquetas = nuevasEtiquetasStr != null
                    ? new ArrayList<>(Arrays.asList(nuevasEtiquetasStr.split(",")))
                    : new ArrayList<>();
            for (String id : idsEtiquetasExistentes) {
                Etiqueta etiquetaExistente = EtiquetaServices.getInstance().findAll().stream()
                        .filter(e -> e.getId() == Long.parseLong(id))
                        .findFirst()
                        .orElse(null);
                if (etiquetaExistente != null) {
                    etiq.add(etiquetaExistente);
                }
            }

            for (String etiquetaStr : nuevasEtiquetas) {
                if (etiquetaStr.isEmpty()) {
                    continue;
                }
                Etiqueta nuevaEtiqueta = new Etiqueta(etiquetaStr);
                EtiquetaServices.getInstance().create(nuevaEtiqueta);
                etiq.add(nuevaEtiqueta);
            }
            articulo.setCuerpo(ctx.formParam("cuerpo"));
            articulo.setTitulo(ctx.formParam("titulo"));
            articulo.setListaEtiquetas(etiq);
            ArticuloServices.getInstance().edit(articulo);
            ctx.redirect("/");
        });

        this.app.get("/articulos/etiqueta/{id}", ctx -> {
            try {
                // Obtener el ID de la etiqueta desde el parámetro de la URL
                long idEtiqueta = Long.parseLong(ctx.pathParam("id"));

                // Buscar la etiqueta por ID
                Etiqueta etiqueta = EtiquetaServices.getInstance().find(idEtiqueta);

                if (etiqueta == null) {
                    // Si no se encuentra la etiqueta, devolver error 404
                    ctx.status(404).result("Etiqueta no encontrada");
                    return;
                }

                // Obtener los artículos asociados a la etiqueta
                List<Articulo> articulosPorEtiqueta = ArticuloServices.getInstance()
                        .findAllByTag(etiqueta.getEtiqueta());

                if (articulosPorEtiqueta.isEmpty()) {
                    // Si no hay artículos asociados, devolver un mensaje apropiado
                    ctx.status(404).result("No se encontraron artículos para esta etiqueta");
                    return;
                }

                // Crear el modelo para Thymeleaf
                Map<String, Object> model = new HashMap<>();
                model.put("titulo", "Artículos por Etiqueta: " + etiqueta.getEtiqueta());
                model.put("articulos", articulosPorEtiqueta);
                model.put("etiquetas", EtiquetaServices.getInstance().findAll());

                // Enviar la respuesta al cliente renderizando la plantilla
                ctx.render("/templates/ArticulosEtiqueta.html", model);
            } catch (NumberFormatException e) {
                // Si el ID no es válido
                ctx.status(400).result("ID de etiqueta inválido");
                e.printStackTrace(); // Esto te ayudará a depurar el error
            } catch (Exception e) {
                // Captura cualquier otro error
                ctx.status(500).result("Error al obtener artículos por etiqueta");
                e.printStackTrace(); // Esto te ayudará a depurar el error
            }
        });

    }
}