package com.biblioteca.libreria.controller;

import com.biblioteca.libreria.model.Usuario;
import com.biblioteca.libreria.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // ------------------------------------------------------------
    // PÁGINA DE LOGIN
    // ------------------------------------------------------------
    @GetMapping("/login")
    public String login() {
        // Devuelve la plantilla login.html
        return "login";
    }

    // ------------------------------------------------------------
    // REGISTRO NORMAL (FORMULARIO PROPIO)
    // ------------------------------------------------------------
    @PostMapping("/registro")
    public String registrarNuevo(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam String password) {

        // Crear un nuevo usuario con los datos del formulario
        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setEmail(email);
        u.setContrasena(password);
        u.setTipoUsuario("USER");
        u.setFechaRegistro(LocalDateTime.now());

        // Guardar en la base de datos
        usuarioRepository.save(u);

        // Volver a la pantalla de login
        return "redirect:/login";
    }

    // ------------------------------------------------------------
    // COMPLETAR REGISTRO DESPUÉS DE GOOGLE / GITHUB (GET)
    // ------------------------------------------------------------
    @GetMapping("/completar-registro")
    public String mostrarFormularioCompletar(OAuth2AuthenticationToken auth, Model model) {

        // Si no viene de un login OAuth2, lo mandamos al login normal
        if (auth == null) {
            return "redirect:/login";
        }

        // Saber si ha entrado con Google o con GitHub
        String registrationId = auth.getAuthorizedClientRegistrationId();
        var attrs = auth.getPrincipal().getAttributes();

        // Intentamos sacar el email del usuario
        String email = (String) attrs.get("email");

        // Caso especial: GitHub puede no enviar email
        if ("github".equalsIgnoreCase(registrationId) && email == null) {
            String login = (String) attrs.get("login");   // nombre de usuario en GitHub
            if (login != null) {
                email = login + "@github.local";          // inventamos un correo interno
            }
        }

        // Si seguimos sin email, no podemos continuar
        if (email == null) {
            return "redirect:/login";
        }

        // Buscar el usuario por email
        Optional<Usuario> optUsuario = usuarioRepository.findByEmail(email);

        if (!optUsuario.isPresent()) {
            // Si no existe, volvemos al login (por seguridad)
            return "redirect:/login";
        }

        Usuario usuario = optUsuario.get();

        // Enviar el usuario a la vista para mostrar sus datos
        model.addAttribute("usuario", usuario);

        // Cargar completar-registro.html
        return "completar-registro";
    }

    // ------------------------------------------------------------
    // COMPLETAR REGISTRO DESPUÉS DE GOOGLE / GITHUB (POST)
    // ------------------------------------------------------------
    @PostMapping("/completar-registro")
    public String guardarCompletar(
            OAuth2AuthenticationToken auth,
            @RequestParam String nombre,
            @RequestParam String password) {

        // Solo debe venir de un login OAuth2
        if (auth == null) {
            return "redirect:/login";
        }

        // Saber si es Google o GitHub
        String registrationId = auth.getAuthorizedClientRegistrationId();
        var attrs = auth.getPrincipal().getAttributes();

        String email = (String) attrs.get("email");

        // Mismo caso especial para GitHub
        if ("github".equalsIgnoreCase(registrationId) && email == null) {
            String login = (String) attrs.get("login");
            if (login != null) {
                email = login + "@github.local";
            }
        }

        if (email == null) {
            return "redirect:/login";
        }

        // Buscar el usuario en la BD
        Optional<Usuario> optUsuario = usuarioRepository.findByEmail(email);
        if (!optUsuario.isPresent()) {
            return "redirect:/login";
        }

        Usuario usuario = optUsuario.get();

        // Actualizar datos que ha rellenado en el formulario
        usuario.setNombre(nombre);
        usuario.setContrasena(password);

        // Guardar cambios
        usuarioRepository.save(usuario);

        // Ir a la página de perfil
        return "redirect:/perfil";
    }

    // ------------------------------------------------------------
    // PÁGINA DE PERFIL
    // ------------------------------------------------------------
    @GetMapping("/perfil")
    public String perfil() {
        // Devuelve la plantilla perfil.html
        return "perfil";
    }
}
