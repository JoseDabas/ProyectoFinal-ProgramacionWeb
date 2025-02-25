package practica.Entidades;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.*;

@Entity
public class Etiqueta implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    String etiqueta;
    @ManyToMany(mappedBy = "listaEtiquetas")
    private List<Articulo> articulos;

    public Etiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public Etiqueta() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public void setEtiqueta(String etiqueta) {
        this.etiqueta = etiqueta;
    }
}
