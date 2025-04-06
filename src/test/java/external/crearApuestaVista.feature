Feature: Testeo UI con Karate

  Scenario: Probar crear una apuesta y verla desde tu historial
    Given driver baseUrl + '/login'
    

    * click('#debugButtonA')

    * click('#enlaceNavTodosEventos')

    * click('.estilo-contenedor-adaptable')

    * click('#boton-crear-formula')

    * delay(500)
    And input('#tituloModal', 'Mayor número de golpeos')
    And input('#formulaModal', 'Golpeos-JugA > Golpeos-JugB')

    * delay(1000)

    * click('#botonSiguienteCrearApuesta')

    * delay(500)
    And input('#cantidadModal', '20')
    * delay(500)
    * click('#botonCrearApuestaDefinitiva')

    * delay(2000)

    * click('#enlaceNavMisApuestas')
    * click('.headRowBettingBox')
    * delay(7000)
    