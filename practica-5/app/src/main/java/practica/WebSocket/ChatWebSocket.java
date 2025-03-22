package practica.WebSocket;

import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsMessageContext;
import practica.Entidades.ChatMensaje;
import practica.Entidades.Usuario;
import practica.Servicios.ChatServices;
import practica.Servicios.UsuarioServices;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ChatWebSocket {
    // Mapa para almacenar las conexiones de usuarios
    private static Map<String, WsContext> userSessions = new ConcurrentHashMap<>();
    // Mapa para almacenar las conexiones de administradores
    private static Map<String, WsContext> adminSessions = new ConcurrentHashMap<>();

    private static Gson gson = new Gson();

    public static void handleConnect(WsConnectContext ctx) {
        System.out.println("WebSocket conectado: " + ctx.getSessionId());
    }

    public static void handleMessage(WsMessageContext ctx) {
        String message = ctx.message();
        System.out.println("Mensaje recibido: " + message);

        try {
            Map<String, Object> messageMap = gson.fromJson(message, new TypeToken<Map<String, Object>>() {
            }.getType());
            String type = (String) messageMap.get("type");

            if ("register".equals(type)) {
                // Registrar usuario o administrador
                boolean isAdmin = messageMap.get("isAdmin") != null && (boolean) messageMap.get("isAdmin");

                if (isAdmin) {
                    String username = (String) messageMap.get("username");
                    System.out.println("Registrando administrador con nombre de usuario: " + username);
                    adminSessions.put(username, ctx);
                    System.out.println("Administrador registrado: " + username);
                    System.out.println("Total administradores conectados: " + adminSessions.size());
                } else {
                    String sessionId = (String) messageMap.get("sessionId");
                    userSessions.put(sessionId, ctx);
                    System.out.println("Usuario registrado: " + sessionId);
                    System.out.println("Total usuarios conectados: " + userSessions.size());
                }
            } else if ("chat".equals(type)) {
                // Procesar y reenviar mensaje de chat
                String sessionId = (String) messageMap.get("sessionId");
                String visitorName = (String) messageMap.get("visitorName");
                String messageText = (String) messageMap.get("message");
                boolean isAdmin = messageMap.get("isAdmin") != null && (boolean) messageMap.get("isAdmin");

                System.out.println("Mensaje de chat recibido: sessionId=" + sessionId +
                        ", visitorName=" + visitorName +
                        ", isAdmin=" + isAdmin +
                        ", mensaje=" + messageText);

                // Guardar mensaje en la base de datos
                ChatMensaje chatMensaje = new ChatMensaje(messageText, visitorName, isAdmin, sessionId);

                if (isAdmin) {
                    String adminUsername = (String) messageMap.get("adminUsername");
                    System.out.println("Mensaje del administrador: " + adminUsername);
                    Usuario admin = UsuarioServices.getInstance().find(adminUsername);
                    if (admin != null) {
                        chatMensaje.setAdministrador(admin);
                        System.out.println("Administrador encontrado en la base de datos");
                    } else {
                        System.out.println("Administrador no encontrado en la base de datos: " + adminUsername);
                    }
                }

                try {
                    ChatServices.getInstance().create(chatMensaje);
                    System.out.println("Mensaje guardado en la base de datos");
                } catch (Exception e) {
                    System.err.println("Error guardando mensaje en la base de datos: " + e.getMessage());
                    e.printStackTrace();
                }

                // Reenviar el mensaje al destinatario correspondiente
                if (isAdmin && userSessions.containsKey(sessionId)) {
                    System.out.println("Reenviando mensaje del administrador al usuario: " + sessionId);
                    userSessions.get(sessionId).send(message);
                } else if (!isAdmin) {
                    // Notificar a todos los administradores
                    System.out.println("Notificando a todos los administradores. Total: " + adminSessions.size());

                    if (adminSessions.isEmpty()) {
                        System.out.println("No hay administradores conectados");
                    }

                    for (Map.Entry<String, WsContext> entry : adminSessions.entrySet()) {
                        String adminUsername = entry.getKey();
                        WsContext adminCtx = entry.getValue();

                        System.out.println("Enviando a administrador: " + adminUsername);
                        if (adminCtx.session.isOpen()) {
                            adminCtx.send(message);
                            System.out.println("Mensaje enviado a administrador: " + adminUsername);
                        } else {
                            System.out.println("Sesión cerrada para administrador: " + adminUsername);
                            // Remover sesiones cerradas
                            adminSessions.remove(adminUsername);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error procesando mensaje WebSocket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void handleClose(WsCloseContext ctx) {
        // Remover la sesión al desconectarse
        String sessionId = ctx.getSessionId();
        userSessions.values().removeIf(wsCtx -> wsCtx.getSessionId().equals(sessionId));
        adminSessions.values().removeIf(wsCtx -> wsCtx.getSessionId().equals(sessionId));
        System.out.println("WebSocket desconectado: " + sessionId);
    }
}