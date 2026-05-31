package edu.mondragon.webengl.pelukeria.domain.reservas.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import edu.mondragon.webengl.pelukeria.domain.reservas.model.Reserva;
import java.time.format.DateTimeFormatter;
 
@Service
public class EmailService {
 
    @Autowired(required = false)
    private JavaMailSender mailSender;
 
    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern("EEEE d 'de' MMMM 'a las' HH:mm");
 
    // Recordatorio 1 hora antes
    public void enviarRecordatorio(Reserva reserva) {
        enviar(
            reserva.getErabiltzailea().getGmail(),
            "⏰ Recordatorio - Tu cita en Pelukeria en 1 hora",
            "Hola " + reserva.getErabiltzailea().getIzena() + ",\n\n" +
            "Te recordamos que tienes una cita " + reserva.getHasiera().format(FORMATO) + ".\n\n" +
            "Servicios: " + reserva.getZerbitzuak().replace(",", ", ") + "\n" +
            "Duración: " + reserva.getIraupena() + " min · Precio: " + reserva.getPrezioa() + " €\n\n" +
            (reserva.getOharrak() != null && !reserva.getOharrak().isEmpty()
                ? "Tus notas: " + reserva.getOharrak() + "\n\n" : "") +
            "¡Hasta pronto!\nPelukeria"
        );
    }
 
    // El admin ha cambiado la hora
    public void enviarCambioReserva(Reserva reserva) {
        enviar(
            reserva.getErabiltzailea().getGmail(),
            "📅 Tu cita en Pelukeria ha sido modificada",
            "Hola " + reserva.getErabiltzailea().getIzena() + ",\n\n" +
            "Tu cita ha sido modificada por el equipo de Pelukeria.\n\n" +
            "Nueva fecha y hora: " + reserva.getHasiera().format(FORMATO) + "\n" +
            "Servicios: " + reserva.getZerbitzuak().replace(",", ", ") + "\n\n" +
            "Si tienes alguna duda contacta con nosotros.\n\nPelukeria"
        );
    }
 
    // El admin ha cancelado la reserva
    public void enviarCancelacionAdmin(Reserva reserva) {
        enviar(
            reserva.getErabiltzailea().getGmail(),
            "❌ Tu cita en Pelukeria ha sido cancelada",
            "Hola " + reserva.getErabiltzailea().getIzena() + ",\n\n" +
            "Lamentablemente tu cita del " + reserva.getHasiera().format(FORMATO) +
            " ha sido cancelada por el equipo de Pelukeria.\n\n" +
            "Por favor, accede a la web para reservar una nueva cita.\n\n" +
            "Disculpa las molestias.\nPelukeria"
        );
    }
 
    private void enviar(String destinatario, String asunto, String cuerpo) {
        if (mailSender == null) {
            System.out.println("Mail desactivado - no se envía a: " + destinatario);
            return;
        }
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject(asunto);
        mensaje.setText(cuerpo);
        mailSender.send(mensaje);
    }
}