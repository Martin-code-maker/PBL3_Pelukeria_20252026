package edu.mondragon.webengl.pelukeria.controller;

import edu.mondragon.webengl.pelukeria.domain.erabiltzailea.model.Erabiltzailea;
import edu.mondragon.webengl.pelukeria.domain.erabiltzailea.repository.ErabiltzaileaRepository;
import edu.mondragon.webengl.pelukeria.domain.reservas.model.Reserva;
import edu.mondragon.webengl.pelukeria.domain.reservas.service.EmailService;
import edu.mondragon.webengl.pelukeria.domain.reservas.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/reservas")
public class ReservaController {

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private ErabiltzaileaRepository erabiltzaileaRepository;

    @Autowired
    private EmailService emailService;

    // Carpeta donde se guardan las imágenes (configurable en application.properties)
    // Por defecto: carpeta "uploads" dentro del directorio de trabajo
    @Value("${pelukeria.uploads.dir:uploads}")
    private String uploadsDir;

    // GET /reservas
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

    // POST /reservas -> guarda la reserva con descripción e imagen opcional
    @PostMapping
    public String crearReserva(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam String fecha,
            @RequestParam String hora,
            @RequestParam String servicios,
            @RequestParam int duracion,
            @RequestParam double precio,
            @RequestParam(required = false) String oharrak,
            @RequestParam(required = false) MultipartFile imagen) throws IOException {

        Erabiltzailea erabiltzailea = erabiltzaileaRepository.findByIzena(userDetails.getUsername())
                .orElseThrow();

        LocalDateTime hasiera = LocalDateTime.of(
                LocalDate.parse(fecha),
                LocalTime.parse(hora)
        );

        // Guardar imagen si el usuario subió una
        String irudiPath = null;
        if (imagen != null && !imagen.isEmpty()) {
            irudiPath = guardarImagen(imagen, erabiltzailea.getId());
        }

        Reserva reserva = new Reserva(erabiltzailea, hasiera, duracion, servicios, precio, oharrak, irudiPath);
        reservaService.save(reserva);

        return "redirect:/reservas";
    }

    // POST /reservas/{id}/delete -> cancela una reserva del usuario
    @PostMapping("/{id}/delete")
    public String cancelarReserva(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        reservaService.findById(id).ifPresent(r -> {
            if (r.getErabiltzailea().getIzena().equals(userDetails.getUsername())) {
                // Borrar la imagen del disco si existe
                if (r.getIrudiPath() != null) {
                    try { Files.deleteIfExists(Paths.get(uploadsDir, r.getIrudiPath())); }
                    catch (IOException ignored) {}
                }
                reservaService.deleteById(id);
            }
        });
        return "redirect:/reservas";
    }

    // ENDPOINT DE PRUEBA - borrar cuando confirmes que el email funciona
    @GetMapping("/test-email")
    @ResponseBody
    public String testEmail(@AuthenticationPrincipal UserDetails userDetails) {
        Erabiltzailea e = erabiltzaileaRepository.findByIzena(userDetails.getUsername()).orElseThrow();
        Reserva r = new Reserva(e, LocalDateTime.now().plusMinutes(1), 15, "corte", 15.0, "Prueba", null);
        emailService.enviarRecordatorio(r);
        return "Email enviado a " + e.getGmail();
    }

    // Guarda la imagen en disco y devuelve el nombre de fichero generado
    private String guardarImagen(MultipartFile file, Long userId) throws IOException {
        // Crear carpeta si no existe
        Path dir = Paths.get(uploadsDir);
        Files.createDirectories(dir);

        // Nombre único para evitar colisiones: uuid + extensión original
        String extension = "";
        String original  = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            extension = original.substring(original.lastIndexOf("."));
        }
        String nombreFichero = "reserva_" + userId + "_" + UUID.randomUUID() + extension;

        Files.copy(file.getInputStream(), dir.resolve(nombreFichero), StandardCopyOption.REPLACE_EXISTING);
        return nombreFichero;
    }
}