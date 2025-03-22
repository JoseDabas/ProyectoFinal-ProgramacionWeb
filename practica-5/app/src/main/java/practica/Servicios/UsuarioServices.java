package practica.Servicios;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import practica.Entidades.Usuario;
//import javax.persistence.NoResultException;

import java.util.List;

public class UsuarioServices extends BaseDatosServices<Usuario> {
    private static UsuarioServices instance;

    private UsuarioServices() {
        super(Usuario.class);
    }

    public static UsuarioServices getInstance() {
        if (instance == null) {
            instance = new UsuarioServices();
        }
        return instance;
    }

    public Usuario findUserByUsername(String username) {
        Usuario user = null;
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

            List<Usuario> usuarios = query.getResultList();
            System.out.println("Usuarios encontrados: " + usuarios.size());

            if (usuarios.isEmpty()) {
                System.out.println("La consulta no devolvió resultados.");
            }

            return usuarios;
        } catch (Exception e) {
            e.printStackTrace();
            throw new PersistenceException("Error al obtener la lista de usuarios.", e);
        } finally {
            em.close();
        }
    }

    public boolean autenticar(String username, String password) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Usuario> query = em.createQuery(
                    "SELECT u FROM Usuario u WHERE u.username = :username AND u.password = :password",
                    Usuario.class);
            query.setParameter("username", username);
            query.setParameter("password", password);

            Usuario user = query.getSingleResult();
            return user != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            em.close();
        }
    }
}