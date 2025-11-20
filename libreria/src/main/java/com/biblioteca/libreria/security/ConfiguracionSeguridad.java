package com.biblioteca.libreria.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ConfiguracionSeguridad {

    @Bean
    public SecurityFilterChain filtroSeguridad(HttpSecurity http) throws Exception {

        http
            // aqui decimos qué rutas son publicas y cuales piden estar logueado
            .authorizeHttpRequests(autorizacion -> autorizacion
                .requestMatchers(
                        "/",              // portada
                        "/index",         // por si se usa /index
                        "/login",         // pagina de login personalizada
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/api/**"         // de momento dejamos las apis abiertas
                ).permitAll()
                .anyRequest().authenticated() // el resto de rutas piden login
            )

            // activamos el login con google / github
            .oauth2Login(loginOauth -> loginOauth
                .loginPage("/login")           // nuestra pagina de login
                .defaultSuccessUrl("/panel", true) // a donde mandamos al user tras loguearse
            )

            // logout basico: destruye sesion y vuelve al inicio
            .logout(cerrarSesion -> cerrarSesion
                .logoutSuccessUrl("/").permitAll()
            )

            // desactivamos csrf para no liarnos con pruebas y postman
            .csrf(csrf -> csrf.disable());

        return http.build();
    }
}
