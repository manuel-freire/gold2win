package es.ucm.fdi.iw.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import es.ucm.fdi.iw.model.Evento;
import es.ucm.fdi.iw.model.Seccion;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.Variable;
import es.ucm.fdi.iw.model.VariableSeccion;

/**
 *  Site administration.
 *
 *  Access to this end-point is authenticated - see SecurityConfig
 */
@Controller
@RequestMapping("admin")
public class AdminController {

    @Autowired
	private EntityManager entityManager;
    
	private static final Logger log = LogManager.getLogger(AdminController.class);

	@GetMapping("/")
    public String index(Model model) {
        return "admin";
    }

    @GetMapping("/reportes")
    public String tablaReportes(Model model){
        return "reportes";
    }

    @GetMapping("/reporteConcreto")
    public String reporteConcreto(Model model){
        return "reporteConcreto";
    }

    @GetMapping("/verificarEvento")
    public String verificarEvento(Model model){
        return "verificarEvento";
    }

    @GetMapping("/secciones")
    public String secciones(Model model){
        //obtengo las secciones
        String querySecciones = "SELECT s FROM Seccion s WHERE s.enabled = true ORDER BY s.grupo ASC";
        List<Seccion> secciones = entityManager.createQuery(querySecciones).getResultList();

        model.addAttribute("secciones", secciones);

        return "secciones";
    }

    @GetMapping("/secciones/{id}/editar")
    public String editarSeccion(@PathVariable long id, Model model, HttpSession session){
        Seccion target = entityManager.find(Seccion.class, id);
        String querySecciones = "SELECT s FROM Seccion s WHERE s.enabled = true ORDER BY s.grupo ASC";
        List<Seccion> secciones = entityManager.createQuery(querySecciones).getResultList();

        model.addAttribute("seccionEditable", target);
        model.addAttribute("secciones", secciones);

        return "editarSeccion";
    }


    @GetMapping("/secciones-crearSeccion")
    public String seccionesCrear(Model model){
        //obtengo las secciones
        String querySecciones = "SELECT s FROM Seccion s WHERE s.enabled = true ORDER BY s.grupo ASC";
        List<Seccion> secciones = entityManager.createQuery(querySecciones).getResultList();

        model.addAttribute("secciones", secciones);

        return "secciones-crearSeccion";
    }

    @Transactional
    @ResponseBody
    @PostMapping("/guardarSeccion")
    public ResponseEntity<JsonNode> guardarSeccion(@RequestBody JsonNode json) {
        // Crear una respuesta JSON con el mensaje
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        response.put("mensaje", "Seccion guardada correctamente");

        JsonNode seccionNode = json.get("seccionN");
        if (seccionNode == null || !seccionNode.has("nombre") || !seccionNode.has("tipo")) {
            return ResponseEntity.badRequest().body(objectMapper.createObjectNode().put("error", "Datos de seccionN incompletos"));
        }

        String nombre = seccionNode.get("nombre").asText();
        String grupo = seccionNode.get("tipo").asText();

        Seccion nuevaSeccion = new Seccion();
        nuevaSeccion.setNombre(nombre);
        nuevaSeccion.setGrupo(grupo);
        nuevaSeccion.setEnabled(true);
        entityManager.persist(nuevaSeccion);
        entityManager.flush();  //para asegurar que exista la sección cuando se añadan variables

        JsonNode itemsNode = json.get("arrayVariables");
        if (itemsNode != null && itemsNode.isArray()) {
            for (JsonNode item : itemsNode) {

                String nombreV = item.get("nombreV").asText();
                String tipoV = item.get("tipoV").asText();

                VariableSeccion nuevaVariable = new VariableSeccion();
                nuevaVariable.setNombre(nombreV);
                nuevaVariable.setNumerico(tipoV.equals("Valor numérico"));
                nuevaVariable.setSeccion(nuevaSeccion);
                entityManager.persist(nuevaVariable);
            }
        }
        return ResponseEntity.ok(response);
    }

    @Transactional
    @ResponseBody
    @DeleteMapping("/eliminarSeccion/{id}")
    public ResponseEntity<JsonNode> eliminarSeccion(@PathVariable Long id) {
        Seccion seccion = entityManager.find(Seccion.class, id);

        if (seccion != null) {
            seccion.setEnabled(false);
            entityManager.merge(seccion);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        response.put("mensaje", "Seccion eliminada correctamente");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/obtenerEventos")
    @ResponseBody
    public ResponseEntity<List<Seccion>> obtenerEventos() {
        String querySecciones = "SELECT s FROM Seccion s WHERE s.enabled = true ORDER BY s.grupo ASC";
        List<Seccion> secciones = entityManager.createQuery(querySecciones).getResultList();
        return ResponseEntity.ok(secciones);
    }
}
