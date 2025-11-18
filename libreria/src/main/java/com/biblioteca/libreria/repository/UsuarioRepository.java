package com.biblioteca.libreria.repository;

import com.biblioteca.libreria.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
}
