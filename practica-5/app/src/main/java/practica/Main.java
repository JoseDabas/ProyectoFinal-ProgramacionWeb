package practica;

import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import practica.Controladores.ArticuloControlador;
import practica.Controladores.InicioControlador;
import practica.Controladores.UsuarioControlador;

import practica.Servicios.*;

import practica.WebSocket.ChatWebSocket;

import practica.Entidades.Usuario;
import practica.Controladores.ChatControlador;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;

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

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new Hibernate5JakartaModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Javalin app = Javalin.create(javalinConfig -> {

            javalinConfig.jsonMapper(new JavalinJackson(objectMapper));
            javalinConfig.plugins.enableDevLogging();
            javalinConfig.staticFiles.add("/public");

        });

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

        app.start(7000);
    }
}