package edu.pucmm.eict.ormjpa.servicios;

import edu.pucmm.eict.ormjpa.modelos.Foto;
import edu.pucmm.eict.ormjpa.modelos.Profesor;

import java.util.List;

/**
 *
 */
public class FotoServices extends GestionDb<Foto> {

    private static FotoServices instancia;

    private FotoServices() {
        super(Foto.class);
    }

    public static FotoServices getInstancia() {
        if (instancia == null) {
            instancia = new FotoServices();
        }
        return instancia;
    }

}