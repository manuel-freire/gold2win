package es.ucm.fdi.iw.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import es.ucm.fdi.iw.model.Apuesta;
import es.ucm.fdi.iw.model.Evento;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@RestController
public class PruebasController {

    @Autowired
    private EntityManager entityManager;

    @PostMapping("/api/apuesta")
    @Transactional
    public ResponseEntity<Apuesta> crearPersona(@RequestBody Apuesta apuesta) {
        // Validación de los datos de entrada
        //if (apuesta.getNombre() == null || apu.getNombre().isEmpty()) {
           // return ResponseEntity.badRequest().body("Nombre es obligatorio");
        //}

        // Persistir la nueva persona en la base de datos
        entityManager.persist(apuesta);
        entityManager.flush();  // Asegurarse de que los cambios se persistan

        return ResponseEntity.status(HttpStatus.CREATED).body(apuesta);
    }

    @GetMapping("/api/buscarEvento")
    public ResponseEntity<Long> obtenerEvento(@RequestParam String nombre) {
        Evento evento = entityManager.find(Evento.class, nombre);

        if (evento == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); 
        }

        return ResponseEntity.ok(evento.getId()); 
    }
}