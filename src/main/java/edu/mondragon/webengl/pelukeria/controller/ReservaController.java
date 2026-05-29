package edu.mondragon.webengl.pelukeria.controller;

import edu.mondragon.webengl.pelukeria.domain.erabiltzailea.model.Erabiltzailea;
import edu.mondragon.webengl.pelukeria.domain.erabiltzailea.repository.ErabiltzaileaRepository;
import edu.mondragon.webengl.pelukeria.domain.reservas.model.Reserva;
import edu.mondragon.webengl.pelukeria.domain.reservas.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
 
@Controller
@RequestMapping("/reservas")
public class ReservaController {
 
    @Autowired
    private ReservaService reservaService;
 
    @Autowired
    private ErabiltzaileaRepository erabiltzaileaRepository;
 
    // GET /reservas -> muestra el calendario con las reservas del usuario
    @GetMapping
    public String mostrarReservas(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Erabiltzailea erabiltzailea = erabiltzaileaRepository.findByIzena(userDetails.getUsername())
                .orElseThrow();
 
        List<Reserva> misReservas = reservaService.findByErabiltzaileaId(erabiltzailea.getId());
        model.addAttribute("misReservas", misReservas);
        model.addAttribute("todasReservas", reservaService.findAll());
        model.addAttribute("erabiltzailea", erabiltzailea);
        return "reservas";
    }
 
    // POST /reservas -> guarda una nueva reserva enviada desde el formulario JS
    @PostMapping
    public String crearReserva(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String fecha,       // "2025-06-02"
            @RequestParam String hora,        // "10:00"
            @RequestParam String servicios,   // "corte,barba"
            @RequestParam int duracion,       // minutos totales
            @RequestParam double precio) {
 
        Erabiltzailea erabiltzailea = erabiltzaileaRepository.findByIzena(userDetails.getUsername())
                .orElseThrow();
 
        LocalDateTime hasiera = LocalDateTime.of(
                LocalDate.parse(fecha),
                LocalTime.parse(hora)
        );
 
        Reserva reserva = new Reserva(erabiltzailea, hasiera, duracion, servicios, precio);
        reservaService.save(reserva);
 
        return "redirect:/reservas";
    }
 
    // DELETE /reservas/{id} -> cancela una reserva del usuario
    @PostMapping("/{id}/delete")
    public String cancelarReserva(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        reservaService.findById(id).ifPresent(r -> {
            if (r.getErabiltzailea().getIzena().equals(userDetails.getUsername())) {
                reservaService.deleteById(id);
            }
        });
        return "redirect:/reservas";
    }
}