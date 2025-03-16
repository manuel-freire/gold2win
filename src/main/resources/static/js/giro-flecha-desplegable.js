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
            messageElement.style.display = "none"; // Oculta el mensaje
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
