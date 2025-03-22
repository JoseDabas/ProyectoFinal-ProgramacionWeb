package practica.Servicios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import practica.Entidades.ChatMensaje;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatServices extends BaseDatosServices<ChatMensaje> {
    private static ChatServices instance = null;

    private ChatServices() {
        super(ChatMensaje.class);
    }

    public static ChatServices getInstance() {
        if (instance == null) {
            instance = new ChatServices();
        }
        return instance;
    }

    public List<ChatMensaje> findBySesionId(String sesionId) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<ChatMensaje> query = em.createQuery(
                    "SELECT c FROM ChatMensaje c WHERE c.sesionId = :sesionId ORDER BY c.fecha ASC",
                    ChatMensaje.class);
            query.setParameter("sesionId", sesionId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    // Método para obtener sesiones activas con nombres de visitantes
    public List<Map<String, String>> findActiveSessionsInfo() {
        EntityManager em = getEntityManager();
        try {
            // Consultamos el ID de sesión y el nombre del visitante
            TypedQuery<Object[]> query = em.createQuery(
                    "SELECT DISTINCT c.sesionId, c.nombreVisitante FROM ChatMensaje c GROUP BY c.sesionId, c.nombreVisitante ORDER BY c.sesionId",
                    Object[].class);

            List<Object[]> results = query.getResultList();
            List<Map<String, String>> sessionInfos = new ArrayList<>();

            for (Object[] result : results) {
                Map<String, String> info = new HashMap<>();
                info.put("sessionId", (String) result[0]);
                info.put("visitorName", (String) result[1]);
                sessionInfos.add(info);
            }

            return sessionInfos;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    // Mantener el método original para compatibilidad
    public List<String> findActiveSessions() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<String> query = em.createQuery(
                    "SELECT DISTINCT c.sesionId FROM ChatMensaje c",
                    String.class);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }
}