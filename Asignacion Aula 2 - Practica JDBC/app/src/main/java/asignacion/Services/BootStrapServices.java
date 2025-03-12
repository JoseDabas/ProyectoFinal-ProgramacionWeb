package asignacion.Services;

import org.h2.tools.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by vacax on 07/06/17.
 */
public class BootStrapServices {

    private static BootStrapServices instancia;

    private BootStrapServices() {

    }

    public static BootStrapServices getInstancia() {
        if (instancia == null) {
            instancia = new BootStrapServices();
        }
        return instancia;
    }

    public void startDb() {
        try {
            // Modo servidor H2.
            Server.createTcpServer("-tcpPort",
                    "9092",
                    "-tcpAllowOthers",
                    "-tcpDaemon",
                    "-ifNotExists").start();
            // Abriendo el cliente web. El valor 0 representa puerto aleatorio.
            String status = Server.createWebServer("-trace", "-webPort", "0").start().getStatus();
            //
            System.out.println("Status Web: " + status);
        } catch (SQLException ex) {
            System.out.println("Problema con la base de datos: " + ex.getMessage());
        }
    }

    private void crearTablas() {
        String sql = "CREATE TABLE IF NOT EXISTS ESTUDIANTE\n" +
                "(MATRICULA INT PRIMARY KEY NOT NULL,\n" +
                " NOMBRE TEXT NOT NULL, \n" +
                " CARRERA TEXT NOT NULL);";

        try (Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/AsignacionAula2", "sa", "");
                Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void init() {
        startDb();
        crearTablas();
    }
}
