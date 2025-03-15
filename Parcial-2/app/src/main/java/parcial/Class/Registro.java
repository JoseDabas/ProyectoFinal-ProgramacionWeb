package parcial.Class;

import jakarta.persistence.*;

@Entity
public class Registro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @OneToOne
    private Respuesta formulario;
    @OneToOne
    private Usuario usuario;

    public Registro(Respuesta formulario, Usuario usuario) {
        this.formulario = formulario;
        this.usuario = usuario;
    }

    public Registro() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Respuesta getFormulario() {
        return formulario;
    }

    public void setFormulario(Respuesta formulario) {
        this.formulario = formulario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

}
