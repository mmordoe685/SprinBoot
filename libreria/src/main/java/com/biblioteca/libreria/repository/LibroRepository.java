package com.biblioteca.libreria.repository;

import com.biblioteca.libreria.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LibroRepository extends JpaRepository<Libro, Integer> {

    // Todos los libros que están en venta
    List<Libro> findByEnVentaTrue();

    // Libros en venta por nombre de categoría (por ejemplo "Novelas")
    List<Libro> findByCategoria_NombreCategoriaAndEnVentaTrue(String nombreCategoria);
}
