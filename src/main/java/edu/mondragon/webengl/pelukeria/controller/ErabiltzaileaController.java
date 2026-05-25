package edu.mondragon.webengl.pelukeria.controller;

import edu.mondragon.webengl.pelukeria.domain.erabiltzailea.service.ErabiltzaileaService;
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
    private PasswordEncoder passwordEncoder;  // Inyecta el encoder de Spring Security


    ErabiltzaileaController(ErabiltzaileaService erabiltzaileaService) {
        this.erabiltzaileaService = erabiltzaileaService;
    }

    @PostMapping(path = "add")
    public @ResponseBody String addNewErabiltzailea (@RequestParam String izena, @RequestParam String pasahitza, @RequestParam String gmail) {
        Erabiltzailea erabiltzailea = new Erabiltzailea();
        erabiltzailea.setIzena(izena);
        erabiltzailea.setPasahitza(passwordEncoder.encode(pasahitza));
        //erabiltzailea.setPasahitza(pasahitza);
        erabiltzailea.setGmail(gmail);
        erabiltzaileaRepository.save(erabiltzailea);

        return "Erabiltzailea gordeta";
    }

    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Erabiltzailea> getAllErabiltzaileak() {
        return erabiltzaileaRepository.findAll();// This returns a JSON or XML with the users
    }

    @GetMapping
    public @ResponseBody List<Erabiltzailea> findAll() {
        return erabiltzaileaService.findAll();
    }
    
    @GetMapping("/{id}")
    public @ResponseBody Optional<Erabiltzailea> findById(@PathVariable Long id) {
        return erabiltzaileaService.findById(id);
    }

    // create a erabiltzailea
    @ResponseStatus(HttpStatus.CREATED) // 201
    @PostMapping
    public @ResponseBody Erabiltzailea create(@RequestBody Erabiltzailea erabiltzailea) {
        return erabiltzaileaService.save(erabiltzailea);
    }

    // update a erabiltzailea
    @PutMapping
    public @ResponseBody Erabiltzailea update(@RequestBody Erabiltzailea erabiltzailea) {
        return erabiltzaileaService.save(erabiltzailea);
    }

    // delete a erabiltzailea
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204
    @DeleteMapping("/{id}")
    public @ResponseBody void deleteById(@PathVariable Long id) {
        erabiltzaileaService.deleteById(id);
    }
    
}
