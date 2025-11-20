package com.biblioteca.libreria.controller;

import com.biblioteca.libreria.Service.EmailService;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para exponer endpoints relacionados con el envío de correos.
 *
 * Estos endpoints permiten probar y utilizar las distintas funcionalidades
 * de notificación por correo desde la aplicación o herramientas externas
 * como Postman.
 */
@RestController
@RequestMapping("/api/mail")
public class EmailController {

    /**
     * Servicio de correo que contiene la lógica de envío de emails.
     */
    private final EmailService emailService;

    /**
     * Constructor del controlador donde se inyecta el servicio de correo.
     *
     * @param emailService instancia de EmailService proporcionada por Spring
     */
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * Endpoint para enviar un correo de bienvenida tras un nuevo registro.
     *
     * Ejemplo:
     * POST /api/mail/registro?to=correo@ejemplo.com&username=NombreUsuario
     */
    @PostMapping("/registro")
    public String sendRegistro(@RequestParam String to,
                               @RequestParam String username) {
        emailService.sendNewRegistrationEmail(to, username);
        return "Correo de registro enviado a " + to;
    }

    /**
     * Endpoint para enviar un correo de confirmación de cuenta.
     *
     * Ejemplo:
     * POST /api/mail/confirmacion?to=correo@ejemplo.com&username=NombreUsuario&token=abc123
     */
    @PostMapping("/confirmacion")
    public String sendConfirmacion(@RequestParam String to,
                                   @RequestParam String username,
                                   @RequestParam String token) {
        emailService.sendAccountConfirmationEmail(to, username, token);
        return "Correo de confirmación enviado a " + to;
    }

    /**
     * Endpoint para enviar un correo de recuperación de contraseña.
     *
     * Ejemplo:
     * POST /api/mail/recuperacion?to=correo@ejemplo.com&username=NombreUsuario&token=reset123
     */
    @PostMapping("/recuperacion")
    public String sendRecuperacion(@RequestParam String to,
                                   @RequestParam String username,
                                   @RequestParam String token) {
        emailService.sendPasswordRecoveryEmail(to, username, token);
        return "Correo de recuperación de contraseña enviado a " + to;
    }

    /**
     * Endpoint para enviar un correo con un resumen de actividad.
     *
     * Ejemplo:
     * POST /api/mail/resumen?to=correo@ejemplo.com&username=NombreUsuario&resumen=TextoResumen
     */
    @PostMapping("/resumen")
    public String sendResumen(@RequestParam String to,
                              @RequestParam String username,
                              @RequestParam String resumen) {
        emailService.sendActivitySummaryEmail(to, username, resumen);
        return "Correo con resumen de actividad enviado a " + to;
    }
}
