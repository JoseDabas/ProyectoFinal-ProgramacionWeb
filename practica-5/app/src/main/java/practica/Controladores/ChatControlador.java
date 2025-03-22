package practica.Controladores;

import io.javalin.Javalin;
import io.javalin.rendering.JavalinRenderer;
import io.javalin.rendering.template.JavalinThymeleaf;
import practica.Entidades.ChatMensaje;
import practica.Entidades.Usuario;
import practica.Servicios.ChatServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatControlador extends BaseControlador {

    public ChatControlador(Javalin app) {
        super(app);
        registerTemplates();
    }

    public void registerTemplates() {
        JavalinRenderer.register(new JavalinThymeleaf(), ".html");
    }

    @Override
    public void aplicarRutas() {
        // Ruta para la sección de administración de chats (solo admins y autores)
        this.app.before("/admin/chats", ctx -> {
            Usuario usuario = ctx.sessionAttribute("username");
            if (usuario == null || (!usuario.isAdministrator() && !usuario.isAutor())) {
                ctx.redirect("/");
                return;
            }
        });

        // Panel de administración de chats
        this.app.get("/admin/chats", ctx -> {
            try {
                System.out.println("Accediendo a panel de administración de chats");

                // Usar el nuevo método para obtener información más completa
                List<Map<String, String>> sessionInfos = null;
                try {
                    sessionInfos = ChatServices.getInstance().findActiveSessionsInfo();
                    System.out.println("Sesiones activas: " +
                            (sessionInfos != null ? sessionInfos.size() : "null"));
                } catch (Exception e) {
                    System.err.println("Error obteniendo sesiones activas: " + e.getMessage());
                    e.printStackTrace();
                    sessionInfos = new ArrayList<>();
                }

                // También obtener la lista simple para compatibilidad
                List<String> sesionesActivas = ChatServices.getInstance().findActiveSessions();

                Map<String, Object> model = new HashMap<>();
                model.put("titulo", "Administración de Chats");
                model.put("sesiones", sesionesActivas); // Mantener para compatibilidad
                model.put("sessionInfos", sessionInfos); // Nueva lista con info completa

                try {
                    ctx.render("/templates/AdminChats.html", model);
                    System.out.println("Renderizado exitoso");
                } catch (Exception e) {
                    System.err.println("Error renderizando la plantilla: " + e.getMessage());
                    e.printStackTrace();
                    ctx.status(500).result("Error renderizando la plantilla: " + e.getMessage());
                }
            } catch (Exception e) {
                System.err.println("Error general en panel de administración de chats: " + e.getMessage());
                e.printStackTrace();
                ctx.status(500).result("Error al cargar el panel de administración de chats");
            }
        });

        // Ver conversación específica - CAMBIANDO :sessionId por {sessionId}
        this.app.get("/admin/chat/{sessionId}", ctx -> {
            try {
                String sessionId = ctx.pathParam("sessionId");
                List<ChatMensaje> mensajes = ChatServices.getInstance().findBySesionId(sessionId);

                Usuario admin = ctx.sessionAttribute("username");

                Map<String, Object> model = new HashMap<>();
                model.put("titulo", "Conversación de Chat");
                model.put("mensajes", mensajes != null ? mensajes : new ArrayList<>());
                model.put("sessionId", sessionId);
                model.put("admin", admin);

                if (mensajes != null && !mensajes.isEmpty()) {
                    model.put("nombreVisitante", mensajes.get(0).getNombreVisitante());
                }

                ctx.render("/templates/ChatConversacion.html", model);
            } catch (Exception e) {
                System.err.println("Error al cargar la conversación de chat: " + e.getMessage());
                e.printStackTrace();
                ctx.status(500).result("Error al cargar la conversación de chat");
            }
        });

        // API para obtener mensajes de una sesión - CAMBIANDO :sessionId por
        // {sessionId}
        this.app.get("/api/chat/{sessionId}", ctx -> {
            try {
                String sessionId = ctx.pathParam("sessionId");
                List<ChatMensaje> mensajes = ChatServices.getInstance().findBySesionId(sessionId);
                ctx.json(mensajes != null ? mensajes : new ArrayList<>());
            } catch (Exception e) {
                System.err.println("Error al obtener mensajes de chat: " + e.getMessage());
                e.printStackTrace();
                ctx.status(500).json(Map.of("error", "Error al obtener mensajes de chat"));
            }
        });
    }
}