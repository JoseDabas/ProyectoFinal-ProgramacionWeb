package practica.Entidades;

import java.io.Serializable;

import jakarta.persistence.*;

@Entity
public class Usuario implements Serializable {
    @Id
    private String username;
    private String nombre;
    private String password;
    boolean administrator;
    boolean autor;
    @OneToOne
    @JoinColumn(name = "foto_id", referencedColumnName = "id")
    private Foto foto;

    public Usuario(String username, String nombre, String password, boolean administrator, boolean autor) {
        this.username = username;
        this.nombre = nombre;
        this.password = password;
        this.administrator = administrator;
        this.autor = autor;
    }

    public Usuario() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }

    public boolean isAutor() {
        return autor;
    }

    public void setAutor(boolean autor) {
        this.autor = autor;
    }

    public Foto getFoto() {
        return foto;
    }

    public void setFoto(Foto foto) {
        this.foto = foto;
    }
}
