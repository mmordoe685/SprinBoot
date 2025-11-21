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

    @Column(unique = true, nullable = false)
    private String email;
    
    private String contrasena;
    private String tipoUsuario;
    private LocalDateTime fechaRegistro;

    private String fotoUrl;
    private String pais;

    @OneToMany(mappedBy = "usuario")
    private List<Compra> compras;

    @ManyToMany(mappedBy = "usuariosBiblioteca")
    private List<Libro> bibliotecaPersonal;

    // =============================
    //       GETTERS & SETTERS
    // =============================

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }


    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public List<Compra> getCompras() {
        return compras;
    }

    public void setCompras(List<Compra> compras) {
        this.compras = compras;
    }

    public List<Libro> getBibliotecaPersonal() {
        return bibliotecaPersonal;
    }

    public void setBibliotecaPersonal(List<Libro> bibliotecaPersonal) {
        this.bibliotecaPersonal = bibliotecaPersonal;
    }
}
