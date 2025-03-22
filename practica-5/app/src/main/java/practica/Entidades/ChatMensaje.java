package practica.Entidades;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
public class ChatMensaje implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String mensaje;
    private String nombreVisitante;
    private boolean esAdministrador;

    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    private String sesionId; // Para identificar conversaciones

    @ManyToOne(fetch = FetchType.EAGER)
    private Usuario administrador; // El administrador/autor que responde

    public ChatMensaje() {
        this.fecha = new Date();
    }

    public ChatMensaje(String mensaje, String nombreVisitante, boolean esAdministrador, String sesionId) {
        this.mensaje = mensaje;
        this.nombreVisitante = nombreVisitante;
        this.esAdministrador = esAdministrador;
        this.fecha = new Date();
        this.sesionId = sesionId;
    }

    // Getters y setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getNombreVisitante() {
        return nombreVisitante;
    }

    public void setNombreVisitante(String nombreVisitante) {
        this.nombreVisitante = nombreVisitante;
    }

    public boolean isEsAdministrador() {
        return esAdministrador;
    }

    public void setEsAdministrador(boolean esAdministrador) {
        this.esAdministrador = esAdministrador;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getSesionId() {
        return sesionId;
    }

    public void setSesionId(String sesionId) {
        this.sesionId = sesionId;
    }

    public Usuario getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Usuario administrador) {
        this.administrador = administrador;
    }
}