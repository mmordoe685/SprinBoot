package com.biblioteca.libreria.controller;

import com.biblioteca.libreria.model.Usuario;
import com.biblioteca.libreria.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

@Controller
@RequestMapping("/usuarios")
public class UsuarioViewController {

    @Autowired
    private UsuarioRepository repo;

    // ======================================================
    // MÉTODO PARA CREAR URL DE GRAVATAR (simple)
    // ======================================================
    private String generarGravatar(String email) {

        if (email == null) {
            return "https://www.gravatar.com/avatar/00000000000000000000000000000000?d=identicon";
        }

        try {
            MessageDigest md = MessageDigest.getInstance("MD5");

            String limpio = email.trim().toLowerCase();
            byte[] bytes = md.digest(limpio.getBytes());

            StringBuilder hex = new StringBuilder();
            for (byte b : bytes) {
                hex.append(String.format("%02x", b));
            }

            return "https://www.gravatar.com/avatar/" + hex.toString() + "?d=identicon";

        } catch (Exception e) {
            return "https://www.gravatar.com/avatar/00000000000000000000000000000000?d=identicon";
        }
    }

    // ======================================================
    // LISTADO DE USUARIOS
    // ======================================================
    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", repo.findAll());
        return "usuarios";
    }

    // ======================================================
    // DETALLE
    // ======================================================
    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Integer id, Model model) {

        Usuario usuario = repo.findById(id).orElse(null);

        if (usuario == null) {
            return "redirect:/usuarios";
        }

        model.addAttribute("usuario", usuario);
        return "usuario-detalle";
    }

    // ======================================================
    // NUEVO USUARIO
    // ======================================================
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {

        model.addAttribute("usuario", new Usuario());
        model.addAttribute("accion", "/usuarios");
        model.addAttribute("titulo", "Crear usuario");

        return "usuario-form";
    }

    // ======================================================
    // GUARDAR NUEVO USUARIO (ULTRA DAW)
    // ======================================================
    @PostMapping
    public String guardarNuevo(@ModelAttribute Usuario usuario,
                               @RequestParam("foto") MultipartFile foto) {

        try {
            // Caso 1 → foto subida
            if (foto != null && !foto.isEmpty()) {

                Path carpeta = Paths.get("uploads");
                if (!Files.exists(carpeta)) {
                    Files.createDirectories(carpeta);
                }

                String nombreArchivo = "usuario-" + System.currentTimeMillis()
                        + "-" + foto.getOriginalFilename();

                Path ruta = carpeta.resolve(nombreArchivo);
                Files.write(ruta, foto.getBytes());

                usuario.setFotoUrl("/uploads/" + nombreArchivo);
            }

            // Caso 2 → NO sube foto → Gravatar
            if (usuario.getFotoUrl() == null || usuario.getFotoUrl().isBlank()) {
                usuario.setFotoUrl(generarGravatar(usuario.getEmail()));
            }

            repo.save(usuario);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/usuarios";
    }

    // ======================================================
    // EDITAR USUARIO (ULTRA DAW)
    // ======================================================
    @GetMapping("/{id}/editar")
    public String mostrarFormularioEditar(@PathVariable Integer id, Model model) {

        Usuario usuario = repo.findById(id).orElse(null);

        if (usuario == null) {
            return "redirect:/usuarios";
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("accion", "/usuarios/" + id + "/editar");
        model.addAttribute("titulo", "Editar usuario");

        return "usuario-form";
    }

    // ======================================================
    // GUARDAR EDICIÓN (ULTRA DAW – opción A)
    // ======================================================
    @PostMapping("/{id}/editar")
    public String guardarEdicion(@PathVariable Integer id,
                                 @ModelAttribute Usuario datos,
                                 @RequestParam("foto") MultipartFile foto) {

        Usuario usuario = repo.findById(id).orElse(null);

        if (usuario == null) {
            return "redirect:/usuarios";
        }

        // Actualizamos datos básicos
        usuario.setNombre(datos.getNombre());
        usuario.setEmail(datos.getEmail());
        usuario.setContrasena(datos.getContrasena());
        usuario.setTipoUsuario(datos.getTipoUsuario());
        usuario.setPais(datos.getPais());

        try {
            // Caso 1 → si sube foto nueva, se guarda
            if (foto != null && !foto.isEmpty()) {

                Path carpeta = Paths.get("uploads");
                if (!Files.exists(carpeta)) {
                    Files.createDirectories(carpeta);
                }

                String nombreArchivo = "usuario-" + id + "-" + foto.getOriginalFilename();
                Path ruta = carpeta.resolve(nombreArchivo);

                Files.write(ruta, foto.getBytes());

                usuario.setFotoUrl("/uploads/" + nombreArchivo);
            }

            // Caso 2 → no sube foto → mantenemos la que ya tenía
            // (Opción A: NO tocamos fotoUrl si ya existe)

            repo.save(usuario);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/usuarios";
    }

    // ======================================================
    // ELIMINAR
    // ======================================================
    @PostMapping("/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Integer id) {

        if (repo.existsById(id)) {
            repo.deleteById(id);
        }

        return "redirect:/usuarios";
    }
}
