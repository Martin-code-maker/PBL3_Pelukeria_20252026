/* ============================================
   RESERVAS - JavaScript
   ============================================ */

// --- Configuración de servicios ---
const SERVICIOS = {
    corte:       { nombre: "Corte de pelo",      duracion: 15, precio: 15 },
    barba:       { nombre: "Barba",              duracion: 10, precio: 10 },
    tinte:       { nombre: "Tinte",              duracion: 30, precio: 30 },
    tratamiento: { nombre: "Tratamiento capilar",duracion: 25, precio: 25 }
};

const HORAS_SEMANA = ["09:00","10:00","11:00","12:00","13:00","14:00","15:00","16:00","17:00","18:00","19:00"];
const HORAS_SABADO = ["10:00","11:00","12:00","13:00"];

// Estado global
let weekOffset   = 0;
let selectedCell = null;
let selectedDate = null;
let selectedHour = null;
let reservasOcupadas = {};
let misReservas      = {};

// -------------------------------------------------------
// Inicialización: se ejecuta cuando el DOM está listo.
// Usamos tanto DOMContentLoaded como un fallback por si
// el script se inyecta tarde (dentro del layout Thymeleaf).
// -------------------------------------------------------
function initReservas() {
    // Leer reservas desde los <span> generados por Thymeleaf
    document.querySelectorAll(".slot-ocupado").forEach(el => {
        reservasOcupadas[el.dataset.key] = true;
    });
    document.querySelectorAll(".slot-mio").forEach(el => {
        misReservas[el.dataset.key] = true;
    });

    buildCalendar();
}

// Si el DOM ya está listo (script cargado tarde dentro del fragmento)
if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initReservas);
} else {
    initReservas();
}

// --- Modo: Calendario o Buscar ---
function setMode(mode) {
    document.getElementById("btn-calendar").classList.toggle("active", mode === "calendar");
    document.getElementById("btn-search").classList.toggle("active", mode === "search");
    document.getElementById("calendar-section").style.display = mode === "calendar" ? "block" : "none";
    document.getElementById("search-panel").classList.toggle("visible", mode === "search");
}

// --- Navegación de semanas ---
function changeWeek(delta) {
    weekOffset += delta;
    buildCalendar();
}

function getMondayOfWeek(offset) {
    const today = new Date();
    const day = today.getDay();
    const diff = today.getDate() - day + (day === 0 ? -6 : 1);
    const monday = new Date(today.setDate(diff + offset * 7));
    monday.setHours(0, 0, 0, 0);
    return monday;
}

function formatDate(date) {
    return date.toISOString().split("T")[0];
}

function formatDateLabel(date) {
    return date.toLocaleDateString("es-ES", { day: "numeric", month: "short" });
}

// --- Construcción del calendario ---
function buildCalendar() {
    const monday = getMondayOfWeek(weekOffset);
    const grid = document.getElementById("calendar-grid");
    grid.innerHTML = "";

    const dayNames = ["Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"];
    const weekDates = [];

    // Celda esquina vacía
    const corner = document.createElement("div");
    corner.className = "cal-header-cell corner";
    grid.appendChild(corner);

    // Cabecera con días
    for (let i = 0; i < 6; i++) {
        const d = new Date(monday);
        d.setDate(monday.getDate() + i);
        weekDates.push(d);

        const cell = document.createElement("div");
        cell.className = "cal-header-cell" + (i === 5 ? " sat" : "");
        cell.innerHTML = dayNames[i] + "<br><span style='font-size:0.68rem;opacity:0.8'>" + formatDateLabel(d) + "</span>";
        grid.appendChild(cell);
    }

    // Label de semana
    document.getElementById("week-label").textContent =
        formatDateLabel(weekDates[0]) + " – " + formatDateLabel(weekDates[5]);

    // Filas de horas
    HORAS_SEMANA.forEach(hora => {
        const timeCell = document.createElement("div");
        timeCell.className = "cal-time-cell";
        timeCell.textContent = hora;
        grid.appendChild(timeCell);

        weekDates.forEach(function(date, i) {
            const isSabado = (i === 5);
            const cell = document.createElement("div");
            const key = formatDate(date) + "T" + hora;

            if (isSabado && !HORAS_SABADO.includes(hora)) {
                cell.className = "cal-slot closed";
            } else if (misReservas[key]) {
                cell.className = "cal-slot taken-mine";
                cell.textContent = "Mía";
            } else if (reservasOcupadas[key]) {
                cell.className = "cal-slot taken";
                cell.textContent = "Ocup.";
            } else {
                cell.className = "cal-slot available";
                cell.innerHTML = "<i class='bi bi-check2'></i>";
                cell.onclick = (function(d, h, c) {
                    return function() { openModal(d, h, c); };
                })(date, hora, cell);
            }

            grid.appendChild(cell);
        });
    });
}

