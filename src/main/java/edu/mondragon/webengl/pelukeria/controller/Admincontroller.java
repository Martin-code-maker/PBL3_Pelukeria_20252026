package edu.mondragon.webengl.pelukeria.controller;

import edu.mondragon.webengl.pelukeria.domain.erabiltzailea.model.Erabiltzailea;
import edu.mondragon.webengl.pelukeria.domain.erabiltzailea.repository.ErabiltzaileaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
 import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import edu.mondragon.webengl.pelukeria.domain.reservas.model.Reserva;
import edu.mondragon.webengl.pelukeria.domain.reservas.service.EmailService;
import edu.mondragon.webengl.pelukeria.domain.reservas.service.ReservaService;
import edu.mondragon.webengl.pelukeria.domain.servicios.model.Servicios;
import edu.mondragon.webengl.pelukeria.domain.servicios.service.ServiciosService;




@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")

public class Admincontroller {
    @Autowired private ReservaService reservaService;
    @Autowired private ServiciosService serviciosService;
    @Autowired private ErabiltzaileaRepository erabiltzaileaRepository;
    @Autowired private EmailService emailService;
 
    // ---- Panel principal admin ----
    @GetMapping
    public String panel(Model model) {
        model.addAttribute("todasReservas", reservaService.findAll());
        model.addAttribute("servicios", serviciosService.findAll());
        model.addAttribute("usuarios", erabiltzaileaRepository.findAll());
        return "admin/panel";
    }
 
    // ---- Gestión de reservas ----
 
    // Formulario para editar hora de una reserva
    @GetMapping("/reservas/{id}/editar")
    public String editarReservaForm(@PathVariable Long id, Model model) {
        Reserva r = reservaService.findById(id).orElseThrow();
        model.addAttribute("reserva", r);
        return "admin/editar-reserva";
    }
 
    // Guardar nueva hora
    @PostMapping("/reservas/{id}/editar")
    public String editarReserva(@PathVariable Long id,
                                @RequestParam String fecha,
                                @RequestParam String hora) {
        Reserva r = reservaService.findById(id).orElseThrow();
        LocalDateTime nuevaHora = LocalDateTime.of(LocalDate.parse(fecha), LocalTime.parse(hora));
        r.setHasiera(nuevaHora);
        reservaService.save(r);
 
        // Notificar al cliente por email
        try {
            emailService.enviarCambioReserva(r);
        } catch (Exception e) {
            System.err.println("Error enviando email de cambio: " + e.getMessage());
        }
 
        return "redirect:/admin";
    }
 
    // Eliminar reserva de un cliente
    @PostMapping("/reservas/{id}/eliminar")
    public String eliminarReserva(@PathVariable Long id) {
        Reserva r = reservaService.findById(id).orElseThrow();
 
        // Notificar al cliente antes de borrar
        try {
            emailService.enviarCancelacionAdmin(r);
        } catch (Exception e) {
            System.err.println("Error enviando email de cancelación: " + e.getMessage());
        }
 
        reservaService.deleteById(id);
        return "redirect:/admin";
    }
 
    // ---- Gestión de servicios ----
 
    @GetMapping("/servicios/nuevo")
    public String nuevoServicioForm(Model model) {
        model.addAttribute("servicio", new Servicios());
        return "admin/editar-servicio";
    }
 
    @GetMapping("/servicios/{id}/editar")
    public String editarServicioForm(@PathVariable Long id, Model model) {
        model.addAttribute("servicios", serviciosService.findById(id).orElseThrow());
        return "admin/editar-servicios";
    }
 
    @PostMapping("/servicios/guardar")
    public String guardarServicio(@RequestParam(required = false) Long id,
                                  @RequestParam String izena,
                                  @RequestParam String kodea,
                                  @RequestParam int iraupena,
                                  @RequestParam double prezioa,
                                  @RequestParam String ikonoa) {
        Servicios s = id != null ? serviciosService.findById(id).orElse(new Servicios()) : new Servicios();
        s.setIzena(izena);
        s.setKodea(kodea);
        s.setIraupena(iraupena);
        s.setPrezioa(prezioa);
        s.setIkonoa(ikonoa);
        serviciosService.save(s);
        return "redirect:/admin";
    }
 
    @PostMapping("/servicios/{id}/eliminar")
    public String eliminarServicio(@PathVariable Long id) {
        serviciosService.deleteById(id);
        return "redirect:/admin";
    }
}
