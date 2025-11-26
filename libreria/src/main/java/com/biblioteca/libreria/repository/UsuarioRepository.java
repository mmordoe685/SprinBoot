package com.biblioteca.libreria.repository;

import com.biblioteca.libreria.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByEmail(String email);

    // 👇 nombre correcto para que Spring Data lo genere
    boolean existsByEmail(String email);
}
