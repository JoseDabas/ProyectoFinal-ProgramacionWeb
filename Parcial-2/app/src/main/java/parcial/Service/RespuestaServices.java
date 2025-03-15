package parcial.Service;

import parcial.Class.Respuesta;

public class RespuestaServices extends DataBaseServices<Respuesta> {
    private static RespuestaServices instancia;

    public RespuestaServices(Class<Respuesta> clase) {
        super(clase);
    }

    public static RespuestaServices getInstancia() {
        if (instancia == null) {
            instancia = new RespuestaServices(Respuesta.class);
        }
        return instancia;
    }
}
