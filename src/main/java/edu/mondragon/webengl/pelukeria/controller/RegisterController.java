package edu.mondragon.webengl.pelukeria.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.mondragon.webengl.pelukeria.domain.erabiltzailea.model.Erabiltzailea;
import edu.mondragon.webengl.pelukeria.domain.erabiltzailea.repository.ErabiltzaileaRepository;

@Controller
public class RegisterController {

    @Autowired
    private ErabiltzaileaRepository erabiltzaileaRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/register")
    public String showRegisterForm() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String izena,
                               @RequestParam String gmail,
                               @RequestParam String pasahitza,
                               RedirectAttributes redirectAttributes) {
        
        // Verificar si el usuario ya existe
        if (erabiltzaileaRepository.findByIzena(izena).isPresent()) {
            redirectAttributes.addAttribute("error", "El usuario ya existe");
            return "redirect:/register";
        }
        
        // Crear nuevo usuario
        Erabiltzailea newUser = new Erabiltzailea();
        newUser.setIzena(izena);
        newUser.setGmail(gmail);
        newUser.setPasahitza(passwordEncoder.encode(pasahitza));
        
        erabiltzaileaRepository.save(newUser);
        
        // Redirigir al login con mensaje de éxito
        redirectAttributes.addAttribute("registered", true);
        return "redirect:/login";
    }
}