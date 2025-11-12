package com.biblioteca.libreria.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;

    private String nombre;
    private String email;
    private String contraseña;
    private String tipoUsuario;
    private LocalDateTime fechaRegistro;

    @OneToMany(mappedBy = "usuario")
    private List<Compra> compras;

    @ManyToMany(mappedBy = "usuariosBiblioteca")
    private List<Libro> bibliotecaPersonal;

    // Getters y setters
}
