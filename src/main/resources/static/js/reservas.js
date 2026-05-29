/* ============================================
   RESERVAS - JavaScript
   Granularidad: slots de 10 minutos
   ============================================ */

const SERVICIOS = {
    corte:       { nombre: "Corte de pelo",       duracion: 15, precio: 15 },
    barba:       { nombre: "Barba",               duracion: 10, precio: 10 },
    tinte:       { nombre: "Tinte",               duracion: 30, precio: 30 },
    tratamiento: { nombre: "Tratamiento capilar", duracion: 25, precio: 25 }
};

// Genera slots de 10 en 10 minutos entre horaInicio y horaFin (exclusivo)
function generarSlots(horaInicio, horaFin) {
    const slots = [];
    let [h, m] = horaInicio.split(":").map(Number);
    const [hFin, mFin] = horaFin.split(":").map(Number);
    while (h < hFin || (h === hFin && m < mFin)) {
        slots.push(String(h).padStart(2,"0") + ":" + String(m).padStart(2,"0"));
        m += 10;
        if (m >= 60) { h++; m -= 60; }
    }
    return slots;
}

const SLOTS_SEMANA = generarSlots("09:00", "19:00"); // 9:00 a 19:00 (último slot 18:50)
const SLOTS_SABADO = generarSlots("10:00", "14:00"); // 10:00 a 14:00 (último slot 13:50)

// Estado global
let weekOffset   = 0;
let selectedCell = null;
let selectedDate = null;
let selectedHour = null;

// Listas de reservas { inicio: Date, duracion: int }
let reservasOcupadas = [];
let misReservasList  = [];

// -------------------------------------------------------
// Inicialización
// -------------------------------------------------------
function initReservas() {
    document.querySelectorAll(".slot-ocupado").forEach(el => {
        reservasOcupadas.push({
            inicio:   parseISO(el.dataset.inicio),
            duracion: parseInt(el.dataset.duracion)
        });
    });
    document.querySelectorAll(".slot-mio").forEach(el => {
        misReservasList.push({
            inicio:   parseISO(el.dataset.inicio),
            duracion: parseInt(el.dataset.duracion)
        });
    });
    buildCalendar();
}

function parseISO(str) {
    const [datePart, timePart] = str.split("T");
    const [y, mo, d] = datePart.split("-").map(Number);
    const [h, m]     = timePart.split(":").map(Number);
    return new Date(y, mo - 1, d, h, m, 0);
}

if (document.readyState === "loading") {
    document.addEventListener("DOMContentLoaded", initReservas);
} else {
    initReservas();
}

// -------------------------------------------------------
// Detección de solapamiento
// Un slot de 10 min está ocupado si se solapa con alguna reserva.
// -------------------------------------------------------
const SLOT_DUR_MIN = 10;

function reservaEnSlot(lista, slotDate) {
    const slotFin = new Date(slotDate.getTime() + SLOT_DUR_MIN * 60000);
    return lista.find(r => {
        const rFin = new Date(r.inicio.getTime() + r.duracion * 60000);
        return slotDate < rFin && slotFin > r.inicio;
    }) || null;
}

// -------------------------------------------------------
// Calendario
// -------------------------------------------------------
function setMode(mode) {
    document.getElementById("btn-calendar").classList.toggle("active", mode === "calendar");
    document.getElementById("btn-search").classList.toggle("active",   mode === "search");
    document.getElementById("calendar-section").style.display = mode === "calendar" ? "block" : "none";
    document.getElementById("search-panel").classList.toggle("visible", mode === "search");
}

function changeWeek(delta) {
    weekOffset += delta;
    buildCalendar();
}

function getMondayOfWeek(offset) {
    const today  = new Date();
    const day    = today.getDay();
    const diff   = today.getDate() - day + (day === 0 ? -6 : 1);
    const monday = new Date(today.setDate(diff + offset * 7));
    monday.setHours(0, 0, 0, 0);
    return monday;
}

function formatDate(date) {
    const y  = date.getFullYear();
    const mo = String(date.getMonth() + 1).padStart(2, "0");
    const d  = String(date.getDate()).padStart(2, "0");
    return y + "-" + mo + "-" + d;
}

function formatDateLabel(date) {
    return date.toLocaleDateString("es-ES", { day: "numeric", month: "short" });
}

