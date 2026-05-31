package edu.mondragon.webengl.pelukeria.controller;

import edu.mondragon.webengl.pelukeria.domain.erabiltzailea.service.ErabiltzaileaService;
import edu.mondragon.webengl.pelukeria.domain.reservas.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import edu.mondragon.webengl.pelukeria.domain.erabiltzailea.model.Erabiltzailea;
import edu.mondragon.webengl.pelukeria.domain.erabiltzailea.repository.ErabiltzaileaRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
//import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping(path="/pelukeria")
public class ErabiltzaileaController {

    @Autowired
    private ErabiltzaileaService erabiltzaileaService;
 
    @Autowired
    private ErabiltzaileaRepository erabiltzaileaRepository;
 
    @Autowired
    private ReservaService reservaService;
 
    @Autowired
    private PasswordEncoder passwordEncoder;
 
    ErabiltzaileaController(ErabiltzaileaService erabiltzaileaService) {
        this.erabiltzaileaService = erabiltzaileaService;
    }
 
    @PostMapping(path = "add")
    public @ResponseBody String addNewErabiltzailea(@RequestParam String izena,
                                                     @RequestParam String pasahitza,
                                                     @RequestParam String gmail) {
        Erabiltzailea e = new Erabiltzailea();
        e.setIzena(izena);
        e.setPasahitza(passwordEncoder.encode(pasahitza));
        e.setGmail(gmail);
        erabiltzaileaRepository.save(e);
        return "Erabiltzailea gordeta";
    }
 
    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Erabiltzailea> getAllErabiltzaileak() {
        return erabiltzaileaRepository.findAll();
    }
 
    @GetMapping
    public @ResponseBody List<Erabiltzailea> findAll() {
        return erabiltzaileaService.findAll();
    }
 
    @GetMapping("/{id}")
    public @ResponseBody Optional<Erabiltzailea> findById(@PathVariable Long id) {
        return erabiltzaileaService.findById(id);
    }
 
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public @ResponseBody Erabiltzailea create(@RequestBody Erabiltzailea e) {
        return erabiltzaileaService.save(e);
    }
 
    @PutMapping
    public @ResponseBody Erabiltzailea update(@RequestBody Erabiltzailea e) {
        return erabiltzaileaService.save(e);
    }
 
    // Borra primero las reservas del usuario y luego al usuario
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public @ResponseBody void deleteById(@PathVariable Long id) {
        // Borrar todas las reservas del usuario antes de borrar el usuario
        reservaService.findByErabiltzaileaId(id)
                .forEach(r -> reservaService.deleteById(r.getId()));
 
        erabiltzaileaService.deleteById(id);
    }
}
