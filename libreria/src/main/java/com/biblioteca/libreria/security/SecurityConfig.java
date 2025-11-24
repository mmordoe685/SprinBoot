package com.biblioteca.libreria.security;

import com.biblioteca.libreria.model.Usuario;
import com.biblioteca.libreria.repository.UsuarioRepository;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private CustomGoogleInicioSesion customGoogleInicioSesion;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Codificador de contraseñas.
     * Ahora mismo usamos NoOp (NO encripta, solo para pruebas).
     * Más adelante puedes cambiarlo a BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * Configura el AuthenticationManager para que use
     * nuestro UserDetailsService (AccesoAdmin) y el passwordEncoder.
     */
    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        authBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());

        return authBuilder.build();
    }

    /**
     * Este handler se ejecuta cuando el login (form o OAuth2) ha ido BIEN.
     * Aquí decidimos:
     *  - Cómo obtener el email/nombre (según sea Google, GitHub o login normal).
     *  - Si creamos el usuario en la BD si no existe.
     *  - Si necesita completar registro (poner contraseña).
     *  - A dónde redirigir (perfil o admin).
     */
    @Bean
public AuthenticationSuccessHandler successHandler() {
    return (request, response, authentication) -> {
        String email = null;
        String nombre = null;

        // 1) Si el login ha sido con Google o GitHub (OAuth2)
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oAuth2Token = (OAuth2AuthenticationToken) authentication;
            var attrs = oAuth2Token.getPrincipal().getAttributes();
            String registrationId = oAuth2Token.getAuthorizedClientRegistrationId();

            if ("google".equalsIgnoreCase(registrationId)) {
                email = (String) attrs.get("email");
                nombre = (String) attrs.get("name");
            } else if ("github".equalsIgnoreCase(registrationId)) {
                email = (String) attrs.get("email");
                String login = (String) attrs.get("login"); // nick de GitHub
                if (email == null && login != null) {
                    email = login + "@github.local";
                }
                nombre = (String) attrs.get("name");
                if (nombre == null) {
                    nombre = login;
                }
            }
        } else {
            // 2) Si el login ha sido con el formulario normal
            email = authentication.getName();
        }

        // 3) Buscar el usuario en la BD por su email
        Usuario usuario = null;
        if (email != null) {
            usuario = usuarioRepository.findByEmail(email).orElse(null);
        }

        // 4) Si no existe en la BD y tenemos email, lo creamos
        if (usuario == null && email != null) {
            usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setNombre(nombre);
            usuario.setTipoUsuario("USER"); // Por defecto, todos son USER
            usuario.setFechaRegistro(LocalDateTime.now());
            usuario = usuarioRepository.save(usuario);
        }

        // 5) Si el usuario no existe o no tiene contraseña -> completar-registro
        if (usuario == null || usuario.getContrasena() == null) {
            response.sendRedirect("/completar-registro");
            return;
        }

        // 6) Si el usuario tiene rol USER, redirigir a catalogo.html
        boolean esUser = authentication.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));

        if (esUser) {
            response.sendRedirect("/catalogo");  // Redirige al catálogo si el usuario es de tipo USER
        } else {
            // Si el usuario es admin, redirige al admin
            boolean esAdmin = authentication.getAuthorities()
                    .stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (esAdmin) {
                response.sendRedirect("/admin");
            } else {
                response.sendRedirect("/perfil");
            }
        }
    };
}


    /**
     * Configuración principal de la seguridad HTTP:
     * - Qué URLs son públicas
     * - Qué URLs necesitan estar logueado
     * - Config de login normal
     * - Config de login con OAuth2 (Google/GitHub)
     * - Logout
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // Autorización de las distintas rutas
            .authorizeHttpRequests(auth -> auth
                // Recursos públicos (CSS, JS, imágenes...), login, registro, OAuth2...
                .requestMatchers(
                     "/", "/index.html",
                        "/css/**", "/js/**", "/images/**", "/uploads/**",
                        "/login", "/registro",
                        "/oauth2/**", "/error"
                ).permitAll()
                // Rutas /admin solo para usuarios con rol ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Cualquier otra ruta requiere estar autenticado
                .anyRequest().authenticated()
            )

            // Configuración del login por formulario
            .formLogin(form -> form
                .loginPage("/login").permitAll()     // Página de login personalizada
                .usernameParameter("email")          // Campo "name" del input del email
                .passwordParameter("password")       // Campo "name" del input de contraseña
                .successHandler(successHandler())    // Qué pasa si el login va bien
            )

            // Configuración del login con OAuth2 (Google/GitHub)
            .oauth2Login(oauth -> oauth
                .loginPage("/login")                 // Usamos la misma página de login
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customGoogleInicioSesion) // Servicio que carga los datos de Google/GitHub
                )
                .successHandler(successHandler())    // Mismo success handler
            )

            // Configuración del logout
            .logout(logout -> logout
                .logoutUrl("/logout")               // URL para hacer logout
                .logoutSuccessUrl("/login?logout")  // A dónde vamos después de hacer logout
            )

            // Para simplificar ahora, desactivamos CSRF (podrás activarlo más adelante)
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