function buildCalendar() {
    const monday = getMondayOfWeek(weekOffset);
    const grid   = document.getElementById("calendar-grid");
    grid.innerHTML = "";

    const dayNames  = ["Lun", "Mar", "Mié", "Jue", "Vie", "Sáb"];
    const weekDates = [];

    // Esquina vacía
    const corner = document.createElement("div");
    corner.className = "cal-header-cell corner";
    grid.appendChild(corner);

    // Cabecera días
    for (let i = 0; i < 6; i++) {
        const d = new Date(monday);
        d.setDate(monday.getDate() + i);
        weekDates.push(d);
        const cell = document.createElement("div");
        cell.className = "cal-header-cell" + (i === 5 ? " sat" : "");
        cell.innerHTML = dayNames[i] + "<br><span style='font-size:0.68rem;opacity:0.8'>"
                       + formatDateLabel(d) + "</span>";
        grid.appendChild(cell);
    }

    document.getElementById("week-label").textContent =
        formatDateLabel(weekDates[0]) + " – " + formatDateLabel(weekDates[5]);

    // Filas: solo mostrar etiqueta de hora cuando es en punto (:00) o media (:30)
    // para no saturar la vista, pero los slots siguen siendo de 10 min
    SLOTS_SEMANA.forEach(slot => {
        const isSabadoSlot = !SLOTS_SABADO.includes(slot);
        const [h, m] = slot.split(":").map(Number);

        // Etiqueta de hora: mostrar solo :00 y :30
        const timeCell = document.createElement("div");
        timeCell.className = "cal-time-cell";
        timeCell.textContent = (m === 0 || m === 30) ? slot : "";
        grid.appendChild(timeCell);

        weekDates.forEach(function(date, i) {
            const isSabado  = (i === 5);
            const cell      = document.createElement("div");
            const slotDate  = new Date(date.getFullYear(), date.getMonth(), date.getDate(), h, m, 0);
            const miRes     = reservaEnSlot(misReservasList,  slotDate);
            const otroRes   = reservaEnSlot(reservasOcupadas, slotDate);

            if (isSabado && isSabadoSlot) {
                cell.className = "cal-slot closed";

            } else if (miRes) {
                cell.className = "cal-slot taken-mine";
                // Solo en el slot de inicio mostramos la etiqueta
                if (miRes.inicio.getTime() === slotDate.getTime()) {
                    cell.textContent = miRes.duracion + "min";
                    cell.title = "Tu reserva: " + miRes.duracion + " min";
                }

            } else if (otroRes) {
                cell.className = "cal-slot taken";
                if (otroRes.inicio.getTime() === slotDate.getTime()) {
                    cell.textContent = "Ocup.";
                }

            } else {
                cell.className = "cal-slot available";
                cell.onclick   = (function(d, s, c) {
                    return function() { openModal(d, s, c); };
                })(date, slot, cell);
            }

            grid.appendChild(cell);
        });
    });
}

// -------------------------------------------------------
// Modal
// -------------------------------------------------------
function openModal(date, hora, cell) {
    if (selectedCell) selectedCell.classList.remove("selected");
    selectedCell = cell;
    if (cell) cell.classList.add("selected");

    selectedDate = formatDate(date);
    selectedHour = hora;

    const label = date.toLocaleDateString("es-ES", { weekday: "long", day: "numeric", month: "long" });
    document.getElementById("modal-slot-label").textContent = label + " · " + hora;

    document.querySelectorAll(".service-item").forEach(item => {
        item.classList.remove("checked");
        item.querySelector("input").checked = false;
    });

    updateModalSummary();
    document.getElementById("modal-backdrop").classList.add("visible");
}

// Igual que openModal pero pre-selecciona una lista de servicios
function openModalConServicios(date, hora, cell, servicios) {
    openModal(date, hora, cell);
    if (!servicios || servicios.length === 0) return;
    document.querySelectorAll(".service-item").forEach(item => {
        if (servicios.includes(item.dataset.service)) {
            item.classList.add("checked");
            item.querySelector("input").checked = true;
        }
    });
    updateModalSummary();
}

function closeModal(e) {
    if (!e || e.target === document.getElementById("modal-backdrop")) {
        document.getElementById("modal-backdrop").classList.remove("visible");
        if (selectedCell) selectedCell.classList.remove("selected");
        selectedCell = null;
    }
}

