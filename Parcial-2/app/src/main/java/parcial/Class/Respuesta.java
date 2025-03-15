package parcial.Class;

import jakarta.persistence.*;

@Entity
public class Respuesta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private String sector;
    private String nivelEscolar;
    @ManyToOne
    private Usuario usuario;
    private double latitud;
    private double longitud;

    public Respuesta(String nombre, String sector, String nivelEscolar, Usuario usuario, double latitud,
            double longitud) {
        this.nombre = nombre;
        this.sector = sector;
        this.nivelEscolar = nivelEscolar;
        this.usuario = usuario;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public Respuesta() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getNivelEscolar() {
        return nivelEscolar;
    }

    public void setNivelEscolar(String nivelEscolar) {
        this.nivelEscolar = nivelEscolar;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}