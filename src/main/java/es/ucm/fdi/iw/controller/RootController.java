package es.ucm.fdi.iw.controller;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.Evento;
import es.ucm.fdi.iw.model.FormulaApuesta;
import es.ucm.fdi.iw.model.Seccion;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.User.Role;
import es.ucm.fdi.iw.model.VariableSeccion;

import java.io.File;


/**
 *  Non-authenticated requests only.
 */
@Controller
public class RootController {

	@Autowired
	private EntityManager entityManager;

    @Autowired
    private LocalData localData;

	private static final Logger log = LogManager.getLogger(RootController.class);

	@GetMapping("/login")
    public String login(Model model) {
        return "login";
    }

	@GetMapping("/")
    public String index(Model model) {
        //obtengo los eventos (solo los 10 primeros que no hayan sucedido ya)
        LocalDateTime ahora = LocalDateTime.now();
        String queryEventos = "SELECT e FROM Evento e WHERE e.fechaCierre > :ahora ORDER BY e.fechaCierre ASC";
        TypedQuery<Evento> query = entityManager.createQuery(queryEventos, Evento.class);
        query.setParameter("ahora", ahora);
        query.setMaxResults(10);
        List<Evento> eventos = query.getResultList();

        //obtengo las secciones
        String querySecciones = "SELECT s FROM Seccion s WHERE s.enabled = true ORDER BY s.grupo ASC";
        List<Seccion> secciones = entityManager.createQuery(querySecciones).getResultList();

        //añado los eventos y las secciones al modelo
        model.addAttribute("eventos", eventos);
        model.addAttribute("secciones", secciones);

        return "index";
    }

    @GetMapping("/seccion/{id}/pic")
    public StreamingResponseBody getPic(@PathVariable long id) throws IOException {
        File f = localData.getFile("user", ""+id+".jpg");
        InputStream in = new BufferedInputStream(f.exists() ?
            new FileInputStream(f) : RootController.defaultPic());
        return os -> FileCopyUtils.copy(in, os);
    }

    private static InputStream defaultPic() {
	    return new BufferedInputStream(Objects.requireNonNull(
            UserController.class.getClassLoader().getResourceAsStream(
                "static/img/default-pic.jpg")));
    }

    
    @GetMapping("/evento/{id}/apostar")
    public String apostar(@PathVariable long id,@RequestParam long seccionId, Model model, HttpSession session){
        String queryApuestas = "SELECT a FROM FormulaApuesta a WHERE a.evento.id = :id";
        List<FormulaApuesta> apuestas = entityManager.createQuery(queryApuestas).setParameter("id", id).getResultList();

        String queryVariables = "SELECT v FROM VariableSeccion v WHERE v.seccion.id = :seccionId";
        List<VariableSeccion> variables = entityManager.createQuery(queryVariables).setParameter("seccionId", seccionId).getResultList();

        model.addAttribute("apuestas", apuestas);
        model.addAttribute("variables", variables);

        return "crearApuesta";
    }


    @GetMapping("/misApuestas")
    public String misApuestas(Model model){
        return "misApuestas";
    }

    @GetMapping("/crearApuesta")
    public String crearApuesta(Model model){
        return "crearApuesta";
    }

    @GetMapping("/admin")
    public String admin(Model model){
        return "admin";
    }

    @GetMapping("/cartera/ingresar")
    public String ingresar(Model model, HttpSession session) {
        User user = (User) session.getAttribute("u");
        if (user == null || !user.hasRole(Role.USER)) {
            return "redirect:/login";  // Redirige si no es un usuario autenticado
        }
        return "ingresar";
    }

	@GetMapping("/cartera/retirar")
	public String retirar(Model model) {
    	return "retirar";
	}

    @GetMapping("/cartera/ingreso")
	public String ingreso(Model model) {
    	return "ingreso";
	}

	@GetMapping("/cartera/ingresar/paypal")
	public String paypal(Model model) {
    	return "paypal";
	}

	@GetMapping("/cartera/ingresar/tarjeta")
	public String tarjeta(Model model) {
    	return "tarjeta";
	}

    @GetMapping("/misApuestas/todas")
    public String todasMisApuestas(Model model){
        return "misApuestas-todas";
    }

    @GetMapping("/misApuestas/determinadas")
    public String apuestasDeterminadas(Model model){
        return "misApuestas-determinadas";
    }

    @GetMapping("/misApuestas/pendientes")
    public String apuestasPendientes(Model model){
        return "misApuestas-pendientes";
    }
}
