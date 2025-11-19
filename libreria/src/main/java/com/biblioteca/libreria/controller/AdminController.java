package com.biblioteca.libreria.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    // Ruta para mostrar el panel de administración
    @GetMapping("/panel_admin")
    public String panelAdmin(Model model) {
        // Puedes agregar atributos para mostrar estadísticas, libros, usuarios, etc.
        model.addAttribute("titulo", "Panel de Administración");
        model.addAttribute("mensaje", "Bienvenido al Panel de Administración de la Biblioteca Digital");
        return "Panel_Admin"; // Apunta a templates/Panel_Admin.html
    }

}
