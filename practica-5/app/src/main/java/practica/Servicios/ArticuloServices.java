package practica.Servicios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import org.hibernate.Hibernate;
import practica.Entidades.Articulo;
import java.util.List;

public class ArticuloServices extends BaseDatosServices<Articulo> {
    private static ArticuloServices instance = null;

    private ArticuloServices() {
        super(Articulo.class);
    }

    public static ArticuloServices getInstance() {
        if (instance == null) {
            instance = new ArticuloServices();
        }
        return instance;
    }

    public List<Articulo> findAllRecent() {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Articulo> query = em.createQuery(
                    "SELECT a FROM Articulo a JOIN FETCH a.listaEtiquetas ORDER BY a.fecha DESC", Articulo.class);
            return query.getResultList();
        } catch (PersistenceException e) {
            e.printStackTrace(); // Loguea el error si es necesario
            throw e; // Rethrow para que el controlador lo maneje
        } finally {
            em.close();
        }
    }

    public List<Articulo> findAllRecentPag(int pageNumber, int pageSize) throws PersistenceException {
        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Articulo> query = em.createQuery(
                    "SELECT a FROM Articulo a JOIN FETCH a.listaEtiquetas ORDER BY a.fecha DESC", Articulo.class);
            query.setFirstResult((pageNumber - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } catch (PersistenceException e) {
            e.printStackTrace(); // Loguea el error si es necesario
            throw e; // Rethrow para que el controlador lo maneje
        } finally {
            em.close();
        }
    }

    public List<Articulo> findAllByTag(String tag) {
        EntityManager em = getEntityManager();
        TypedQuery<Articulo> query = em.createQuery(
                "SELECT a FROM Articulo a JOIN FETCH a.listaEtiquetas e WHERE e.etiqueta = :tag ORDER BY a.fecha DESC",
                Articulo.class);
        query.setParameter("tag", tag);
        return query.getResultList();
    }

    public List<Articulo> findAllByTagPag(String tag, int pageNumber, int pageSize) throws PersistenceException {
        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Articulo> query = em.createQuery(
                    "SELECT a FROM Articulo a JOIN FETCH a.listaEtiquetas e WHERE e.etiqueta = :tag ORDER BY a.fecha DESC",
                    Articulo.class);
            query.setParameter("tag", tag);
            query.setFirstResult((pageNumber - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } catch (PersistenceException e) {
            e.printStackTrace(); // Loguea el error si es necesario
            throw e; // Rethrow para que el controlador lo maneje
        } finally {
            em.close();
        }
    }

    public Articulo buscar(long id) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Articulo> query = em.createQuery(
                    "SELECT a FROM Articulo a JOIN FETCH a.listaEtiquetas WHERE a.id = :id", Articulo.class);
            query.setParameter("id", id);
            Articulo articulo = query.getSingleResult();
            Hibernate.initialize(articulo.getListaComentarios()); // Inicializar los comentarios
            return articulo;
        } catch (PersistenceException e) {
            e.printStackTrace(); // Loguea el error si es necesario
            throw e; // Rethrow para que el controlador lo maneje
        } finally {
            em.close();
        }
    }

    public Object getSessionFactory() {
        throw new UnsupportedOperationException("Unimplemented method 'getSessionFactory'");
    }

}
