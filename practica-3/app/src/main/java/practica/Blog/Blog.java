package practica.Blog;

import practica.Encapsulaciones.Articulo;
import practica.Encapsulaciones.Comentario;
import practica.Encapsulaciones.Etiqueta;
import practica.Encapsulaciones.Usuario;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class Blog {
    private static Blog instance = null;
    private ArrayList<Articulo> articulos;
    private Map<String, Usuario> usuarios; // Usamos un HashMap para los usuarios
    private ArrayList<Comentario> comentarios;
    private ArrayList<Etiqueta> etiquetas;
    private static AtomicLong genArt = new AtomicLong(1);
    private static AtomicLong genCom = new AtomicLong(1);
    private static AtomicLong genEt = new AtomicLong(1);

    private Blog() {
        this.articulos = new ArrayList<>();
        this.usuarios = new HashMap<>(); // Inicializamos el HashMap
        this.comentarios = new ArrayList<>();
        this.etiquetas = new ArrayList<>();
    }

    public static Blog getInstance() {
        if (instance == null) {
            instance = new Blog();
        }
        return instance;
    }

    public void agregarUsuario(Usuario usuario) {
        if (usuario.getUsername() != null && findUserByUsername(usuario.getUsername()) != null) {
            return; // El usuario ya existe, no lo agregamos
        }
        this.usuarios.put(usuario.getUsername(), usuario); // Guardamos el usuario en el HashMap
    }

    public void agregarArticulo(Articulo articulo) {
        articulo.setId(genArt.getAndIncrement());
        this.articulos.add(articulo);
    }

    public void agregarComentario(Comentario comentario) {
        comentario.setId(genCom.getAndIncrement());
        this.comentarios.add(comentario);
    }

    public void agregarEtiqueta(Etiqueta etiqueta) {
        etiqueta.setId(genEt.getAndIncrement());
        this.etiquetas.add(etiqueta);
    }

    public ArrayList<Usuario> getUsuarios() {
        return new ArrayList<>(this.usuarios.values()); // Convertimos el HashMap en ArrayList
    }

    public ArrayList<Articulo> getArticulos() {
        return this.articulos;
    }

    public ArrayList<Comentario> getComentarios() {
        return this.comentarios;
    }

    public ArrayList<Etiqueta> getEtiquetas() {
        return this.etiquetas;
    }

    public static long getGenArt() {
        return genArt.get();
    }

    public static long getGenCom() {
        return genCom.get();
    }

    public static long getGenEt() {
        return genEt.get();
    }

    public Usuario findUserByUsername(String username) {
        return this.usuarios.get(username); // Búsqueda eficiente en el HashMap
    }

    public Articulo findArticuloById(long id) {
        for (Articulo art : articulos) {
            if (art.getId() == id) {
                return art;
            }
        }
        return null;
    }

    public Etiqueta findEtiquetaById(long id) {
        for (Etiqueta etq : etiquetas) {
            if (etq.getId() == id) {
                return etq;
            }
        }
        return null;
    }

    public Comentario findComentarioById(long id) {
        for (Comentario com : comentarios) {
            if (com.getId() == id) {
                return com;
            }
        }
        return null;
    }

    public void eliminarArticulo(long id) {
        Articulo articulo = this.findArticuloById(id);
        if (articulo != null) {
            this.articulos.remove(articulo);
        }
    }
}
