package com.biblioteca.libreria.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCategoria;

    private String nombreCategoria;
    private String descripcion;

    @OneToMany(mappedBy = "categoria")
    private List<Libro> libros;

    // Getters y setters
}
