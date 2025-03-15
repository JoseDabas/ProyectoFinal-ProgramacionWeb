package parcial.Service;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaQuery;
import java.util.List;

public class DataBaseServices<T> {
    private static EntityManagerFactory emf;
    private Class<T> clase;

    public DataBaseServices(Class<T> clase) {
        emf = Persistence.createEntityManagerFactory("MiUnidadPersistencia");
        this.clase = clase;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void insert(T entity) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        em.persist(entity);
        em.getTransaction().commit();
        em.close();
    }

    public void update(T entity) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        em.merge(entity);
        em.getTransaction().commit();
        em.close();
    }

    public void delete(Object id) {
        EntityManager em = getEntityManager();
        em.getTransaction().begin();
        T entity = em.find(clase, id);
        em.remove(entity);
        em.getTransaction().commit();
        em.close();
    }

    public T find(Object id) {
        EntityManager em = getEntityManager();
        T entity = em.find(clase, id);
        em.close();
        return entity;
    }

    public List<T> findAll() throws PersistenceException {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<T> criteria = em.getCriteriaBuilder().createQuery(clase);
            criteria.select(criteria.from(clase));
            List<T> entities = em.createQuery(criteria).getResultList();
            return entities;
        } finally {
            em.close();
        }
    }

    public void close() {
        emf.close();
    }
}