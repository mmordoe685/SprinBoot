package com.biblioteca.libreria.security;

import com.biblioteca.libreria.model.Usuario;
import com.biblioteca.libreria.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccesoAdmin implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Este método lo usa Spring Security cuando alguien se intenta loguear
     * con el formulario normal (usuario/contraseña).
     *
     * Aquí buscamos el usuario por EMAIL en la base de datos
     * y devolvemos un objeto UserDetails que Spring entiende.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Buscamos el usuario por email en la BD
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            // Si no existe, lanzamos esta excepción especial
            // Spring la entiende como "usuario no encontrado"
            throw new UsernameNotFoundException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();

        // tipoUsuario puede ser "ADMIN" o "USER".
        // Si está a null, asumimos "USER" como valor por defecto.
        String tipoUsuario = usuario.getTipoUsuario();
        if (tipoUsuario == null) {
            tipoUsuario = "USER";
        }

        // Spring espera el rol con el prefijo "ROLE_"
        String rolConPrefijo = "ROLE_" + tipoUsuario.toUpperCase();

        // Creamos la autoridad (el rol) del usuario
        SimpleGrantedAuthority autoridad = new SimpleGrantedAuthority(rolConPrefijo);

        // Devolvemos un User (de Spring Security) con:
        // - email como nombre de usuario
        // - contraseña tal cual está en la BD
        // - lista de roles (en este caso solo 1)
        return new org.springframework.security.core.userdetails.User(
                usuario.getEmail(),
                usuario.getContrasena(),
                List.of(autoridad)
        );
    }
}
