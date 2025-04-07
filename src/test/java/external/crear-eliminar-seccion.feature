Background:
  * def adminToken = 'Bearer tu-token-admin-aqui'
  * def userToken = 'Bearer tu-token-usuario-aqui'
  * def adminHeaders = { Authorization: adminToken }
  * def userHeaders = { Authorization: userToken }

Scenario: Crear sección como admin
  Given path '/admin/secciones'
  And headers adminHeaders
  And request { nombre: 'Sección Test', tipo: 'PRUEBA' }
  When method post
  Then status 201
  And match response.nombre == 'Sección Test'
  * def seccionId = response.id

Scenario: Verificar que sección aparece como usuario
  Given path '/usuario/secciones'
  And headers userHeaders
  When method get
  Then status 200
  And match response contains { id: '#(seccionId)', nombre: 'Sección Test' }

Scenario: Eliminar sección como admin
  Given path '/admin/secciones', seccionId
  And headers adminHeaders
  When method delete
  Then status 200

Scenario: Verificar que sección ya no aparece como usuario
  Given path '/usuario/secciones'
  And headers userHeaders
  When method get
  Then status 200
  And match response !contains { id: '#(seccionId)' }