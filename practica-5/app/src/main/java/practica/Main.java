package practica;

import io.javalin.Javalin;
import practica.Controladores.ArticuloControlador;
import practica.Controladores.InicioControlador;
import practica.Controladores.UsuarioControlador;

import practica.Servicios.*;

import practica.WebSocket.ChatWebSocket;

import practica.Entidades.Usuario;
//import jakarta.persistence.Persistence;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityManagerFactory;
import practica.Controladores.ChatControlador;

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
            // Usar configuración de plugins y registros compatible con tu versión
            javalinConfig.plugins.enableDevLogging();
            // Configurar CORS para WebSockets
            javalinConfig.plugins.enableCors(cors -> {
                cors.add(it -> it.anyHost());
            });
        }).start(8000);

        // Configuración de WebSockets
        app.ws("/chat", ws -> {
            ws.onConnect(ChatWebSocket::handleConnect);
            ws.onMessage(ChatWebSocket::handleMessage);
            ws.onClose(ChatWebSocket::handleClose);
        });

        // Aplicar rutas de los controladores
        new UsuarioControlador(app).aplicarRutas();
        new InicioControlador(app).aplicarRutas();
        new ArticuloControlador(app).aplicarRutas();
        new ChatControlador(app).aplicarRutas(); // Nuevo controlador para chat
    }
}