package asignacion.Services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.InsertOneResult;
import asignacion.Entidades.Estudiante;
import asignacion.Util.NoExisteEstudianteException;
import asignacion.Util.TablasMongo;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EstudianteServices {

    private static EstudianteServices instancia;
    private MongoDbConexion mongoDbConexion;

    /**
     * Constructor privado.
     */
    private EstudianteServices() {
        //
        mongoDbConexion = MongoDbConexion.getInstance();
        mongoDbConexion.getBaseDatos();

    }

    public static EstudianteServices getInstancia() {
        if (instancia == null) {
            instancia = new EstudianteServices();
        }
        return instancia;
    }

    public List<Estudiante> listarEstudiante() {
        List<Estudiante> lista = new ArrayList<>();

        //
        MongoCollection<Document> estudiantes = mongoDbConexion.getBaseDatos()
                .getCollection(TablasMongo.ESTUDIANTES.getValor());

        // Consultando todos los elementos.
        MongoCursor<Document> iterator = estudiantes.find().iterator();
        while (iterator.hasNext()) {

            // obteniendo el documento
            Document next = iterator.next();

            // Encapsulando la información
            Estudiante estudiante = new Estudiante();
            estudiante.setId(next.getObjectId("_id").toHexString());
            estudiante.setMatricula(next.getInteger("matricula"));
            estudiante.setNombre(next.getString("nombre"));
            estudiante.setCarrera(next.getString("carrera"));

            // Agregando a la lista.
            lista.add(estudiante);
        }
        // retornando...
        return lista;
    }

    public Estudiante getEstudiantePorMatricula(int matricula) {
        Estudiante estudiante = null;
        // Conexion a Mongo.
        MongoCollection<Document> estudiantes = mongoDbConexion.getBaseDatos()
                .getCollection(TablasMongo.ESTUDIANTES.getValor());

        //
        Document filtro = new Document("matricula", matricula);
        Document first = estudiantes.find(filtro).first();

        // si no fue encontrado retorna null.
        if (first != null) {
            estudiante = new Estudiante();
            estudiante.setId(first.getObjectId("_id").toHexString());
            estudiante.setMatricula(first.getInteger("matricula"));
            estudiante.setNombre(first.getString("nombre"));
            estudiante.setCarrera(first.getString("carrera"));
            System.out.println("Consultado: " + estudiante.toString());
        }

        // retornando.
        return estudiante;
    }

    public Estudiante crearEstudiante(@NotNull Estudiante estudiante) {
        if (getEstudiantePorMatricula(estudiante.getMatricula()) != null) {
            System.out.println("Estudiante registrado...");
            return null; // generar una excepcion...
        }

        //
        Document document = new Document("matricula", estudiante.getMatricula())
                .append("nombre", estudiante.getNombre())
                .append("carrera", estudiante.getCarrera());

        //
        MongoCollection<Document> estudiantes = mongoDbConexion.getBaseDatos()
                .getCollection(TablasMongo.ESTUDIANTES.getValor());
        //
        InsertOneResult insertOneResult = estudiantes.insertOne(document);
        //
        System.out.println(
                "Insertar: " + insertOneResult.getInsertedId() + ", Acknowledged:" + insertOneResult.wasAcknowledged());

        return estudiante;
    }

    public Estudiante actualizarEstudiante(@NotNull Estudiante estudiante) {
        Estudiante tmp = getEstudiantePorMatricula(estudiante.getMatricula());

        if (tmp == null) {// no existe, no puede se actualizado
            throw new NoExisteEstudianteException("No Existe el estudiante: " + estudiante.getMatricula());
        }

        // Actualizando Documento.
        MongoCollection<Document> estudiantes = mongoDbConexion.getBaseDatos()
                .getCollection(TablasMongo.ESTUDIANTES.getValor());

        //
        Document filtro = new Document("_id", new ObjectId(estudiante.getId()));
        //
        //
        Document document = new Document("matricula", estudiante.getMatricula())
                .append("nombre", estudiante.getNombre())
                .append("carrera", estudiante.getCarrera())
                .append("_id", new ObjectId(estudiante.getId()));
        //
        estudiantes.findOneAndUpdate(filtro, new Document("$set", document));

        return estudiante;
    }

    public boolean eliminandoEstudiante(int matricula) {
        //
        Estudiante estudiantePorMatricula = getEstudiantePorMatricula(matricula);
        // Actualizando Documento.
        MongoCollection<Document> estudiantes = mongoDbConexion.getBaseDatos()
                .getCollection(TablasMongo.ESTUDIANTES.getValor());
        //
        Document filtro = new Document("_id", new ObjectId(estudiantePorMatricula.getId()));
        //
        return estudiantes.findOneAndDelete(filtro) != null;
    }

}