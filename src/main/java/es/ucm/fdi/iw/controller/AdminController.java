package es.ucm.fdi.iw.controller;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import java.io.File;
import java.util.Base64;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.controller.UserController.NoEsTuPerfilException;
import es.ucm.fdi.iw.model.Evento;
import es.ucm.fdi.iw.model.Seccion;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.User.Role;
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

    @Autowired
    private LocalData localData;
    
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
    public ResponseEntity<JsonNode> guardarSeccion(@RequestBody JsonNode json) throws IOException{
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

        JsonNode imageDataNode = json.get("imageData");
        if (imageDataNode != null && imageDataNode.has("image")) {
            String base64Image = imageDataNode.get("image").asText();
            String filename = imageDataNode.get("filename").asText();
            
            MultipartFile photo = convertirBase64AMultipartFile(base64Image, filename);
            setPic(photo, nuevaSeccion.getId());
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

    @ResponseBody
    public String setPic(@RequestParam("photo") MultipartFile photo, @PathVariable long id) throws IOException {

        User target = entityManager.find(User.class, id);
				
		log.info("Updating photo for user {}", id);
		File f = localData.getFileAux("", "imagen"+id+".jpg");
		if (photo.isEmpty()) {
			log.info("failed to upload photo: emtpy file?");
		} else {
			try (BufferedOutputStream stream =
					new BufferedOutputStream(new FileOutputStream(f))) {
				byte[] bytes = photo.getBytes();
				stream.write(bytes);
                log.info("Uploaded photo for {} into {}!", id, f.getAbsolutePath());
			} catch (Exception e) {
				log.warn("Error uploading " + id + " ", e);
			}
		}
		return "{\"status\":\"photo uploaded correctly\"}";
    }
    
    @GetMapping("/verificarSeccion")
    public ResponseEntity<?> verificarSeccion(@RequestParam String nombre) {
        nombre = nombre.trim(); 

        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(s) FROM Seccion s WHERE s.nombre = :nombre", Long.class);
        query.setParameter("nombre", nombre);
        Long count = query.getSingleResult();

        boolean existe = count > 0; // Si el conteo es mayor a 0, ya existe

        return ResponseEntity.ok().body("{\"existe\": " + existe + "}");
    }

    public MultipartFile convertirBase64AMultipartFile(String base64, String filename) throws IOException {
        // Decodificar Base64
        byte[] imageBytes = Base64.getDecoder().decode(base64.split(",")[1]);
    
        // Crear MultipartFile con InputStream
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
        
        // Crear MultipartFile "simulado"
        MultipartFile photo = new MultipartFile() {
            @Override
            public String getName() {
                return filename;
            }
    
            @Override
            public String getOriginalFilename() {
                return filename;
            }
    
            @Override
            public String getContentType() {
                return "image/jpeg"; // o el tipo adecuado
            }
    
            @Override
            public boolean isEmpty() {
                return imageBytes == null || imageBytes.length == 0;
            }
    
            @Override
            public long getSize() {
                return imageBytes.length;
            }
    
            @Override
            public byte[] getBytes() throws IOException {
                return imageBytes;
            }
    
            @Override
            public InputStream getInputStream() throws IOException {
                return byteArrayInputStream;
            }
    
            @Override
            public void transferTo(File dest) throws IOException, IllegalStateException {
                java.nio.file.Files.write(dest.toPath(), imageBytes);
            }
        };
    
        return photo;
    }

}


