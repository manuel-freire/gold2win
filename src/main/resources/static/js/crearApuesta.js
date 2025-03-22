const currentPath = window.location.pathname;
const botonVerMas = document.getElementById("verMasEventos");
var offset = 0; // numElementos cargados
var buscado = null; // indica la ultima busqueda realizada (para sobre la busqueda ver mas)
let fechaInicio = new Date(0); // fecha en que se trajeron eventos por primera vez (para evitar que las cosas se descuadren)
var cargando = true;

fechaInicio = new Date().toISOString();
cargarFormulas().then(() => {
    cargando = false;});

botonVerMas.addEventListener("click", function() {
    if(!cargando){
        cargando = true;
        cargarFormulas().then(() => {
            cargando = false;
        }).catch((error) => {
            cargando = false;
            console.log(error);
        });
    }
});

/* FUNCION PARA LA LUPA */
document.getElementById("queryApuestas").addEventListener("keypress", function(event) {
    if (event.key === "Enter" && !cargando) {
        cargando = true;
        var busqueda = document.getElementById("queryApuestas").value;

        if (busqueda == "") { //si no hay nada escrito se cargan los eventos por defecto
            if(buscado != null){
                fechaInicio = new Date().toISOString();
                buscado = null;
                vaciarContenedorFormulas();
                offset = 0;

                cargarFormulas().then(() => {
                    cargando = false;
                }).catch((error) => {
                    cargando = false;
                    console.log(error);
                });
            }
            else{
                cargando = false;
            }
        }
        else if(buscado != busqueda){
            offset = 0;
            fechaInicio = new Date().toISOString();
            buscado = busqueda;
            vaciarContenedorFormulas();

            cargarFormulas().then(() => {
                cargando = false;
            }).catch((error) => {
                cargando = false;
                console.log(error);
            });
        }
    }
});

function vaciarContenedorFormulas(){
    contenedor = document.getElementById("contendorFormulas");

    while (contenedor.firstChild) {
        contenedor.removeChild(contenedor.firstChild);
    }
}

async function cargarFormulas(){
    botonVerMas.disabled = true; 

    try{
        var response;

        if(buscado == null)
            response = await go(currentPath+ '/cargarMas' + '?fechaInicio=' + fechaInicio + '&offset=' + offset, 'GET');
        else 
            response = await go(currentPath+ '/buscar' + '?fechaInicio=' + fechaInicio +'&busqueda=' + buscado +'&offset=' + offset, 'GET');

        response.formulas.forEach(formula => {
            anadirFormula(formula);
        });

        if(response.hayMasFormulas){
            botonVerMas.disabled = false;
            botonVerMas.style.display = "block";
        }
        else{
            botonVerMas.style.display = "none";
        }

        offset += response.formulas.length;
        console.log(response)
    } catch(error){
        console.error('Error:', error);
    }
}

function anadirFormula(formula){
    let elementoHTML = document.createElement("div");
    elementoHTML.className = "d-flex flex-column contenedor-apuesta";
    elementoHTML.innerHTML = 
        `
            <h5 class="pb-2 border-bottom"> ${formula.nombre}</h5>
            <div id="descripcion-contendor-apuesta">
                <div class="d-flex align-items-start mb-2">
                    <span class="titulo-campo-apuesta me-2">Formula:</span>
                    <div class="border w-100 scrollBarPerso contenedor-info">
                        <span class="spanAdaptable" style="white-space: nowrap;"> ${formula.formula}</span>
                    </div>
                </div>
                <div >
                    <span class="titulo-campo-apuesta">Cuota favorable:</span>
                    <span class="text-success"> ${formula.cuotaFaborable}</span>
                </div>
                <div >
                    <span class="titulo-campo-apuesta">Cuota desfavorable:</span>
                    <span class="text-success">${formula.cuotaDesfavorable}</span>
                </div>
            </div>
            
            <div class="w-100 d-flex align-items-center mt-3">
                <button type="button" class="btn btn-success ">
                    faborable
                </button>

                <input id="cantidad(${formula.id})" type="number" class ="form-control mx-2 flex-grow-1" placeholder="cantidad...">

                <button type="button" class="btn btn-success ">
                    desfaborable
                </button>
            </div>`;

    document.getElementById("contendorFormulas").appendChild(elementoHTML);
}


/*  CODIGO PARA EL MODAL Y LAS APUESTAS  */
document.getElementById("botonSiguienteCrearApuesta").addEventListener("click", () => {
    console.log("entra");
    var elementos1 = document.querySelectorAll('.vision-creatuApuesta-1');
    var elementos2 = document.querySelectorAll('.vision-creatuApuesta-2');

    var titulo = document.getElementById('tituloModal');
    var formula = document.getElementById('formulaModal');

    if(titulo.checkValidity() && formula.checkValidity()){
        elementos1.forEach(function(elemento) {
            elemento.classList.add('desaparece');
        });

        elementos2.forEach(function(elemento) {
            elemento.classList.remove('desaparece');
        });

        document.getElementById("botonRetrocederCrearApuesta").classList.remove('invisible');
    }

});

document.getElementById("botonRetrocederCrearApuesta").addEventListener("click", () => {
    var elementos1 = document.querySelectorAll('.vision-creatuApuesta-1');
    var elementos2 = document.querySelectorAll('.vision-creatuApuesta-2');

    elementos1.forEach(function(elemento) {
        elemento.classList.remove('desaparece');
    });

    elementos2.forEach(function(elemento) {
        elemento.classList.add('desaparece');
    });

    document.getElementById("botonRetrocederCrearApuesta").classList.add('invisible');
});

document.getElementById("crearApuestaForm").addEventListener("submit", function(event) {
    event.preventDefault();

    const titulo = document.getElementById("tituloModal").value;
    const formula = document.getElementById("formulaModal").value;
    const cantidad = document.getElementById("cantidadModal2").value;
    const tipoApuesta = document.getElementById("tipoApuestaModal2").value;

    document.getElementById("ocultador-formulario2").classList.add("invisible");
    let check = document.getElementById("confirmacionApuesta2");
    check.classList.remove("invisible");
    check.style.animation = "fadeIn 0.5s ease-in-out";

    setTimeout(() => {
        location.reload();  // Recarga la página
    }, 1000);
    /*
    fetch("/apostar", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({cantidad, tipoApuesta})
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(error => { throw new Error(error.error); });
        }
        return response.json();
    })
    .then(data => {
        // Si la apuesta es exitosa, mostrar el ícono de éxito
        alert(data.mensaje);
        var modal = bootstrap.Modal.getInstance(document.getElementById('miModal'));
        modal.hide(); // Cierra el modal manualmente
        document.getElementById("cantidad").classList.remove("is-invalid");
        document.getElementById("cantidad").classList.add("is-valid");  // Marca como válida
    })
    .catch(error => {
        // Si hay un error (ej. saldo insuficiente), marcar el campo en rojo
        document.getElementById("cantidad").classList.add("is-invalid");
        document.getElementById("cantidadError").textContent = error.message;  // Mostrar el error
    });
    */
});