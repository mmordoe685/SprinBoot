package com.biblioteca.libreria.repository;

import com.biblioteca.libreria.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Long> {
    List<Libro> findByEnVentaTrue();
}
