package com.biblioteca.libreria.repository;

import com.biblioteca.libreria.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    
}