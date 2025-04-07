var mandando = false; //controla que no se pueda mandar varias veces el mismo formulario

document.getElementById("btnSiguiente").addEventListener("click", function(event) {
    console.log("submit");
    nombre = document.getElementById("firstName");
    apellidos = document.getElementById("lastName");
    username = document.getElementById("username");

    if(!nombre.checkValidity()){
        document.getElementById("nombreInvalido").innerHTML = "El nombre es obligatorio";
        document.getElementById("firstName").classList.add("is-invalid");
    }
    else{
        document.getElementById("firstName").classList.remove("is-invalid");
        document.getElementById("firstName").classList.add("is-valid");
    }

    if(!apellidos.checkValidity()){
        document.getElementById("apellidosInvalido").innerHTML = "Los apellidos son obligatorios";
        document.getElementById("lastName").classList.add("is-invalid");
    }
    else{
        document.getElementById("lastName").classList.remove("is-invalid");
        document.getElementById("lastName").classList.add("is-valid");
    }

    if(!username.checkValidity()){
        document.getElementById("usernameInvalido").innerHTML = "El nombre de usuario es obligatorio";
        document.getElementById("username").classList.add("is-invalid");
    }
    else{
        document.getElementById("username").classList.remove("is-invalid");
        document.getElementById("username").classList.add("is-valid");
    }

    if(nombre.checkValidity() && apellidos.checkValidity() && username.checkValidity()) {
        document.getElementById("apartadoNombre").classList.add("desaparece");
        document.getElementById("apartadoCredenciales").classList.remove("desaparece");
    }
});

document.getElementById("btnAtras").addEventListener("click", function(event) {
    document.getElementById("apartadoCredenciales").classList.add("desaparece");
    document.getElementById("apartadoNombre").classList.remove("desaparece");
});


document.getElementById('formularioRegister').addEventListener('submit', function (event) {
    event.preventDefault(); 

    if (mandando) return; // Si ya se está enviando, no hacer nada
    mandando = true; // Marcar como enviando

    correo = document.getElementById("email");
    password = document.getElementById("password");

    if(!correo.checkValidity()){
        document.getElementById("emailInvalido").innerHTML = "El correo no es valido";
        document.getElementById("email").classList.add("is-invalid");
    }
    else{
        document.getElementById("email").classList.remove("is-invalid");
        document.getElementById("email").classList.add("is-valid");
    }

    if(!password.checkValidity()){
        document.getElementById("passwordInvalida").innerHTML = "La contraseña es obligatoria";
        document.getElementById("password").classList.add("is-invalid");
    }
    else{
        document.getElementById("password").classList.remove("is-invalid");
        document.getElementById("password").classList.add("is-valid");
    }
  
    if(!correo.checkValidity() || !password.checkValidity()) {
        mandando = false; // Desmarcar como enviando si hay errores
        return; // Si hay errores, no enviar el formulario
    }
    
    const username_val = document.getElementById('username').value;
    const password_val = document.getElementById('password').value;
    const email_val = document.getElementById('email').value;
    const nombre_val = document.getElementById('firstName').value;
    const apellidos_val = document.getElementById('lastName').value;
  
    go('/register', 'POST', {
        username: username_val,
        password: password_val,
        email: email_val,
        firstName: nombre_val,
        lastName: apellidos_val
    })
    .then(data => {
        if (data.success) {
            window.location.href = '/login'; //redirijo al login
        } else {
            if (data.error === 'username') {
                document.getElementById("apartadoCredenciales").classList.add("desaparece");
                document.getElementById("apartadoNombre").classList.remove("desaparece");
                document.getElementById('usernameInvalido').innerHTML = 'El nombre de usuario ya está en uso.';
                document.getElementById("username").classList.add("is-invalid");

            } else if (data.error === 'email') {
                document.getElementById('emailInvalido').innerHTML = 'El correo electrónico ya está en uso.';
                document.getElementById("email").classList.add("is-invalid");
            } else {
                alert('Error');
            }
        }

        mandando = false;
    })
    .catch(error => {
        console.error('Error al registrar el usuario:', error);
        mandando = false; 
    });

  });