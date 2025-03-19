package asignacion.Services;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class MongoDbConexion {

    private static MongoDbConexion instance;
    private MongoClient mongoClient;
    private String DB_NOMBRE;

    private MongoDbConexion() {

    }

    public static MongoDbConexion getInstance() {
        if (instance == null) {
            instance = new MongoDbConexion();
        }
        return instance;
    }

    public MongoDatabase getBaseDatos() {

        if (mongoClient == null) {
            ProcessBuilder processBuilder = new ProcessBuilder();
            String URL_MONGODB = processBuilder.environment().get("URL_MONGO");
            DB_NOMBRE = processBuilder.environment().get("DB_NOMBRE");
            mongoClient = MongoClients.create(URL_MONGODB);
        }

        // Retomando la conexión
        MongoDatabase database = mongoClient.getDatabase(DB_NOMBRE);
        database.runCommand(new Document("ping", 1));
        System.out.println("Pinged your deployment. You successfully connected to MongoDB!");

        //
        return database;

    }

    public void cerrar() {
        mongoClient.close();
        mongoClient = null;
    }

}
