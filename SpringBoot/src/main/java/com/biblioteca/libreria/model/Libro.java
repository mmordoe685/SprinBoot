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

    // Getters y setters
}
