package edu.mondragon.webengl.pelukeria.domain.erabiltzailea.model;

//import org.jspecify.annotations.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "erabiltzaileak") //Datu basean tablaren izena
public class Erabiltzailea {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    
    private Long id;
    private String izena;
    private String pasahitza;
    private String gmail;

    
    public Erabiltzailea() {} // for JPA only, no use
    public Erabiltzailea(String izena, String pasahitza, String gmail) {
        this.izena = izena;
        this.pasahitza = pasahitza;
        this.gmail = gmail;
    }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getIzena() {
        return izena;
    }
    public void setIzena(String izena) {
        this.izena = izena;
    }
    public String getPasahitza() {
        return pasahitza;
    }
    public void setPasahitza(String pasahitza) {
        this.pasahitza = pasahitza;
    }
    public String getGmail() {
        return gmail;
    }
    public void setGmail(String gmail) {
        this.gmail = gmail;
    }
    
    @Override
    public String toString() {
        return "Erabiltzailea [id=" + id + ", izena=" + izena + ", pasahitza=" + pasahitza
                + ", gmail=" + gmail + "]";
    }
}





