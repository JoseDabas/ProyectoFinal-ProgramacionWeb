package proyecto.services;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import dev.morphia.query.Query;
import dev.morphia.query.filters.Filters;
import org.bson.types.ObjectId;
import proyecto.util.DatosEstaticos;

public class MongoServices<T> {
    private Class<T> claseEntidad;
    private MongoClient cliente = MongoDbConexion.getMongoClient();
    // private MorphiaConfig config = new ManualMorphiaConfig(); //por si quieren
    // configurar

    public MongoServices(Class<T> claseEntidad) {
        this.claseEntidad = claseEntidad;
    }

    protected Datastore getConexionMorphia() {
        return Morphia.createDatastore(cliente, MongoDbConexion.getNombreBaseDatos());
    }

    public void crear(T entidad) {

        Datastore datastore = getConexionMorphia();
        try {
            datastore.save(entidad);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Query<T> find() {
        Datastore datastore = getConexionMorphia();
        Query<T> query = datastore.find(claseEntidad);
        return query;
    }

    public T findOne(String campo, String valor) {
        Datastore datastore = getConexionMorphia();
        Query<T> query = datastore.find(claseEntidad).filter(Filters.eq(campo, valor));
        return query.first();
    }

    public T findByID(String id) {
        Datastore datastore = getConexionMorphia();
        Query<T> query = datastore.find(claseEntidad).filter(Filters.eq("_id", new ObjectId(id)));
        return query.first();
    }

    public Query<T> findByField(String campo, String valor) {
        Datastore datastore = getConexionMorphia();
        Query<T> query = datastore.find(claseEntidad).filter(Filters.eq(campo, valor));
        return query;
    }

    public void delete(String campo, String valor) {
        Datastore datastore = getConexionMorphia();
        datastore.find(claseEntidad).filter(Filters.eq(campo, valor)).delete();
    }

    public void deleteById(String id) {
        Datastore datastore = getConexionMorphia();
        datastore.find(claseEntidad).filter(Filters.eq("_id", new ObjectId(id))).delete();

    }

    public void update(T entidad) {
        Datastore datastore = getConexionMorphia();
        datastore.save(entidad);
    }

    /**
     * Clase interna para realizar la conexión de a la base de datos.
     */
    private static class MongoDbConexion {

        private static MongoClient mongoClient;
        private static String nombreBaseDatos;

        public static MongoClient getMongoClient() {
            if (mongoClient == null) {
                // Usar System.getProperty en lugar de ProcessBuilder
                String URL_MONGODB = System.getProperty(DatosEstaticos.URL_MONGO.getValor());
                nombreBaseDatos = System.getProperty(DatosEstaticos.DB_NOMBRE.getValor());

                if (URL_MONGODB == null || URL_MONGODB.isEmpty()) {
                    URL_MONGODB = "mongodb://localhost:27017";
                }

                mongoClient = MongoClients.create(URL_MONGODB);
            }
            return mongoClient;
        }

        public static String getNombreBaseDatos() {
            return nombreBaseDatos;
        }
    }
}
