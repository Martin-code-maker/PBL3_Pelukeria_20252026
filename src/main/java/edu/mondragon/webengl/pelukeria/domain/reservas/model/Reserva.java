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

    @ManyToOne
    @JoinColumn(name = "erabiltzailea_id", nullable = false)
    private Erabiltzailea erabiltzailea;

    private LocalDateTime hasiera;
    private int iraupena;
    private String zerbitzuak;
    private double prezioa;

    // Descripción del corte que quiere el cliente
    @Column(length = 500)
    private String oharrak; // "notas"

    // Ruta relativa de la imagen subida (null si no subió ninguna)
    private String irudiPath; // "rutaImagen"

    public Reserva() {}

    public Reserva(Erabiltzailea erabiltzailea, LocalDateTime hasiera, int iraupena,
                   String zerbitzuak, double prezioa, String oharrak, String irudiPath) {
        this.erabiltzailea = erabiltzailea;
        this.hasiera       = hasiera;
        this.iraupena      = iraupena;
        this.zerbitzuak    = zerbitzuak;
        this.prezioa       = prezioa;
        this.oharrak       = oharrak;
        this.irudiPath     = irudiPath;
    }

    public Long getId()                          { return id; }
    public void setId(Long id)                   { this.id = id; }

    public Erabiltzailea getErabiltzailea()                       { return erabiltzailea; }
    public void setErabiltzailea(Erabiltzailea e)                 { this.erabiltzailea = e; }

    public LocalDateTime getHasiera()            { return hasiera; }
    public void setHasiera(LocalDateTime h)      { this.hasiera = h; }

    public int getIraupena()                     { return iraupena; }
    public void setIraupena(int i)               { this.iraupena = i; }

    public String getZerbitzuak()                { return zerbitzuak; }
    public void setZerbitzuak(String z)          { this.zerbitzuak = z; }

    public double getPrezioa()                   { return prezioa; }
    public void setPrezioa(double p)             { this.prezioa = p; }

    public String getOharrak()                   { return oharrak; }
    public void setOharrak(String o)             { this.oharrak = o; }

    public String getIrudiPath()                 { return irudiPath; }
    public void setIrudiPath(String i)           { this.irudiPath = i; }

    @Override
    public String toString() {
        return "Reserva [id=" + id + ", erabiltzailea=" + erabiltzailea.getIzena()
                + ", hasiera=" + hasiera + ", zerbitzuak=" + zerbitzuak + "]";
    }
}