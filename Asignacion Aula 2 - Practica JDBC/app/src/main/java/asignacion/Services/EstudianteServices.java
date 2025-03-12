package asignacion.Services;

import asignacion.Entidades.Estudiante;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstudianteServices {

    private static EstudianteServices instancia;

    private EstudianteServices() {
    }

    public static EstudianteServices getInstancia() {
        if (instancia == null) {
            instancia = new EstudianteServices();
        }
        return instancia;
    }

    public void crearEstudiante(Estudiante estudiante) {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/AsignacionAula2", "sa", "");
                PreparedStatement stmt = conn
                        .prepareStatement("INSERT INTO ESTUDIANTE (MATRICULA, NOMBRE, CARRERA) VALUES (?, ?, ?)")) {

            stmt.setInt(1, estudiante.getMatricula());
            stmt.setString(2, estudiante.getNombre());
            stmt.setString(3, estudiante.getCarrera());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Estudiante> listarEstudiante() {
        List<Estudiante> estudiantes = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/AsignacionAula2", "sa", "");
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM ESTUDIANTE")) {

            while (rs.next()) {
                estudiantes
                        .add(new Estudiante(rs.getInt("MATRICULA"), rs.getString("NOMBRE"), rs.getString("CARRERA")));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return estudiantes;
    }

    public Estudiante getEstudiantePorMatricula(int matricula) {
        Estudiante estudiante = null;
        try (Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/AsignacionAula2", "sa", "");
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ESTUDIANTE WHERE MATRICULA = ?")) {

            stmt.setInt(1, matricula);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                estudiante = new Estudiante(rs.getInt("MATRICULA"), rs.getString("NOMBRE"), rs.getString("CARRERA"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return estudiante;
    }

    public void actualizarEstudiante(Estudiante estudiante) {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/AsignacionAula2", "sa", "");
                PreparedStatement stmt = conn
                        .prepareStatement("UPDATE ESTUDIANTE SET NOMBRE = ?, CARRERA = ? WHERE MATRICULA = ?")) {

            stmt.setString(1, estudiante.getNombre());
            stmt.setString(2, estudiante.getCarrera());
            stmt.setInt(3, estudiante.getMatricula());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void eliminarEstudiante(int matricula) {
        try (Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/AsignacionAula2", "sa", "");
                PreparedStatement stmt = conn.prepareStatement("DELETE FROM ESTUDIANTE WHERE MATRICULA = ?")) {

            stmt.setInt(1, matricula);

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
