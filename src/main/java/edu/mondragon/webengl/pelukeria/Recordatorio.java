package edu.mondragon.webengl.pelukeria;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import edu.mondragon.webengl.pelukeria.domain.reservas.model.Reserva;
import edu.mondragon.webengl.pelukeria.domain.reservas.service.EmailService;
import edu.mondragon.webengl.pelukeria.domain.reservas.service.ReservaService;
import java.time.LocalDateTime;
import java.util.List;
 
@Component
public class Recordatorio {
 
    @Autowired
    private ReservaService reservaService;
 
    @Autowired(required = false)
    private EmailService emailService;
 
    // Se ejecuta cada minuto
    // cron = "segundos minutos horas día mes díaSemana"
    @Scheduled(cron = "0 * * * * *")
    public void enviarRecordatorios() {
        if (emailService == null) return; // no disponible en tests
        // Rango de búsqueda: reservas que empiecen entre 60 y 61 minutos desde ahora
        LocalDateTime desde = LocalDateTime.now().plusMinutes(60);
        LocalDateTime hasta = LocalDateTime.now().plusMinutes(61);
 
        List<Reserva> proximasReservas = reservaService.findByHasieraBetween(desde, hasta);
 
        for (Reserva reserva : proximasReservas) {
            try {
                emailService.enviarRecordatorio(reserva);
                System.out.println("Recordatorio enviado a: " + reserva.getErabiltzailea().getGmail());
            } catch (Exception e) {
                // Si falla el envío no queremos que rompa el scheduler
                System.err.println("Error enviando recordatorio a "
                    + reserva.getErabiltzailea().getGmail() + ": " + e.getMessage());
            }
        }
    }
}