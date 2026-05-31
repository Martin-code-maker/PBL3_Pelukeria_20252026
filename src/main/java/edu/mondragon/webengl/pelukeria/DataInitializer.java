package edu.mondragon.webengl.pelukeria;

import edu.mondragon.webengl.pelukeria.domain.servicios.model.Servicios;
import edu.mondragon.webengl.pelukeria.domain.servicios.repository.ServiciosRepository;

import edu.mondragon.webengl.pelukeria.domain.erabiltzailea.model.Erabiltzailea;
import edu.mondragon.webengl.pelukeria.domain.erabiltzailea.repository.ErabiltzaileaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
 
@Component
public class DataInitializer implements CommandLineRunner {
    @Autowired
    private ErabiltzaileaRepository erabiltzaileaRepository;
 
    @Autowired
    private ServiciosRepository servizioRepository;
 
    @Autowired
    private PasswordEncoder passwordEncoder;
 
    @Override
    public void run(String... args) {
        // Crear admin si no existe
        if (erabiltzaileaRepository.findByIzena("admin").isEmpty()) {
            Erabiltzailea admin = new Erabiltzailea("admin", passwordEncoder.encode("admin123"), "admin@pelukeria.com");
            admin.setRola("ADMIN");
            erabiltzaileaRepository.save(admin);
            System.out.println("Admin creado: usuario=admin, contraseña=admin123");
        }
 
        // Crear servicios por defecto si la tabla está vacía
        if (servizioRepository.count() == 0) {
            servizioRepository.save(new Servicios("Corte de pelo", "corte",       15, 15.0, "bi-scissors"));
            servizioRepository.save(new Servicios("Barba",          "barba",       10, 10.0, "bi-person-bounding-box"));
            servizioRepository.save(new Servicios("Tinte",          "tinte",       30, 30.0, "bi-droplet-fill"));
            servizioRepository.save(new Servicios("Tratamiento",    "tratamiento", 25, 25.0, "bi-flower1"));
            System.out.println("Servicios por defecto creados.");
        }
    }
}
