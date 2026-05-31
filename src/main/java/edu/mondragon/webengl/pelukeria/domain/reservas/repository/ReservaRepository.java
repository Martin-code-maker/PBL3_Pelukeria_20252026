package edu.mondragon.webengl.pelukeria.domain.reservas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import edu.mondragon.webengl.pelukeria.domain.reservas.model.Reserva;
import java.time.LocalDateTime;
import java.util.List;
 
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    // Todas las reservas de un usuario concreto
    List<Reserva> findByErabiltzaileaId(Long erabiltzaileaId);
    List<Reserva> findByHasieraBetween(LocalDateTime desde, LocalDateTime hasta);
}