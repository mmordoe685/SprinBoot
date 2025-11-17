package com.biblioteca.libreria.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de la gestión y envío de correos electrónicos.
 *
 * En este servicio se centraliza la lógica para los distintos tipos de
 * notificaciones por correo: registro, confirmación de cuenta,
 * recuperación de contraseña y resumen de actividad.
 */
@Service
public class EmailService {

    /**
     * Componente proporcionado por Spring para el envío de correos electrónicos.
     */
    private final JavaMailSender mailSender;

    /**
     * Dirección de correo que se utilizará como remitente de los mensajes.
     * El valor se carga desde el fichero de configuración application.properties.
     */
    @Value("${app.mail.from}")
    private String from;

    /**
     * URL base de la aplicación, utilizada para generar enlaces en los correos
     * (por ejemplo, enlaces de confirmación o recuperación de contraseña).
     */
    @Value("${app.mail.base-url}")
    private String baseUrl;

    /**
     * Constructor del servicio. Spring inyecta automáticamente el JavaMailSender.
     *
     * @param mailSender componente responsable de enviar los correos
     */
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Método privado genérico para enviar un correo simple de texto plano.
     *
     * @param to      dirección de correo del destinatario
     * @param subject asunto del mensaje
     * @param text    contenido del mensaje en formato texto
     */
    private void sendSimpleMail(String to, String subject, String text) {
        // Creamos un objeto SimpleMailMessage para representar el correo
        SimpleMailMessage message = new SimpleMailMessage();

        // Establecemos el remitente del correo
        message.setFrom(from);

        // Establecemos el destinatario
        message.setTo(to);

        // Establecemos el asunto del mensaje
        message.setSubject(subject);

        // Establecemos el cuerpo del mensaje en texto plano
        message.setText(text);

        // Enviamos el correo a través del componente JavaMailSender
        mailSender.send(message);
    }

    /**
     * Envío de correo de bienvenida tras un nuevo registro de usuario.
     *
     * @param to       dirección de correo del usuario
     * @param username nombre del usuario (se utilizará en el saludo)
     */
    public void sendNewRegistrationEmail(String to, String username) {
        String subject = "Bienvenido/a a la Biblioteca Online";

        // Cuerpo del mensaje de bienvenida
        String body = """
                Hola %s,
                
                Gracias por registrarte en la Biblioteca Online.
                
                A partir de ahora podrás consultar y gestionar tus libros,
                así como acceder a las distintas funcionalidades de la plataforma.
                
                Si no has realizado tú este registro, por favor ignora este mensaje.
                
                Un saludo,
                Equipo de la Biblioteca.
                """.formatted(username);

        // Llamamos al método genérico para enviar el correo
        sendSimpleMail(to, subject, body);
    }

    /**
     * Envío de correo de confirmación de cuenta con un enlace que incluye un token.
     *
     * @param to       dirección de correo del usuario
     * @param username nombre del usuario
     * @param token    token generado para confirmar la cuenta
     */
    public void sendAccountConfirmationEmail(String to, String username, String token) {
        // Construimos la URL de confirmación utilizando la URL base y el token
        String confirmUrl = baseUrl + "/confirm-account?token=" + token;

        String subject = "Confirmación de cuenta - Biblioteca Online";

        // Cuerpo del mensaje de confirmación de cuenta
        String body = """
                Hola %s,
                
                Gracias por registrarte en la Biblioteca Online.
                
                Para activar tu cuenta, por favor haz clic en el siguiente enlace:
                
                %s
                
                Si no has solicitado tú la creación de esta cuenta, puedes ignorar este correo.
                
                Un saludo,
                Equipo de la Biblioteca.
                """.formatted(username, confirmUrl);

        // Enviamos el correo de confirmación
        sendSimpleMail(to, subject, body);
    }

    /**
     * Envío de correo de recuperación de contraseña con un enlace que permite
     * al usuario establecer una nueva contraseña a partir de un token.
     *
     * @param to         dirección de correo del usuario
     * @param username   nombre del usuario
     * @param resetToken token generado para la recuperación de contraseña
     */
    public void sendPasswordRecoveryEmail(String to, String username, String resetToken) {
        // Construimos la URL de recuperación utilizando la URL base y el token
        String resetUrl = baseUrl + "/reset-password?token=" + resetToken;

        String subject = "Recuperación de contraseña - Biblioteca Online";

        // Cuerpo del mensaje de recuperación de contraseña
        String body = """
                Hola %s,
                
                Hemos recibido una solicitud para restablecer tu contraseña en la Biblioteca Online.
                
                Puedes establecer una nueva contraseña accediendo al siguiente enlace:
                
                %s
                
                Si no has solicitado tú el cambio de contraseña, ignora este mensaje.
                
                Un saludo,
                Equipo de la Biblioteca.
                """.formatted(username, resetUrl);

        // Enviamos el correo de recuperación
        sendSimpleMail(to, subject, body);
    }

    /**
     * Envío de un resumen de actividad al usuario.
     *
     * Este resumen puede contener información sobre compras realizadas,
     * libros añadidos a la biblioteca personal u otras acciones recientes.
     *
     * @param to               dirección de correo del usuario
     * @param username         nombre del usuario
     * @param resumenActividad texto con el resumen de actividad que se desee enviar
     */
    public void sendActivitySummaryEmail(String to, String username, String resumenActividad) {
        String subject = "Resumen de actividad - Biblioteca Online";

        // Cuerpo del mensaje de resumen de actividad
        String body = """
                Hola %s,
                
                A continuación se muestra un resumen de tu actividad reciente en la Biblioteca Online:
                
                %s
                
                Gracias por seguir utilizando nuestra plataforma.
                
                Un saludo,
                Equipo de la Biblioteca.
                """.formatted(username, resumenActividad);

        // Enviamos el correo de resumen de actividad
        sendSimpleMail(to, subject, body);
    }
}
