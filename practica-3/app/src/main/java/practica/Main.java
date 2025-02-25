package practica;

import io.javalin.Javalin;
//import practica.Blog.Blog;
import practica.Controladores.ArticuloControlador;
import practica.Controladores.InicioControlador;
import practica.Controladores.UsuarioControlador;

//import practica.Entidades.Articulo;
//import practica.Entidades.Etiqueta;
import practica.Servicios.*;
import practica.Entidades.Usuario;

//import jakarta.persistence.Persistence;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityManagerFactory;

public class Main {
    public static void main(String[] args) {

        IniciarServices.getInstance().startDb();
        Usuario admin = UsuarioServices.getInstance().find("admin");

        if (admin == null) {

            System.out.println("El usuario 'admin' no existe. Creándolo...");
            admin = new Usuario("admin", "admin", "admin", true, false);
            UsuarioServices.getInstance().create(admin);
        } else {
            System.out.println("El usuario 'admin' ya existe.");
        }

        Javalin app = Javalin.create(javalinConfig -> {
            // Configuración de Javalin si es necesario
        }).start(8000);

        // Aplicar rutas de los controladores
        new UsuarioControlador(app).aplicarRutas();
        new InicioControlador(app).aplicarRutas();
        new ArticuloControlador(app).aplicarRutas();
    }
}
