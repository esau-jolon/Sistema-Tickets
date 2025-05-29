/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistema.tickets;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

/**
 *
 * @author esauj
 */
public class EnvioCorreo {

    public static void enviarCredenciales(String destinatario, String nombreUsuario, String contrasena) {
        final String remitente = "ejolont@miumg.edu.gt";
        final String clave = "tkmjlkhpzmzcetec";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session sesion = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, clave);
            }
        });

        try {
            Message mensaje = new MimeMessage(sesion);
            mensaje.setFrom(new InternetAddress(remitente));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject("CREDENCIALES DE ACCESO");
            String contenidoHTML = """
            <h2 style='color:#17aeae;'>Bienvenido al Sistema de Tickets TaskFlow</h2>
            <p>Hola <b>%s</b>,</p>
            <p>A continuación se le brinda las credenciales de su usuario:</p>
            <p>Tu usuario es: <strong>%s</strong></p>
            <p>Tu contraseña es: <strong>%s</strong></p>
            <hr>
            <p style='font-size:12px;color:#555;'>Este es un mensaje automático, no respondas a este correo.</p>
            """.formatted(nombreUsuario, nombreUsuario, contrasena);

            mensaje.setContent(contenidoHTML, "text/html; charset=utf-8");

            Transport.send(mensaje);
            System.out.println("Correo enviado a: " + destinatario);
        } catch (MessagingException e) {
            System.err.println("Error al enviar correo: " + e.getMessage());
        }
    }

    public static void enviarNotificacionCambioEstado(String destinatario, int ticketId, String nuevoEstado) {
        final String remitente = "ejolont@miumg.edu.gt";
        final String clave = "tkmjlkhpzmzcetec";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session sesion = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, clave);
            }
        });

        try {
            Message mensaje = new MimeMessage(sesion);
            mensaje.setFrom(new InternetAddress(remitente));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject("Notificación de cambio de estado");

            String contenidoHTML = """
            <h2 style='color:#ff9800;'>CAMBIO DE ESTADO DE TICKET</h2>
            <p>Estimado usuario,</p>
            <p>Le informamos que su ticket con ID <strong>%s</strong> ha cambiado al estado <strong>%s</strong>.</p>
            <p>Por favor, ingrese al sistema si desea más detalles.</p>
            <hr>
            <p style='font-size:12px;color:#555;'>Este es un mensaje automático, no respondas a este correo.</p>
        """.formatted(ticketId, nuevoEstado);

            mensaje.setContent(contenidoHTML, "text/html; charset=utf-8");
            Transport.send(mensaje);
            System.out.println("Correo de notificación enviado a: " + destinatario);
        } catch (MessagingException e) {
            System.err.println("Error al enviar correo de notificación: " + e.getMessage());
        }
    }

}
