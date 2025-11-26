package com.biblioteca.libreria.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class CustomGoogleInicioSesion implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        // Delegamos en el servicio por defecto de Spring
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        // "google" o "github"
        String registrationId = userRequest
                .getClientRegistration()
                .getRegistrationId();

        // Atributos tal cual vienen del proveedor
        Map<String, Object> attributes = oauth2User.getAttributes();

        // Clave principal del nombre de usuario
        String userNameAttributeName = userRequest
                .getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // Rol por defecto para cualquier usuario OAuth2
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");

        // Devolvemos un DefaultOAuth2User que Spring entiende
        return new DefaultOAuth2User(
                Collections.singleton(authority),
                attributes,
                userNameAttributeName
        );
    }
}
