package practica.Servicios;

import practica.Entidades.Etiqueta;

public class EtiquetaServices extends BaseDatosServices<Etiqueta> {
    private static EtiquetaServices instance = null;

    private EtiquetaServices() {
        super(Etiqueta.class);
    }

    public static EtiquetaServices getInstance() {
        if (instance == null) {
            instance = new EtiquetaServices();
        }
        return instance;
    }

}