package edu.mondragon.webengl.pelukeria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import edu.mondragon.webengl.pelukeria.domain.erabiltzailea.model.Erabiltzailea;
import edu.mondragon.webengl.pelukeria.domain.erabiltzailea.repository.ErabiltzaileaRepository;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping(path="/pelukeria")
public class ErabiltzaileaController {
    @Autowired
    private ErabiltzaileaRepository erabiltzaileaRepository;

    @PostMapping(path = "add")
    public @ResponseBody String addNewErabiltzailea (@RequestParam String izena, @RequestParam String pasahitza, @RequestParam String gmail) {
        Erabiltzailea erabiltzailea = new Erabiltzailea();
        erabiltzailea.setIzena(izena);
        erabiltzailea.setPasahitza(pasahitza);
        erabiltzailea.setGmail(gmail);
        erabiltzaileaRepository.save(erabiltzailea);

        return "Erabiltzailea gordeta";
    }

    @GetMapping(path = "/all")
    public @ResponseBody Iterable<Erabiltzailea> getAllErabiltzaileak() {
        return erabiltzaileaRepository.findAll();// This returns a JSON or XML with the users
    }
    
    
}
