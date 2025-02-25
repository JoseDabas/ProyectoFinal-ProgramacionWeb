package practica;

import io.github.cdimascio.dotenv.Dotenv;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class ConexionDataBase {

    private static final Dotenv dotenv = Dotenv.load();

    public static DataSource getDataSource() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUrl(dotenv.get(
                "JDBC_DATABASE_URL"));
        ds.setUser(dotenv.get("DB_USER"));
        ds.setPassword(dotenv.get("DB_PASSWORD "));
        return ds;
    }

    public static Connection getConnection() throws SQLException {
        return getDataSource().getConnection();
    }

}
