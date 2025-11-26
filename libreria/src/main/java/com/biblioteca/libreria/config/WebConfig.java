package com.biblioteca.libreria.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // Mapea cualquier petición a /images/** a la carpeta static/images
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/static/images/");

        // (Opcional) por si quieres asegurarte con el logo y otros estáticos
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }
}