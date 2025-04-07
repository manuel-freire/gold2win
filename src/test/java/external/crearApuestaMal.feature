Feature: Crear y obtener una apuesta

  Background:
    * url baseUrl

  Scenario: Crear una apuesta y obtenerla por ID
    Given path 'api', 'apuesta' ,
    And request { "cantidad": 100.0, "aFavor": true, "apostador": { "id": 1 }, "formulaApuesta": { "id": 15 } }
    When method post
    Then status 201
    And match response.cantidad == 100.0
    And match response.apostador.id == 1
    And match response.formulaApuesta.id == 15
    And match response.id != null

    * def id = response.id
    * print 'ID de la apuesta creada:', id
    * karate.pause(1000)

    Given path 'api', 'apuesta', id
    When method get
    Then status 200
    And match response.cantidad == 100.0
    And match response.aFavor == true
    And match response.apostador.id == 1
    And match response.formulaApuesta.id == 15