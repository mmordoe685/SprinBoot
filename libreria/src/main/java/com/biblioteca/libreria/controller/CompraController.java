package com.biblioteca.libreria.controller;

import com.biblioteca.libreria.model.Libro;
import com.biblioteca.libreria.repository.LibroRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CompraController {

    private final LibroRepository libroRepository;

    public CompraController(LibroRepository libroRepository) {
        this.libroRepository = libroRepository;
    }

    @GetMapping("/carrito")
    public String verCarrito(Model model) {

        // Obtener los libros que están en venta
        List<Libro> librosEnVenta = libroRepository.findByEnVentaTrue();

        model.addAttribute("titulo", "Carrito de Compras");
        model.addAttribute("libros", librosEnVenta);

        return "Carrito"; // templates/Carrito.html
    }
}
