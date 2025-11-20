package com.biblioteca.libreria.controller;

import com.biblioteca.libreria.Service.EmailService;
import com.biblioteca.libreria.model.Usuario;
import com.biblioteca.libreria.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository repo;

    @Autowired
    private EmailService emailService;   //  inyectamos el servicio de email

    @GetMapping
    public List<Usuario> listar() {
        return repo.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtener(@PathVariable Integer id) {
        return repo.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Usuario crear(@RequestBody Usuario usuario) {
        // guardamos fecha de registro
        usuario.setFechaRegistro(LocalDateTime.now());

        // guardamos en BD
        Usuario nuevo = repo.save(usuario);

        // enviamos correo de bienvenida
        emailService.sendNewRegistrationEmail(nuevo.getEmail(), nuevo.getNombre());

        return nuevo;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> actualizar(@PathVariable Integer id, @RequestBody Usuario datos) {
        return repo.findById(id)
            .map(usuario -> {
                usuario.setNombre(datos.getNombre());
                usuario.setEmail(datos.getEmail());
                usuario.setContrasena(datos.getContrasena());
                usuario.setTipoUsuario(datos.getTipoUsuario());
                return ResponseEntity.ok(repo.save(usuario));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Integer id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
