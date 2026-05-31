package edu.mondragon.webengl.pelukeria.domain.servicios.repository;

import edu.mondragon.webengl.pelukeria.domain.servicios.model.Servicios;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
 
public interface ServiciosRepository extends JpaRepository<Servicios, Long> {
    Optional<Servicios> findByKodea(String kodea);
}
 