function toggleService(item) {
    item.classList.toggle("checked");
    item.querySelector("input").checked = item.classList.contains("checked");
    updateModalSummary();
}

function updateModalSummary() {
    let duracion = 0, precio = 0;
    document.querySelectorAll(".service-item.checked").forEach(item => {
        const key = item.dataset.service;
        duracion += SERVICIOS[key].duracion;
        precio   += SERVICIOS[key].precio;
    });

    document.getElementById("summary-duration").textContent = duracion > 0 ? duracion + " min" : "—";
    document.getElementById("summary-price").textContent    = precio   > 0 ? precio   + " €"   : "—";

    if (duracion > 0 && selectedHour) {
        const [h, m] = selectedHour.split(":").map(Number);
        const fin    = new Date(0, 0, 0, h, m + duracion, 0);
        const hh     = String(fin.getHours()).padStart(2, "0");
        const mm     = String(fin.getMinutes()).padStart(2, "0");
        document.getElementById("summary-end").textContent      = hh + ":" + mm;
        document.getElementById("summary-end-row").style.display = "flex";
    } else {
        document.getElementById("summary-end-row").style.display = "none";
    }

    document.getElementById("btn-confirm").disabled = (duracion === 0);
}

// -------------------------------------------------------
// Confirmar reserva
// -------------------------------------------------------
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

// -------------------------------------------------------
// Panel de búsqueda
// -------------------------------------------------------
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

    // Filtro de día: valor del select ("" = todos, "0"=Lunes ... "5"=Sábado)
    const diaFiltro = document.getElementById("filter-day").value;

    // Servicios seleccionados en el panel de búsqueda
    const serviciosFiltro = [];
    let durFiltro = 0;
    document.querySelectorAll("#search-panel input[type=checkbox]:checked").forEach(cb => {
        serviciosFiltro.push(cb.value);
        durFiltro += SERVICIOS[cb.value].duracion;
    });
    if (durFiltro === 0) durFiltro = 10;

    let found = 0;

    for (let i = 0; i < 6; i++) {
        // Aplicar filtro de día: si se eligió un día concreto, saltar los demás
        if (diaFiltro !== "" && parseInt(diaFiltro) !== i) continue;

        const date  = new Date(monday);
        date.setDate(monday.getDate() + i);
        const slots = (i === 5) ? SLOTS_SABADO : SLOTS_SEMANA;

        slots.forEach(hora => {
            const [h, m]   = hora.split(":").map(Number);
            const slotDate = new Date(date.getFullYear(), date.getMonth(), date.getDate(), h, m, 0);

            // Comprobar que todos los slots del bloque necesario están libres
            let bloqueLibre = true;
            for (let offset = 0; offset < durFiltro; offset += 10) {
                const checkDate = new Date(slotDate.getTime() + offset * 60000);
                if (reservaEnSlot(reservasOcupadas, checkDate) || reservaEnSlot(misReservasList, checkDate)) {
                    bloqueLibre = false;
                    break;
                }
            }

            if (bloqueLibre) {
                const diaMes = date.getDate();
                const el = document.createElement("div");
                el.className = "result-slot";
                el.innerHTML = "<div style='font-size:0.72rem;color:#999'>" + dayNames[i] + " " + diaMes + "</div>"
                             + "<div style='font-size:1rem;font-weight:700'>" + hora + "</div>"
                             + "<div style='font-size:0.72rem;color:#2e7d52'>Libre</div>";

                const capDate      = new Date(date);
                const capHora      = hora;
                const capServicios = serviciosFiltro.slice();

                el.onclick = function() {
                    setMode("calendar");
                    setTimeout(function() { openModalConServicios(capDate, capHora, null, capServicios); }, 50);
                };
                grid.appendChild(el);
                found++;
            }
        });
    }

    // Actualizar label de semana en el panel de búsqueda
    const labelEl = document.getElementById("search-week-label");
    if (labelEl) {
        const fri = new Date(monday); fri.setDate(monday.getDate() + 5);
        labelEl.textContent = formatDateLabel(monday) + " – " + formatDateLabel(fri);
    }

    if (found === 0) {
        grid.innerHTML = "<p style='color:#999;font-size:0.875rem'>No hay huecos libres esta semana.</p>";
    }
    panel.style.display = "block";
}