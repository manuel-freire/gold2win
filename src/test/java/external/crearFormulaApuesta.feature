Feature: Testeo UI con Karate

  Scenario: Probar crear una apuesta y verla desde tu historial
    Given driver baseUrl + '/login'

    * click('#debugButtonA')

    * click('#enlaceNavTodosEventos')

    * click('#contenedorEvento-2')

    * click('#boton-crear-formula')

    * delay(500)
    And input('#tituloModal', 'Victoria Lakers')
    And input('#formulaModal', 'Puntos-EquipA > Puntos-EquipB')

    * delay(1000)

    * click('#botonSiguienteCrearApuesta')

    * delay(500)
    And input('#cantidadModal', '20')
    * delay(500)
    * click('#botonCrearApuestaDefinitiva')

    * delay(2000)

    * click('#usuarioNav')
    * delay(500)
    * click('#botonNavCerrarSesion')

    * click('#debugButtonB')
    * click('#enlaceNavTodosEventos')
     * click('#contenedorEvento-2')
    * delay(6000)