// --- Modal ---
function openModal(date, hora, cell) {
    if (selectedCell) selectedCell.classList.remove("selected");
    selectedCell = cell;
    if (cell) cell.classList.add("selected");

    selectedDate = formatDate(date);
    selectedHour = hora;

    const label = date.toLocaleDateString("es-ES", { weekday: "long", day: "numeric", month: "long" });
    document.getElementById("modal-slot-label").textContent = label + " · " + hora;

    // Resetear servicios
    document.querySelectorAll(".service-item").forEach(item => {
        item.classList.remove("checked");
        item.querySelector("input").checked = false;
    });

    updateModalSummary();
    document.getElementById("modal-backdrop").classList.add("visible");
}

function closeModal(e) {
    if (!e || e.target === document.getElementById("modal-backdrop")) {
        document.getElementById("modal-backdrop").classList.remove("visible");
        if (selectedCell) selectedCell.classList.remove("selected");
        selectedCell = null;
    }
}

// --- Toggle servicio ---
function toggleService(item) {
    item.classList.toggle("checked");
    item.querySelector("input").checked = item.classList.contains("checked");
    updateModalSummary();
}

function updateModalSummary() {
    let duracion = 0;
    let precio   = 0;

    document.querySelectorAll(".service-item.checked").forEach(item => {
        const key = item.dataset.service;
        duracion += SERVICIOS[key].duracion;
        precio   += SERVICIOS[key].precio;
    });

    document.getElementById("summary-duration").textContent = duracion > 0 ? duracion + " min" : "—";
    document.getElementById("summary-price").textContent    = precio > 0   ? precio + " €"     : "—";

    if (duracion > 0 && selectedHour) {
        const parts = selectedHour.split(":");
        const fin = new Date();
        fin.setHours(parseInt(parts[0]), parseInt(parts[1]) + duracion, 0);
        const hh = String(fin.getHours()).padStart(2, "0");
        const mm = String(fin.getMinutes()).padStart(2, "0");
        document.getElementById("summary-end").textContent = hh + ":" + mm;
        document.getElementById("summary-end-row").style.display = "flex";
    } else {
        document.getElementById("summary-end-row").style.display = "none";
    }

    document.getElementById("btn-confirm").disabled = (duracion === 0);
}

// --- Confirmar reserva ---
function confirmBooking() {
    const checked = document.querySelectorAll(".service-item.checked");
    if (checked.length === 0) return;

    const servicios = Array.from(checked).map(i => i.dataset.service).join(",");
    let duracion = 0, precio = 0;
    checked.forEach(i => {
        duracion += SERVICIOS[i.dataset.service].duracion;
        precio   += SERVICIOS[i.dataset.service].precio;
    });

    document.getElementById("form-fecha").value     = selectedDate;
    document.getElementById("form-hora").value      = selectedHour;
    document.getElementById("form-servicios").value = servicios;
    document.getElementById("form-duracion").value  = duracion;
    document.getElementById("form-precio").value    = precio;
    document.getElementById("booking-form").submit();
}

// --- Panel de búsqueda ---
function recalcFilterSummary() {
    let d = 0, p = 0;
    document.querySelectorAll("#search-panel input[type=checkbox]:checked").forEach(cb => {
        d += SERVICIOS[cb.value].duracion;
        p += SERVICIOS[cb.value].precio;
    });
    document.getElementById("filter-duration-label").textContent = "Duración: " + (d > 0 ? d + " min" : "—");
    document.getElementById("filter-price-label").textContent    = "Total: "    + (p > 0 ? p + " €"   : "—");
}

function showSearchResults() {
    const grid  = document.getElementById("search-results-grid");
    const panel = document.getElementById("search-results");
    grid.innerHTML = "";

    const monday   = getMondayOfWeek(weekOffset);
    const dayNames = ["Lunes","Martes","Miércoles","Jueves","Viernes","Sábado"];
    let found = 0;

    for (let i = 0; i < 6; i++) {
        const date  = new Date(monday);
        date.setDate(monday.getDate() + i);
        const hours = (i === 5) ? HORAS_SABADO : HORAS_SEMANA;

        hours.forEach(hora => {
            const key = formatDate(date) + "T" + hora;
            if (!reservasOcupadas[key] && !misReservas[key]) {
                const el = document.createElement("div");
                el.className = "result-slot";
                el.innerHTML = "<div style='font-size:0.72rem;color:#999'>" + dayNames[i] + "</div>" +
                               "<div style='font-size:1rem;font-weight:700'>" + hora + "</div>" +
                               "<div style='font-size:0.72rem;color:#2e7d52'>Libre</div>";
                const capDate = new Date(date);
                const capHora = hora;
                el.onclick = function() {
                    setMode("calendar");
                    setTimeout(function() { openModal(capDate, capHora, null); }, 50);
                };
                grid.appendChild(el);
                found++;
            }
        });
    }

    if (found === 0) {
        grid.innerHTML = "<p style='color:#999;font-size:0.875rem'>No hay huecos libres esta semana.</p>";
    }

    panel.style.display = "block";
}