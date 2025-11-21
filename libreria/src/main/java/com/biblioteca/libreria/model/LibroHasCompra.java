package com.biblioteca.libreria.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "libro_has_compra")
@IdClass(LibroHasCompraId.class)
public class LibroHasCompra implements Serializable {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_libro")
    private Libro libro;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_compra")
    private Compra compra;

    private Integer cantidad;
    private Double precio;

    // Getters y setters
}
