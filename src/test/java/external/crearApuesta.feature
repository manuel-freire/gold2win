Feature: Testeo UI con Karate

  Scenario: Probar crear una apuesta y verla desde tu historial
    Given driver baseUrl + '/login'
    
    * click('#debugButtonA')

    * click('#enlaceNavTodosEventos')

    * click('#contenedorEvento-3')

    * delay(1000)
    And input('#cantidad-3', '15')
    * delay(2000)
    * click('.botonApostarFavorable')

    * delay(4000)

    * click('#enlaceNavMisApuestas')
    * click('.headRowBettingBox')
    * delay(7000)
    