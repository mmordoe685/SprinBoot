package com.biblioteca.libreria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {

        List<Map<String, String>> libros = List.of(
                Map.of(
                        "titulo", "El mundo de Sofía",
                        "autor", "Jostein Gaarder",
                        "descripcion", "Una introducción a la filosofía."
                ),
                Map.of(
                        "titulo", "1984",
                        "autor", "George Orwell",
                        "descripcion", "Distopía política clásica."
                ),
                Map.of(
                        "titulo", "Clean Code",
                        "autor", "Robert C. Martin",
                        "descripcion", "Guía para escribir mejor código."
                )
        );

        model.addAttribute("usuario", "Usuario");
        model.addAttribute("librosDestacados", libros);

        return "index"; // llama a templates/index.html
    }
}
