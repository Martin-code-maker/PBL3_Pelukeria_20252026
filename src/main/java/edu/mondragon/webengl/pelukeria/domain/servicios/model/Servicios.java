package edu.mondragon.webengl.pelukeria.domain.servicios.model;

import jakarta.persistence.*;
 
@Entity
@Table(name = "zerbitzuak")
public class Servicios {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    private String izena;       // nombre: "Corte de pelo"
    private String kodea;       // clave única: "corte", "barba"...
    private int iraupena;       // duración en minutos
    private double prezioa;     // precio en euros
    private String ikonoa;      // icono Bootstrap: "bi-scissors"
 
    public Servicios() {}
 
    public Servicios(String izena, String kodea, int iraupena, double prezioa, String ikonoa) {
        this.izena    = izena;
        this.kodea    = kodea;
        this.iraupena = iraupena;
        this.prezioa  = prezioa;
        this.ikonoa   = ikonoa;
    }
 
    public Long getId()                  { return id; }
    public void setId(Long id)           { this.id = id; }
    public String getIzena()             { return izena; }
    public void setIzena(String izena)   { this.izena = izena; }
    public String getKodea()             { return kodea; }
    public void setKodea(String kodea)   { this.kodea = kodea; }
    public int getIraupena()             { return iraupena; }
    public void setIraupena(int i)       { this.iraupena = i; }
    public double getPrezioa()           { return prezioa; }
    public void setPrezioa(double p)     { this.prezioa = p; }
    public String getIkonoa()            { return ikonoa; }
    public void setIkonoa(String i)      { this.ikonoa = i; }
}
