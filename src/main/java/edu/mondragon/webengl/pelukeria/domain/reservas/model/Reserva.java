package edu.mondragon.webengl.pelukeria.domain.reservas.model;

import edu.mondragon.webengl.pelukeria.domain.erabiltzailea.model.Erabiltzailea;
import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    // Muchas reservas -> un usuario
    @ManyToOne
    @JoinColumn(name = "erabiltzailea_id", nullable = false)
    private Erabiltzailea erabiltzailea;
 
    // Fecha y hora de inicio de la cita
    private LocalDateTime hasiera; // "inicio" en euskera
 
    // Duración total en minutos (suma de los servicios elegidos)
    private int iraupena; // "duración"
 
    // Servicios elegidos guardados como texto separado por comas
    // Ej: "corte,barba" o "tinte,tratamiento"
    private String zerbitzuak; // "servicios"
 
    // Precio total en euros
    private double prezioa; // "precio"
 
    public Reserva() {}
 
    public Reserva(Erabiltzailea erabiltzailea, LocalDateTime hasiera, int iraupena, String zerbitzuak, double prezioa) {
        this.erabiltzailea = erabiltzailea;
        this.hasiera = hasiera;
        this.iraupena = iraupena;
        this.zerbitzuak = zerbitzuak;
        this.prezioa = prezioa;
    }
 
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
 
    public Erabiltzailea getErabiltzailea() { return erabiltzailea; }
    public void setErabiltzailea(Erabiltzailea erabiltzailea) { this.erabiltzailea = erabiltzailea; }
 
    public LocalDateTime getHasiera() { return hasiera; }
    public void setHasiera(LocalDateTime hasiera) { this.hasiera = hasiera; }
 
    public int getIraupena() { return iraupena; }
    public void setIraupena(int iraupena) { this.iraupena = iraupena; }
 
    public String getZerbitzuak() { return zerbitzuak; }
    public void setZerbitzuak(String zerbitzuak) { this.zerbitzuak = zerbitzuak; }
 
    public double getPrezioa() { return prezioa; }
    public void setPrezioa(double prezioa) { this.prezioa = prezioa; }
 
    @Override
    public String toString() {
        return "Reserva [id=" + id + ", erabiltzailea=" + erabiltzailea.getIzena()
                + ", hasiera=" + hasiera + ", zerbitzuak=" + zerbitzuak + ", prezioa=" + prezioa + "]";
    }
}
