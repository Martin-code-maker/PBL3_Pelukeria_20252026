package edu.mondragon.webengl.pelukeria.domain.erabiltzailea.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.repository.CrudRepository;
import edu.mondragon.webengl.pelukeria.domain.erabiltzailea.model.Erabiltzailea;

public interface ErabiltzaileaRepository extends JpaRepository<Erabiltzailea, Long> {
    Optional<Erabiltzailea> findByIzena(String izena);
    
}
