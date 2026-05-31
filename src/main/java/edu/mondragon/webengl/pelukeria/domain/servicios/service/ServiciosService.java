package edu.mondragon.webengl.pelukeria.domain.servicios.service;

import edu.mondragon.webengl.pelukeria.domain.servicios.model.Servicios;
import edu.mondragon.webengl.pelukeria.domain.servicios.repository.ServiciosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
 
@Service
public class ServiciosService {
    @Autowired
    private ServiciosRepository servicioRepository;
 
    public List<Servicios> findAll()              { return servicioRepository.findAll(); }
    public Optional<Servicios> findById(Long id)  { return servicioRepository.findById(id); }
    public Servicios save(Servicios s)             { return servicioRepository.save(s); }
    public void deleteById(Long id)              { servicioRepository.deleteById(id); }
}
