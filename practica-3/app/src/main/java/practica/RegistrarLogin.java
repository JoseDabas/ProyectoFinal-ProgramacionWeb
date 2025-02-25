package practica;

import java.sql.Connection;
//import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
//import io.github.cdimascio.dotenv.Dotenv;

import org.postgresql.ds.PGSimpleDataSource;

public class RegistrarLogin {

    private static PGSimpleDataSource dataSource;

    static {
        dataSource = new PGSimpleDataSource();
        dataSource.setUrl(System.getenv("JDBC_DATABASE_URL"));
        dataSource.setUser(System.getenv("DB_USER"));
        dataSource.setPassword(System.getenv("DB_PASSWORD"));
    }

    public static void GuardarLogin(String username) {

        String query = "INSERT INTO log_login (username, login_time) VALUES (?, ?)";

        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {

            System.out.println("Conexión exitosa a CockroachDB");

            stmt.setString(1, username);
            System.out.println("Ejecutando: " + query + " con username=" + username);

            stmt.setObject(2, LocalDateTime.now());

            stmt.executeUpdate();

            System.out.println("Inserción exitosa en log_login");

        } catch (SQLException e) {
            System.err.println("Error al insertar en log_login: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
