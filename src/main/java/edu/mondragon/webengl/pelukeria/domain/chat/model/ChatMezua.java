package edu.mondragon.webengl.pelukeria.domain.chat.model;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

// No es una entidad JPA — los mensajes solo viven en memoria (no se guardan en BD)
public class ChatMezua {

    private String igorlea;  // quien envía
    private String testua;   // texto del mensaje
    private String ordua;    // hora de envío
 
    public ChatMezua() {}
 
    public ChatMezua(String igorlea, String testua) {
        this.igorlea = igorlea;
        this.testua  = testua;
        this.ordua   = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
    }
 
    public String getIgorlea()           { return igorlea; }
    public void setIgorlea(String i)     { this.igorlea = i; }
    public String getTestua()            { return testua; }
    public void setTestua(String t)      { this.testua = t; }
    public String getOrdua()             { return ordua; }
    public void setOrdua(String o)       { this.ordua = o; }
    
}
