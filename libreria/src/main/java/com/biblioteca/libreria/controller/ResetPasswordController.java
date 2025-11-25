package com.biblioteca.libreria.controller;

import com.biblioteca.libreria.model.Usuario;
import com.biblioteca.libreria.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ResetPasswordController {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public ResetPasswordController(UsuarioRepository usuarioRepository,
                                   PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Muestra el formulario de cambio de contraseña.
     * Se llama desde el enlace del correo:
     *  /reset-password?token=...&email=...
     */
    @GetMapping("/reset-password")
    public String mostrarFormularioReset(
            @RequestParam(required = false) String token,
            @RequestParam(required = false) String email,
            Model model) {

        model.addAttribute("token", token);
        model.addAttribute("email", email);

        return "reset-password"; // busca reset-password.html en /templates
    }

    /**
     * Procesa el formulario de cambio de contraseña.
     */
    @PostMapping("/reset-password")
    public String procesarFormularioReset(
            @RequestParam String token,
            @RequestParam String email,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {

        // Volvemos a poner token y email por si hay errores y hay que recargar la vista
        model.addAttribute("token", token);
        model.addAttribute("email", email);

        // 1) Validar que no estén vacías
        if (password == null || password.isBlank()
                || confirmPassword == null || confirmPassword.isBlank()) {
            model.addAttribute("error", "La contraseña no puede estar vacía.");
            return "reset-password";
        }

        // 2) Validar que coincidan
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "reset-password";
        }

        // 3) Buscar el usuario por email
        var usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            model.addAttribute("error", "No existe ningún usuario con ese correo.");
            return "reset-password";
        }

        Usuario usuario = usuarioOpt.get();

        // 4) Actualizar la contraseña (ahora mismo con NoOpPasswordEncoder)
        usuario.setContrasena(passwordEncoder.encode(password));
        usuarioRepository.save(usuario);

        // 5) Redirigir al login con un mensaje de éxito
        return "redirect:/login?resetOk";
    }
}

