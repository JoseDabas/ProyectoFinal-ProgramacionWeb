package practica.Encapsulaciones;

public class Etiqueta {
    long id;
    String etiqueta;

    public Etiqueta(long id, String etiqueta) {
        this.id = id;
        this.etiqueta = etiqueta;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEtiqueta() {
        return this.etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }
}
