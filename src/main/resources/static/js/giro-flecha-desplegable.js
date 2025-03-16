function calcularCuota() {
    console.log("Calculando cuotas...");
    
    const apuestas = document.querySelectorAll(".bettingBox");
    const gruposApuestas = {};

    // Recopilar apuestas por fórmula
    apuestas.forEach((apuesta) => {
        const formula = apuesta.getAttribute("data-formula");
        const cantidad = parseFloat(apuesta.getAttribute("data-cantidad")) || 0;
        const aFavor = apuesta.getAttribute("data-a-favor") === "true";

        if (!gruposApuestas[formula]) {
            gruposApuestas[formula] = { cantidadAFavor: 0, cantidadEnContra: 0, elementos: [] };
        }

        if (aFavor) {
            gruposApuestas[formula].cantidadAFavor += cantidad;
        } else {
            gruposApuestas[formula].cantidadEnContra += cantidad;
        }

        gruposApuestas[formula].elementos.push(apuesta);
    });

    // Calcular cuotas para cada grupo de apuestas
    Object.values(gruposApuestas).forEach(({ cantidadAFavor, cantidadEnContra, elementos }) => {
        let cuotaAFavor = 1.0;
        let cuotaEnContra = 1.0;

        if (cantidadAFavor > 0 && cantidadEnContra > 0) {
            cuotaAFavor = (cantidadEnContra + cantidadAFavor) / cantidadAFavor;
            cuotaEnContra = (cantidadEnContra + cantidadAFavor) / cantidadEnContra;
        }

        // Actualizar todas las apuestas que comparten la misma fórmula
        elementos.forEach((apuesta) => {
            const cuotaElemento = apuesta.querySelector(".cuota-apuesta");
            if (!cuotaElemento) return;
            const esAFavor = cuotaElemento.getAttribute("data-afavor") === "true";

            cuotaElemento.textContent = esAFavor
                ? cuotaAFavor.toFixed(1) 
                : cuotaEnContra.toFixed(1);
        });
    }); 
}

// Ejecutar al cargar la página y actualizar cada 30 segundos
document.addEventListener("DOMContentLoaded", () => {
    calcularCuota();
    setInterval(calcularCuota, 30000); // Se actualiza cada 30 segundos
});

function actualizarTiempoRestante1() {
    console.log("entrando a actualizar tiempo restante");
    const elementosTiempoRestante = document.querySelectorAll(".tiempo-restante-apuesta");
    elementosTiempoRestante.forEach((elemento) => {
        console.log("entra al foreach");
        const fechaEventoStr = elemento.getAttribute("data-fecha-evento-apuesta");  
        if (!fechaEventoStr) return;

        // Convertir la cadena a una fecha en JavaScript
        const fechaEvento = new Date(fechaEventoStr.replace(" ", "T")); // Reemplaza el espacio con 'T' para compatibilidad

        // Obtener la fecha actual
        const ahora = new Date();

        // Calcular la diferencia en milisegundos
        const diferencia = fechaEvento - ahora;

        if (diferencia <= 0) {
            /*elemento.textContent = "Evento iniciado";
            return;*/
            // Convertir la diferencia en días, horas y minutos
            const dias = -Math.floor(diferencia / (1000 * 60 * 60 * 24));
            const horas = -Math.floor((diferencia % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
            const minutos = -Math.floor((diferencia % (1000 * 60 * 60)) / (1000 * 60));
            // Mostrar la salida en el formato adecuado
            if (dias > 0) {
                elemento.textContent = `${dias} días`;
            } else {
                elemento.textContent = `${horas}h ${minutos}m`;
            }
        }

        else{
            // Convertir la diferencia en días, horas y minutos
            const dias = Math.floor(diferencia / (1000 * 60 * 60 * 24));
            const horas = Math.floor((diferencia % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
            const minutos = Math.floor((diferencia % (1000 * 60 * 60)) / (1000 * 60));
            // Mostrar la salida en el formato adecuado
            if (dias > 0) {
                elemento.textContent = `${dias} días`;
            } else {
                elemento.textContent = `${horas}h ${minutos}m`;
            }
        }
        
        
    });
}

var tiempoRestanteAux = document.querySelectorAll(".tiempo-restante-apuesta");
if(tiempoRestanteAux.length != 0){
    actualizarTiempoRestante1();
    setInterval(actualizarTiempoRestante1, 60000); // Actualizar cada minuto
}

document.addEventListener("DOMContentLoaded", function () {
    console.log("El DOM ha cargado. Script inicializado.");

    // Seleccionamos todos los botones de toggle y sus respectivos despliegues
    const toggleButtons = document.querySelectorAll(".btn-custom");
    
    toggleButtons.forEach(button => {
        const targetId = button.getAttribute("data-bs-target"); // Obtiene el id del desplegable
        const collapseElement = document.querySelector(targetId); // Busca el elemento correspondiente
        const arrowIconPath = button.querySelector("svg path"); // Encuentra el icono dentro del botón
        const messageElement = button.closest(".headRowBettingBox").querySelector("#mensajeDesplegable"); // Encuentra el mensaje de la apuesta
    

        if(collapseElement != null && arrowIconPath != null && messageElement != null){
            // Definimos los paths de las flechas
            const arrowDownPath =
            "M3.204 5h9.592L8 10.481zm-.753.659 4.796 5.48a1 1 0 0 0 1.506 0l4.796-5.48c.566-.647.106-1.659-.753-1.659H3.204a1 1 0 0 0-.753 1.659";
            const arrowUpPath =
            "M3.204 11h9.592L8 5.519zm-.753-.659 4.796-5.48a1 1 0 0 1 1.506 0l4.796 5.48c.566.647.106 1.659-.753 1.659H3.204a1 1 0 0 1-.753-1.659";

            // Evento cuando el desplegable se abre
            collapseElement.addEventListener("shown.bs.collapse", function () {
            arrowIconPath.setAttribute("d", arrowUpPath); // Flecha hacia arriba
            messageElement.style.display = "block"; // Oculta el mensaje
            console.log("Flecha cambiada a 'hacia arriba'. Desplegable abierto.");
            });

            // Evento cuando el desplegable se cierra
            collapseElement.addEventListener("hidden.bs.collapse", function () {
            arrowIconPath.setAttribute("d", arrowDownPath); // Flecha hacia abajo
            messageElement.style.display = "block"; // Muestra el mensaje
            console.log("Flecha cambiada a 'hacia abajo'. Desplegable cerrado.");
            });
        }
    });
    console.log("Script correctamente cargado y funcional.");
});