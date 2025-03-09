package es.ucm.fdi.iw.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpSession;

import es.ucm.fdi.iw.model.Seccion;
import es.ucm.fdi.iw.model.User;

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
}
