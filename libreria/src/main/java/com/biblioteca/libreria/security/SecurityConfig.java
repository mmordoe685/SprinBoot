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
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    /**
     * Configura el AuthenticationManager para que use
     * nuestro UserDetailsService y el passwordEncoder.
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
     * SuccessHandler: qué pasa cuando el login va bien (formulario u OAuth2).
     */
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            String email = null;
            String nombre = null;

            // 1) Login con Google / GitHub
            if (authentication instanceof OAuth2AuthenticationToken oAuth2Token) {
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
                // 2) Login normal (formulario)
                email = authentication.getName();
            }

            // 3) Buscar usuario en BD
            Usuario usuario = null;
            if (email != null) {
                usuario = usuarioRepository.findByEmail(email).orElse(null);
            }

            // 4) Si no existe y tenemos email -> creamos USER por defecto
            if (usuario == null && email != null) {
                usuario = new Usuario();
                usuario.setEmail(email);
                usuario.setNombre(nombre);
                usuario.setTipoUsuario("USER");
                usuario.setFechaRegistro(LocalDateTime.now());
                usuario = usuarioRepository.save(usuario);
            }

            // 5) Si no hay usuario o no tiene contraseña -> completar-registro
            if (usuario == null || usuario.getContrasena() == null) {
                response.sendRedirect("/completar-registro");
                return;
            }

            // 6) Redirecciones según rol
            boolean esUser = authentication.getAuthorities()
                    .stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_USER"));

            if (esUser) {
                response.sendRedirect("/catalogo");
            } else {
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
     * Configuración principal de seguridad HTTP.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // Autorización de rutas
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/", "/index.html",
                        "/css/**", "/js/**", "/images/**", "/uploads/**",
                        "/login", "/registro",
                        "/oauth2/**", "/error",
                        "/api/mail/**",     // API de email accesible sin login
                        "/reset-password"   // Formulario de reset accesible sin login (GET y POST)
                ).permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )

            // Login por formulario
            .formLogin(form -> form
                .loginPage("/login").permitAll()
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(successHandler())
            )

            // Login con OAuth2 (Google/GitHub)
            .oauth2Login(oauth -> oauth
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customGoogleInicioSesion)
                )
                .successHandler(successHandler())
            )

            // Logout
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
            )

            // CSRF desactivado para simplificar
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
