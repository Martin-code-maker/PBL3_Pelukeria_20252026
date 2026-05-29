package edu.mondragon.webengl.pelukeria.domain.reservas.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import edu.mondragon.webengl.pelukeria.domain.reservas.model.Reserva;
import edu.mondragon.webengl.pelukeria.domain.reservas.repository.ReservaRepository;
import java.util.List;
import java.util.Optional;
 
@Service
public class ReservaService {
 
    @Autowired
    private ReservaRepository reservaRepository;
 
    public List<Reserva> findAll() {
        return reservaRepository.findAll();
    }
 
    public List<Reserva> findByErabiltzaileaId(Long id) {
        return reservaRepository.findByErabiltzaileaId(id);
    }
 
    public Optional<Reserva> findById(Long id) {
        return reservaRepository.findById(id);
    }
 
    public Reserva save(Reserva reserva) {
        return reservaRepository.save(reserva);
    }
 
    public void deleteById(Long id) {
        reservaRepository.deleteById(id);
    }
}