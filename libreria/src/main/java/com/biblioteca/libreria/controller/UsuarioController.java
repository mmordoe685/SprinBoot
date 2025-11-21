package com.biblioteca.libreria.controller;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.biblioteca.libreria.model.Usuario;
import com.biblioteca.libreria.repository.UsuarioRepository;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repo;



    // =========================================================
    // DTO PARA EL DASHBOARD
    // =========================================================
    public static class UsuarioDashboardDTO {
        private Integer idUsuario;
        private String nombre;
        private String email;
        private String tipoUsuario;
        private String fotoUrl;
        private String pais;
        private int totalCompras;
        private int totalLibrosBiblioteca;

        public UsuarioDashboardDTO(
                Integer idUsuario,
                String nombre,
                String email,
                String tipoUsuario,
                String fotoUrl,
                String pais,
                int totalCompras,
                int totalLibrosBiblioteca) {

            this.idUsuario = idUsuario;
            this.nombre = nombre;
            this.email = email;
            this.tipoUsuario = tipoUsuario;
            this.fotoUrl = fotoUrl;
            this.pais = pais;
            this.totalCompras = totalCompras;
            this.totalLibrosBiblioteca = totalLibrosBiblioteca;
        }

        public Integer getIdUsuario() { return idUsuario; }
        public String getNombre() { return nombre; }
        public String getEmail() { return email; }
        public String getTipoUsuario() { return tipoUsuario; }
        public String getFotoUrl() { return fotoUrl; }
        public String getPais() { return pais; }
        public int getTotalCompras() { return totalCompras; }
        public int getTotalLibrosBiblioteca() { return totalLibrosBiblioteca; }
    }

    private String generarGravatar(String email) {

    // Si el email es nulo → devolvemos un avatar por defecto
    if (email == null) {
        return "https://www.gravatar.com/avatar/00000000000000000000000000000000?d=identicon";
    }

    try {
        // 1. Crear el objeto para hacer MD5
        MessageDigest md = MessageDigest.getInstance("MD5");

        // 2. Convertir el email a minúsculas y sin espacios
        String limpio = email.trim().toLowerCase();

        // 3. Obtener el hash en bytes
        byte[] bytes = md.digest(limpio.getBytes());

        // 4. Convertir los bytes a texto hexadecimal (fácil de leer)
        StringBuilder hex = new StringBuilder();
        for (byte b : bytes) {
            hex.append(String.format("%02x", b));
        }

        // 5. Construir la URL final de Gravatar
        return "https://www.gravatar.com/avatar/" + hex.toString() + "?d=identicon";

    } catch (Exception e) {
        // Si algo falla, devolvemos un avatar seguro
        return "https://www.gravatar.com/avatar/00000000000000000000000000000000?d=identicon";
    }
}

    // ============================
    // CRUD DE USUARIOS
    // ============================

    @GetMapping
    public List<Usuario> listar() {
        return repo.findAll();
    }

    @GetMapping("listar/{id}")
    public ResponseEntity<Usuario> obtener(@PathVariable Integer id) {
        Optional<Usuario> encontrado = repo.findById(id);

        if (encontrado.isPresent()) {
            return ResponseEntity.ok(encontrado.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/crear")
public ResponseEntity<Usuario> crear(@RequestBody Usuario usuario) {

    usuario.setFechaRegistro(LocalDateTime.now());

    // Si NO envían foto → usamos gravatar
    if (usuario.getFotoUrl() == null || usuario.getFotoUrl().isBlank()) {
        usuario.setFotoUrl(generarGravatar(usuario.getEmail()));
    }

    Usuario guardado = repo.save(usuario);
    return ResponseEntity.ok(guardado);
}


    

    @PutMapping("update/{id}")
public ResponseEntity<Usuario> actualizar(@PathVariable Integer id,
                                          @RequestBody Usuario datos) {

    // 1. Buscar el usuario en la BD
    Optional<Usuario> encontrado = repo.findById(id);

    // 2. Si no existe, devolver 404
    if (!encontrado.isPresent()) {
        return ResponseEntity.notFound().build();
    }

    // 3. Obtener el usuario real de la BD
    Usuario usuario = encontrado.get();

    // 4. Actualizar los campos normales
    usuario.setNombre(datos.getNombre());
    usuario.setEmail(datos.getEmail());
    usuario.setContrasena(datos.getContrasena());
    usuario.setTipoUsuario(datos.getTipoUsuario());
    usuario.setPais(datos.getPais());

    // ======================================
    // FOTO DE PERFIL (LÓGICA ESTILO DAW)
    // ======================================
    //
    // Si NO han enviado foto (campo vacío o null),
    // entonces generamos automáticamente un gravatar
    // según el email del usuario.
    //
    // Si SÍ se envía fotoUrl, la respetamos.
    //
    // Esto es lo más simple y entendible.
    // ======================================

    if (datos.getFotoUrl() == null || datos.getFotoUrl().isBlank()) {
        // Generar Gravatar para ese email
        String gravatar = generarGravatar(datos.getEmail());
        usuario.setFotoUrl(gravatar);
    } else {
        // Usar la foto enviada
        usuario.setFotoUrl(datos.getFotoUrl());
    }

    // 5. Guardar cambios
    Usuario actualizado = repo.save(usuario);

    // 6. Devolver usuario actualizado
    return ResponseEntity.ok(actualizado);
}
    @DeleteMapping("eliminar/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (!repo.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ============================
    // DASHBOARD USUARIO
    // ============================

    @GetMapping("/{id}/dashboard")
    public ResponseEntity<UsuarioDashboardDTO> dashboard(@PathVariable Integer id) {

        Optional<Usuario> encontrado = repo.findById(id);

        if (!encontrado.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = encontrado.get();

        int totalCompras = (usuario.getCompras() != null)
                ? usuario.getCompras().size()
                : 0;

        int totalLibros = (usuario.getBibliotecaPersonal() != null)
                ? usuario.getBibliotecaPersonal().size()
                : 0;

        UsuarioDashboardDTO dto = new UsuarioDashboardDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getTipoUsuario(),
                usuario.getFotoUrl(),
                usuario.getPais(),
                totalCompras,
                totalLibros
        );

        return ResponseEntity.ok(dto);
    }

    
}
