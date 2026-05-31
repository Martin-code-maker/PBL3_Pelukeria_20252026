package edu.mondragon.webengl.pelukeria.domain.erabiltzailea.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import edu.mondragon.webengl.pelukeria.domain.erabiltzailea.model.Erabiltzailea;
import edu.mondragon.webengl.pelukeria.domain.erabiltzailea.repository.ErabiltzaileaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ErabiltzaileaService implements UserDetailsService {

    @Autowired
    private ErabiltzaileaRepository erabiltzaileaRepository;
 
    public List<Erabiltzailea> findAll() {
        return erabiltzaileaRepository.findAll();
    }
 
    public Optional<Erabiltzailea> findById(Long id) {
        return erabiltzaileaRepository.findById(id);
    }
 
    public Erabiltzailea save(Erabiltzailea e) {
        return erabiltzaileaRepository.save(e);
    }
 
    public void deleteById(Long id) {
        erabiltzaileaRepository.deleteById(id);
    }
 
    @Override
    public UserDetails loadUserByUsername(String izena) throws UsernameNotFoundException {
        Erabiltzailea e = erabiltzaileaRepository.findByIzena(izena)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + izena));
 
        // Pasar el rol real al contexto de seguridad
        return User.builder()
                .username(e.getIzena())
                .password(e.getPasahitza())
                .roles(e.getRola())  // "USER" o "ADMIN"
                .build();
    }
}
