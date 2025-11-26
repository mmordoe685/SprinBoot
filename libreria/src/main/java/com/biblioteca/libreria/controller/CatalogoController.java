package com.biblioteca.libreria.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.biblioteca.libreria.model.Categoria;
import com.biblioteca.libreria.model.Libro;
import com.biblioteca.libreria.repository.CategoriaRepository;
import com.biblioteca.libreria.repository.LibroRepository;

@Controller
public class CatalogoController {


    private final LibroRepository libroRepository;
    private final CategoriaRepository categoriaRepository;

    public CatalogoController(LibroRepository libroRepository,
                              CategoriaRepository categoriaRepository) {
        this.libroRepository = libroRepository;
        this.categoriaRepository = categoriaRepository;
    }

   @GetMapping("/catalogo")
public String verCatalogo(@RequestParam(name = "categoria", required = false) String nombreCategoria, Model model) {
    // Obtener todas las categorías para mostrarlas en los botones de filtro
    List<Categoria> categorias = categoriaRepository.findAll();

    // Libros a mostrar, dependiendo de si se ha seleccionado una categoría o no
    List<Libro> libros;
    if (nombreCategoria == null || nombreCategoria.isEmpty()) {
        // Sin filtro: mostrar todos los libros que están en venta
        libros = libroRepository.findByEnVentaTrue();
    } else {
        // Con filtro: mostrar solo los libros de la categoría seleccionada
        libros = libroRepository.findByCategoria_NombreCategoriaAndEnVentaTrue(nombreCategoria);
    }

    // Añadir los atributos al modelo para pasarlos a la vista
    model.addAttribute("categorias", categorias);
    model.addAttribute("libros", libros);
    model.addAttribute("categoriaSeleccionada", nombreCategoria);

    // Retorna la vista 'Catalogo', que es el template HTML para mostrar los libros
    return "Catalogo"; // Este es el archivo catalogo.html que se encuentra en src/main/resources/templates
}
}
    
