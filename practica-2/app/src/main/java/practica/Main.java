package practica;

import io.javalin.Javalin;
import practica.Blog.Blog;
import practica.Controladores.ArticuloControlador;
import practica.Controladores.InicioControlador;
import practica.Controladores.UsuarioControlador;
import practica.Encapsulaciones.Usuario;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(javalinConfig -> {
            // Configuración de Javalin si es necesario
        }).start(8000);

        // Verificar si el usuario admin ya existe antes de agregarlo
        Usuario admin = Blog.getInstance().findUserByUsername("admin");
        if (admin == null) {
            // Crear un usuario administrador por defecto si no existe
            admin = new Usuario("admin", "admin", "admin", true, false);
            Blog.getInstance().agregarUsuario(admin);
        }

        // Aplicar rutas de los controladores
        new UsuarioControlador(app).aplicarRutas();
        new InicioControlador(app).aplicarRutas();
        new ArticuloControlador(app).aplicarRutas();
    }
}
