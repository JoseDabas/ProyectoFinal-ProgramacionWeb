package practica.Entidades;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class Articulo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String titulo;
    private String cuerpo;

    @ManyToOne(optional = false)
    private Usuario autor;
    private Date fecha;

    @OneToMany(mappedBy = "articulo", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Comentario> listaComentarios;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "ARTICULO_ETIQUETA", joinColumns = @JoinColumn(name = "ARTICULOS_ID"), inverseJoinColumns = @JoinColumn(name = "LISTAETIQUETAS_ID"))
    private List<Etiqueta> listaEtiquetas;

    public Articulo(String titulo, String cuerpo, Usuario autor, Date fecha, List<Etiqueta> listaEtiquetas) {
        this.titulo = titulo;
        this.cuerpo = cuerpo;
        this.autor = autor;
        this.fecha = fecha;
        this.listaComentarios = new ArrayList<>();
        this.listaEtiquetas = listaEtiquetas;
    }

    public Articulo() {

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }

    public Usuario getAutor() {
        return autor;
    }

    public void setAutor(Usuario autor) {
        this.autor = autor;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public List<Comentario> getListaComentarios() {
        return listaComentarios;
    }

    public void setListaComentarios(List<Comentario> listaComentarios) {
        this.listaComentarios = listaComentarios;
    }

    public List<Etiqueta> getListaEtiquetas() {
        return listaEtiquetas;
    }

    public void setListaEtiquetas(List<Etiqueta> listaEtiquetas) {
        this.listaEtiquetas = listaEtiquetas;
    }

    public void agregarComentario(Comentario comentario) {
        comentario.setArticulo(this);
        this.listaComentarios.add(comentario);
    }
}
