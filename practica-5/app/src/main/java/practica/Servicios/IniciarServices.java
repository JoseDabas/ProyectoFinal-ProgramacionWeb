package practica.Servicios;

import java.sql.SQLException;

import org.h2.tools.Server;

public class IniciarServices {

    private static IniciarServices instance = null;

    private IniciarServices() {
    }

    public static IniciarServices getInstance() {
        if (instance == null) {
            instance = new IniciarServices();
        }
        return instance;
    }

    public void startDb() {
        try {
            Server.createTcpServer("-tcpPort",
                    "9092",
                    "-tcpAllowOthers",
                    "-tcpDaemon",
                    "-ifNotExists").start();
            String status = Server.createWebServer("-trace", "-webPort", "0").start().getStatus();
            System.out.println("Status Web: " + status);
        } catch (SQLException ex) {
            System.out.println("Problema con la base de datos: " + ex.getMessage());
        }
    }

}
