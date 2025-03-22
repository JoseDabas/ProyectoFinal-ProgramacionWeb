package asignacion.Services;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import io.github.cdimascio.dotenv.Dotenv;
import org.bson.Document;

public class MongoDbConexion {

    private static MongoDbConexion instance;
    private MongoClient mongoClient;
    private String DB_NOMBRE;
    private Dotenv dotenv;

    private MongoDbConexion() {
        // Cargar variables desde .env
        try {
            dotenv = Dotenv.configure()
                    .ignoreIfMissing() // No lanzar error si no existe el archivo .env
                    .load();
            System.out.println("Archivo .env cargado correctamente");
        } catch (Exception e) {
            System.err.println("Error al cargar el archivo .env: " + e.getMessage());
            dotenv = null;
        }
    }

    public static MongoDbConexion getInstance() {
        if (instance == null) {
            instance = new MongoDbConexion();
        }
        return instance;
    }

    public MongoDatabase getBaseDatos() {
        if (mongoClient == null) {
            // Intentar obtener variables desde diferentes fuentes en este orden:
            // 1. Variables de entorno del sistema
            // 2. Archivo .env
            // 3. Valores predeterminados

            String URL_MONGODB;
            ProcessBuilder processBuilder = new ProcessBuilder();

            // Intentar obtener desde variables de sistema
            String systemUrlMongo = processBuilder.environment().get("URL_MONGO");

            // Si no está en variables de sistema, intentar desde .env
            if (systemUrlMongo != null && !systemUrlMongo.isEmpty()) {
                URL_MONGODB = systemUrlMongo;
                System.out.println("Usando URL_MONGO desde variables del sistema");
            } else if (dotenv != null && dotenv.get("URL_MONGO") != null) {
                URL_MONGODB = dotenv.get("URL_MONGO");
                System.out.println("Usando URL_MONGO desde archivo .env");
            } else {
                URL_MONGODB = "mongodb://localhost:27017";
                System.out.println("Usando URL_MONGO predeterminada: " + URL_MONGODB);
            }

            // Misma lógica para DB_NOMBRE
            String systemDbNombre = processBuilder.environment().get("DB_NOMBRE");

            if (systemDbNombre != null && !systemDbNombre.isEmpty()) {
                DB_NOMBRE = systemDbNombre;
                System.out.println("Usando DB_NOMBRE desde variables del sistema");
            } else if (dotenv != null && dotenv.get("DB_NOMBRE") != null) {
                DB_NOMBRE = dotenv.get("DB_NOMBRE");
                System.out.println("Usando DB_NOMBRE desde archivo .env");
            } else {
                DB_NOMBRE = "estudiantes_db";
                System.out.println("Usando DB_NOMBRE predeterminado: " + DB_NOMBRE);
            }

            try {
                mongoClient = MongoClients.create(URL_MONGODB);
                System.out.println("Intentando conectar a MongoDB en: " + URL_MONGODB);
            } catch (Exception e) {
                System.err.println("Error al conectar a MongoDB: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Retomando la conexión
        MongoDatabase database = mongoClient.getDatabase(DB_NOMBRE);
        try {
            database.runCommand(new Document("ping", 1));
            System.out.println("Conectado exitosamente a la base de datos MongoDB: " + DB_NOMBRE);
        } catch (Exception e) {
            System.err.println("Error al hacer ping a la base de datos: " + e.getMessage());
        }

        return database;
    }

    /**
     * Método para probar la conexión a MongoDB
     * 
     * @return true si la conexión es exitosa, false en caso contrario
     */
    public boolean probarConexion() {
        try {
            getBaseDatos().runCommand(new Document("ping", 1));
            return true;
        } catch (Exception e) {
            System.err.println("Error al conectar a MongoDB: " + e.getMessage());
            return false;
        }
    }

    public void cerrar() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
            System.out.println("Conexión a MongoDB cerrada");
        }
    }
}