package com.biblioteca.libreria.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "libro")
public class Libro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idLibro;

    private String titulo;
    private String autor;
    private String descripcion;
    private Double precio;
    private LocalDate fechaPublicacion;
    private String isbn;
    private String imagen;

    // NUEVO → Disponible para venta (true/false)
    private boolean enVenta = false;

    // NUEVO → Stock disponible
    private Integer stock = 0;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @ManyToMany
    @JoinTable(
        name = "biblioteca_personal",
        joinColumns = @JoinColumn(name = "id_libro"),
        inverseJoinColumns = @JoinColumn(name = "id_usuario")
    )
    private List<Usuario> usuariosBiblioteca;

    @OneToMany(mappedBy = "libro")
    private List<LibroHasCompra> libroCompras;

    // Getters y Setters

    public Integer getIdLibro() { return idLibro; }
    public void setIdLibro(Integer idLibro) { this.idLibro = idLibro; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public LocalDate getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(LocalDate fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }

    public boolean getEnVenta() 
    { return enVenta; }
    public void setEnVenta(boolean enVenta) { this.enVenta = enVenta; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public List<Usuario> getUsuariosBiblioteca() { return usuariosBiblioteca; }
    public void setUsuariosBiblioteca(List<Usuario> usuariosBiblioteca) { this.usuariosBiblioteca = usuariosBiblioteca; }

    public List<LibroHasCompra> getLibroCompras() { return libroCompras; }
    public void setLibroCompras(List<LibroHasCompra> libroCompras) { this.libroCompras = libroCompras; }
}
