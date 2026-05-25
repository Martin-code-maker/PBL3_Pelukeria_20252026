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

    public Erabiltzailea save(Erabiltzailea book) {
        return erabiltzaileaRepository.save(book);
    }

    public void deleteById(Long id) {
        erabiltzaileaRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String izena) throws UsernameNotFoundException {
        Erabiltzailea erabiltzailea = erabiltzaileaRepository.findByIzena(izena)
        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + izena));

        return User.builder()
        .username(erabiltzailea.getIzena())
        .password(erabiltzailea.getPasahitza())
        .roles("USER")
        .build();
    }

    
}
