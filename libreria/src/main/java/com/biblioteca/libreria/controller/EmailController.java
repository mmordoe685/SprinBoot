package com.biblioteca.libreria.controller;

import com.biblioteca.libreria.service.EmailService;
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
     * Se puede probar realizando una petición POST a:
     * /api/mail/registro?to=correo@ejemplo.com&username=NombreUsuario
     *
     * @param to       dirección de correo del destinatario
     * @param username nombre del usuario registrado
     * @return mensaje de confirmación en texto plano
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
     * Ejemplo de uso:
     * /api/mail/confirmacion?to=correo@ejemplo.com&username=NombreUsuario&token=abc123
     *
     * @param to       dirección de correo del destinatario
     * @param username nombre del usuario
     * @param token    token de confirmación generado previamente
     * @return mensaje de confirmación en texto plano
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
     * Ejemplo de uso:
     * /api/mail/recuperacion?to=correo@ejemplo.com&username=NombreUsuario&token=reset123
     *
     * @param to       dirección de correo del destinatario
     * @param username nombre del usuario
     * @param token    token de recuperación de contraseña
     * @return mensaje de confirmación en texto plano
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
     * Ejemplo de uso:
     * /api/mail/resumen?to=correo@ejemplo.com&username=NombreUsuario&resumen=TextoResumen
     *
     * @param to       dirección de correo del destinatario
     * @param username nombre del usuario
     * @param resumen  resumen de la actividad que se desea enviar
     * @return mensaje de confirmación en texto plano
     */
    @PostMapping("/resumen")
    public String sendResumen(@RequestParam String to,
                              @RequestParam String username,
                              @RequestParam String resumen) {
        emailService.sendActivitySummaryEmail(to, username, resumen);
        return "Correo con resumen de actividad enviado a " + to;
    }
}
