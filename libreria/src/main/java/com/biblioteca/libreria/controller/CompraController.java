package com.biblioteca.libreria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CompraController {

    @GetMapping("/carrito")
    public String verCarrito(Model model) {
        // Aquí puedes agregar la lista de libros en el carrito
        // Por ahora mostramos solo una página de ejemplo
        model.addAttribute("titulo", "Carrito de Compras");
        return "Carrito"; // Apunta a templates/Carrito.html
    }
}
