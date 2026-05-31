/* ============================================
   CHAT - JavaScript (WebSocket con STOMP)
   ============================================ */

let stompClient = null;
const username  = document.getElementById("user-data").dataset.username;

// Conectar al WebSocket al cargar la página
connect();

function connect() {
    const socket = new SockJS("/ws-chat");
    stompClient = Stomp.over(socket);

    // Silenciar logs de STOMP en consola
    stompClient.debug = null;

    stompClient.connect({}, function () {
        // Suscribirse al topic del chat
        stompClient.subscribe("/topic/chat", function (mensaje) {
            mostrarMensaje(JSON.parse(mensaje.body));
        });

        // Notificar que el usuario se ha unido
        stompClient.send("/app/chat.unirse", {}, JSON.stringify({ igorlea: username }));

        document.getElementById("chat-status").textContent = "Conectado ✓";
    }, function () {
        document.getElementById("chat-status").textContent = "Desconectado";
        // Reintentar conexión cada 5 segundos
        setTimeout(connect, 5000);
    });
}

function enviarMensaje() {
    const input = document.getElementById("msg-input");
    const texto = input.value.trim();
    if (!texto || !stompClient) return;

    stompClient.send("/app/chat.enviar", {}, JSON.stringify({
        igorlea: username,
        testua:  texto
    }));

    input.value = "";
}

function mostrarMensaje(mezua) {
    const container = document.getElementById("chat-messages");
    const div       = document.createElement("div");

    // Determinar tipo: propio, ajeno o sistema
    if (mezua.igorlea === "Sistema") {
        div.className = "msg system";
        div.innerHTML = `<div class="msg-bubble">${escapeHtml(mezua.testua)}</div>`;
    } else {
        const esMio = mezua.igorlea === username;
        div.className = "msg " + (esMio ? "mine" : "other");
        div.innerHTML = `
            ${!esMio ? `<span class="msg-meta">${escapeHtml(mezua.igorlea)}</span>` : ""}
            <div class="msg-bubble">${escapeHtml(mezua.testua)}</div>
            <span class="msg-meta">${mezua.ordua || ""}</span>
        `;
    }

    container.appendChild(div);
    // Scroll al último mensaje
    container.scrollTop = container.scrollHeight;
}

// Evitar XSS escapando el HTML
function escapeHtml(text) {
    return text
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;");
}