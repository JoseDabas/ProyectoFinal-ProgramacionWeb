package parcial.Service;

import parcial.Class.Registro;

public class RegistroServices extends DataBaseServices<Registro> {
    private static RegistroServices instancia;

    public RegistroServices(Class<Registro> clase) {
        super(clase);
    }

    public static RegistroServices getInstancia() {
        if (instancia == null) {
            instancia = new RegistroServices(Registro.class);
        }
        return instancia;
    }
}