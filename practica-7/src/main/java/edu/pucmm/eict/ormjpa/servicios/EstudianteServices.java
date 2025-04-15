package edu.pucmm.eict.ormjpa.servicios;

import edu.pucmm.eict.ormjpa.entidades.Estudiante;
import edu.pucmm.eict.ormjpa.entidades.Profesor;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servicio para manejar operaciones CRUD de Estudiantes
 * Created by vacax on 03/06/16.
 * Actualizado para gRPC
 */
public class EstudianteServices extends GestionDb<Estudiante> {

    private static final Logger logger = Logger.getLogger(EstudianteServices.class.getName());
    private static EstudianteServices instancia;

    private EstudianteServices() {
        super(Estudiante.class);
    }

    public static EstudianteServices getInstancia() {
        if (instancia == null) {
            instancia = new EstudianteServices();
        }
        return instancia;
    }

    /**
     * Busca estudiantes por nombre
     * 
     * @param nombre El nombre a buscar
     * @return Lista de estudiantes que coinciden
     */
    public List<Estudiante> findAllByNombre(String nombre) {
        EntityManager em = getEntityManager();
        List<Estudiante> lista = new ArrayList<>();
        try {
            Query query = em.createQuery("select e from Estudiante e where e.nombre like :nombre");
            query.setParameter("nombre", nombre + "%");
            lista = query.getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al buscar estudiantes por nombre", e);
        } finally {
            em.close();
        }
        return lista;
    }

    /**
     * Consulta estudiantes usando SQL nativo
     * 
     * @return Lista de estudiantes
     */
    public List<Estudiante> consultaNativa() {
        EntityManager em = getEntityManager();
        List<Estudiante> lista = new ArrayList<>();
        try {
            Query query = em.createNativeQuery("select * from estudiante", Estudiante.class);
            lista = query.getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error en consulta nativa", e);
        } finally {
            em.close();
        }
        return lista;
    }

    /**
     * Sobreescribe el método para manejar excepciones gRPC
     */
    @Override
    public Estudiante crear(edu.pucmm.eict.ormjpa.modelos.Estudiante estudiante) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(estudiante);
            em.getTransaction().commit();
            return estudiante;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "Error al crear estudiante", e);
            throw new RuntimeException("Error al crear estudiante: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Sobreescribe el método para manejar excepciones gRPC
     */
    @Override
    public Estudiante editar(Estudiante estudiante) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Estudiante resultado = em.merge(estudiante);
            em.getTransaction().commit();
            return resultado;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "Error al editar estudiante", e);
            throw new RuntimeException("Error al editar estudiante: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Sobreescribe el método para manejar excepciones gRPC
     */
    @Override
    public boolean eliminar(Object id) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            Estudiante estudiante = em.find(Estudiante.class, id);
            if (estudiante == null) {
                throw new EntityNotFoundException("Estudiante con ID " + id + " no encontrado");
            }
            em.remove(estudiante);
            em.getTransaction().commit();
            return true;
        } catch (EntityNotFoundException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.WARNING, e.getMessage());
            return false;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "Error al eliminar estudiante", e);
            throw new RuntimeException("Error al eliminar estudiante: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    /**
     * Sobreescribe el método para manejar excepciones gRPC
     */
    @Override
    public Estudiante find(Object id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Estudiante.class, id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al buscar estudiante", e);
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Sobreescribe el método para manejar excepciones gRPC
     */
    @Override
    public List<Estudiante> findAll() {
        EntityManager em = getEntityManager();
        try {
            Query query = em.createQuery("select e from Estudiante e");
            return query.getResultList();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al listar estudiantes", e);
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    /**
     * Prueba de actualización de un estudiante
     */
    public void pruebaActualizacion() {
        EntityManager em = getEntityManager();
        try {
            // Creación
            Estudiante est = new Estudiante(2222, "Nombre");
            em.getTransaction().begin();
            em.persist(est);
            em.flush();
            em.getTransaction().commit();

            // Primera actualización
            em.getTransaction().begin();
            est.setNombre("Otro Nombre");
            em.flush();
            em.getTransaction().commit();

            // Segunda actualización
            em.getTransaction().begin();
            est.setNombre("Nuevamente otro nombre...");
            em.flush();
            em.getTransaction().commit();
            em.close();

            // Actualización después de cerrar
            est.setNombre("Cambiando el objeto...");
            em = getEntityManager();
            em.getTransaction().begin();
            em.merge(est);
            em.getTransaction().commit();

            // Consultando un profesor
            Profesor p = em.find(Profesor.class, 1);
            if (p != null) {
                System.out.println("El nombre del profesor: " + p.getNombre());
            } else {
                System.out.println("Profesor no encontrado");
            }
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            logger.log(Level.SEVERE, "Error en prueba de actualización", e);
        } finally {
            em.close();
        }
    }
}