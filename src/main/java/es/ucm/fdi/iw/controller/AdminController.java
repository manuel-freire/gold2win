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

import es.ucm.fdi.iw.model.Seccion;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.VariableSeccion;

import es.ucm.fdi.iw.services.SeccionService;
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

    @Autowired
    private SeccionService seccionService;

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

    @ResponseBody
    @PostMapping("/guardarSeccion")
    public ResponseEntity<JsonNode> guardarUsuario(@RequestBody JsonNode json) {
       // Lógica para guardar el usuario en la base de datos (ejemplo simulado)
        System.out.println("Guardando sección: " + json.toString());

        // Crear una respuesta JSON con el mensaje
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        response.put("mensaje", "Usuario guardado correctamente");


        String nombre = json.get("seccionN").get("nombre").asText();  // Obtiene el valor de "nombre" como String
        String grupo = json.get("seccionN").get("tipo").asText();      // Obtiene el valor de "tipo" como String

        // Crear un objeto Seccion y llenar sus campos usando setters
        Seccion nuevaSeccion = new Seccion();
        nuevaSeccion.setNombre(nombre);
        nuevaSeccion.setGrupo(grupo);
        nuevaSeccion.setEnabled(true);
        Seccion seccionCreada = seccionService.guardarSeccion(nuevaSeccion);

        JsonNode itemsNode = json.get("arrayVariables");

        String tipoV = "";
        if (itemsNode != null && itemsNode.isArray()) {
            for (JsonNode item : itemsNode) {
                String nombreV = item.get("nombreV").asText();
                tipoV = item.get("tipoV").asText();

                VariableSeccion nuevaVariable = new VariableSeccion();
                nuevaVariable.setNombre(nombreV);
                nuevaVariable.setNumerico(tipoV.equals("Valor numérico"));
                nuevaVariable.setSeccion(seccionCreada);
                seccionService.guardarVariableSeccion(nuevaVariable);
            }
        }

        response.put("nombre", tipoV);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/eliminarSeccion/{id}")
    public ResponseEntity<String> eliminarSeccion(@PathVariable Long id) {
        try {
            // Llama al servicio para eliminar la sección
            seccionService.eliminarSeccion(id);
            return ResponseEntity.ok("Sección eliminada correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar la sección: " + e.getMessage());
        }
    }
}
