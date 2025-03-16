
document.getElementById("crearSeccionSubmit").addEventListener("click", function() {
    enviarDatos();
});



function enviarDatos() {
    const nombreSeccion = document.getElementById("inputNombreSeccion").value;
    const grupoSeccion = document.getElementById("inputTipoSeccion").value;
    const ruta = document.getElementById("formularioCrearSeccion").getAttribute("data-ruta-formulario");
    const rutaImagen = document.getElementById("formularioCrearSeccion").getAttribute("data-ruta-imagen");

    const variables = [];

    const contenedoresVariables = document.querySelectorAll(".variableSeccion");

    contenedoresVariables.forEach((div) => {
        const nombre = div.getAttribute("data-name");
        const tipo = div.getAttribute("data-type");
        let numerico = !(tipo === "Texto");

        variables.push({
            nombre: nombre,
            numerico: numerico
        });
    });

    const formData = new FormData();
    
    formData.append("nombre", nombreSeccion);
    formData.append("grupo", grupoSeccion);

    formData.append("plantilla", JSON.stringify(variables));

    // Enviar los datos al servidor con AJAX
    fetch(ruta, {
        method: 'POST',
        body: formData
    })
    .then(response => response.json())
    .then(data => {
        // Aquí puedes manejar la respuesta, como guardar el ID de la sección creada
        const idSeccion = data.id;
        console.log("ID de la sección:", idSeccion);

        // Una vez que tienes el ID, puedes subir la imagen
        enviarImagen(idSeccion,rutaImagen);
    })
    .catch(error => console.error("Error al enviar datos:", error));
}

function enviarImagen(idSeccion,ruta) {
    const archivoImagen = document.getElementById("inputImagenSecciones").files[0];
    const formDataImagen = new FormData();

    // Añadir el archivo de imagen al FormData
    formDataImagen.append("photo", archivoImagen);

    // Enviar la imagen al servidor
    fetch(`${ruta}/${idSeccion}/pic`, {
        method: 'POST',
        body: formDataImagen
    })
    .then(response => response.json())
    .then(data => {
        console.log("Imagen subida correctamente:", data);
    })
    .catch(error => console.error("Error al subir la imagen:", error));
}
