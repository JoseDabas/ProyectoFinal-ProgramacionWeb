package practica.Servicios;

import practica.Entidades.Foto;

public class FotoServices extends BaseDatosServices<Foto> {
    private static FotoServices instance = null;

    private FotoServices() {
        super(Foto.class);
    }

    public static FotoServices getInstance() {
        if (instance == null) {
            instance = new FotoServices();
        }
        return instance;
    }
}