package edu.mondragon.webengl.pelukeria.controller;

import edu.mondragon.webengl.pelukeria.domain.chat.model.ChatMezua;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
 
@Controller
public class ChatController {
    // Página del chat
    @GetMapping("/chat")
    public String chat() {
        return "chat";
    }
 
    // Recibe mensajes de /app/chat.enviar y los reenvía a todos en /topic/chat
    @MessageMapping("/chat.enviar")
    @SendTo("/topic/chat")
    public ChatMezua enviarMensaje(ChatMezua mezua) {
        // El nombre ya viene en el mensaje desde el cliente
        return mezua;
    }
 
    // Notifica a todos cuando alguien se une al chat
    @MessageMapping("/chat.unirse")
    @SendTo("/topic/chat")
    public ChatMezua unirse(ChatMezua mezua) {
        mezua.setTestua(mezua.getIgorlea() + " se ha unido al chat");
        mezua.setIgorlea("Sistema");
        return mezua;
    }
}
