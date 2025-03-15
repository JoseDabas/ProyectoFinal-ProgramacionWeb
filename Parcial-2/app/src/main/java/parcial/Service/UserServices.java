package parcial.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import parcial.Class.Usuario;

import java.util.List;

public class UserServices extends DataBaseServices<Usuario> {
    private static UserServices instancia;

    public UserServices() {
        super(Usuario.class);
    }

    public static UserServices getInstancia() {
        if (instancia == null) {
            instancia = new UserServices();
        }
        return instancia;
    }

    public Usuario findUserByUsername(String username) {
        Usuario user;
        user = this.find(username);

        return user;
    }

    public List<Usuario> findAll(int pageNumber, int pageSize) throws PersistenceException {
        if (pageNumber <= 0) {
            pageNumber = 1;
        }
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery<Usuario> criteriaQuery = em.getCriteriaBuilder().createQuery(Usuario.class);
            criteriaQuery.select(criteriaQuery.from(Usuario.class));
            TypedQuery<Usuario> query = em.createQuery(criteriaQuery);
            query.setFirstResult((pageNumber - 1) * pageSize);
            query.setMaxResults(pageSize);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}
