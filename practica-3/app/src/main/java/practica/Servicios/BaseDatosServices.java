package practica.Servicios;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaQuery;
import java.util.List;

public class BaseDatosServices<T> {
    private static EntityManagerFactory emf;
    private Class<T> claseEntidad;

    public BaseDatosServices(Class<T> claseEntidad) {
        this.claseEntidad = claseEntidad;
        emf = Persistence.createEntityManagerFactory("MiUnidadPersistencia");
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void close() {
        emf.close();
    }

    public T create(T entity) throws PersistenceException, IllegalArgumentException {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            System.out.println("Entidad persistida: " + entity);
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        return entity;
    }

    public T edit(T entity) throws PersistenceException {
        EntityManager em = getEntityManager();
        T managedEntity = null;
        try {
            em.getTransaction().begin();
            managedEntity = em.merge(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        return managedEntity;
    }

    public boolean remove(T entity) throws PersistenceException {
        boolean ok = false;
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.remove(em.merge(entity));
            em.getTransaction().commit();
            ok = true;
        } catch (Exception e) {
            em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
        return ok;
    }

    public T find(Object id) throws PersistenceException {
        EntityManager em = getEntityManager();
        try {
            return em.find(claseEntidad, id);
        } finally {
            em.close();
        }
    }

    public List<T> findAll() throws PersistenceException {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<T> criteriaQuery = em.getCriteriaBuilder().createQuery(claseEntidad);
            criteriaQuery.select(criteriaQuery.from(claseEntidad));
            return em.createQuery(criteriaQuery).getResultList();
        } finally {
            em.close();
        }
    }
}
