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
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import es.ucm.fdi.iw.AppConfig;
import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.model.Apuesta;

import es.ucm.fdi.iw.model.Evento;
import es.ucm.fdi.iw.model.FormulaApuesta;
import es.ucm.fdi.iw.model.Seccion;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.model.Variable;
import es.ucm.fdi.iw.model.User.Role;
import es.ucm.fdi.iw.model.VariableSeccion;

import es.ucm.fdi.iw.model.Transferable;
import java.util.stream.Collectors;

import java.io.File;


/**
 *  Non-authenticated requests only.
 */
@Controller
public class RootController {

    private final AuthenticationManager authenticationManagerBean;

    private final AppConfig appConfig;

    private final AdminController adminController;

	@Autowired
	private EntityManager entityManager;

    @Autowired
    private LocalData localData;

	private static final Logger log = LogManager.getLogger(RootController.class);

    RootController(AdminController adminController, AppConfig appConfig, AuthenticationManager authenticationManagerBean) {
        this.adminController = adminController;
        this.appConfig = appConfig;
        this.authenticationManagerBean = authenticationManagerBean;
    }

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
        model.addAttribute("selectedSeccion",-1);
        model.addAttribute("fechaCreacion", ahora);

        return "index";
    }

    @GetMapping(path = "/seccion/buscar", produces = "application/json")
    @Transactional
    @ResponseBody
    public List<Evento.Transfer> buscarEventos(
            @RequestParam long seccionId,
            @RequestParam String busqueda,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio, //necesito indicar el formato en que viene la fecha
            @RequestParam int offset) {

        List<String> etiquetas = List.of(busqueda.split(" ")).stream().filter(palabra -> palabra.startsWith("[") && palabra.endsWith("]")).collect(Collectors.toList());
        String nombre = List.of(busqueda.split(" ")).stream().filter(palabra -> !(palabra.startsWith("[") && palabra.endsWith("]"))) .collect(Collectors.joining(" "));

        String queryEventos = "SELECT e FROM Evento e WHERE e.fechaCierre > :inicio AND e.fechaCreacion < :inicio AND (LOWER(e.nombre) LIKE LOWER(:nombre)) ORDER BY e.fechaCierre ASC"; //por defecto se cogen todas las secciones
        Seccion seccion = entityManager.find(Seccion.class, seccionId);
        TypedQuery<Evento> query;

        if (seccion != null && seccion.isEnabled()) {
            queryEventos = "SELECT e FROM Evento e WHERE e.fechaCierre > :inicio AND e.fechaCreacion < :inicio AND e.seccion.id = :seccionId AND " + "(LOWER(e.nombre) LIKE LOWER(:nombre)) ORDER BY e.fechaCierre ASC"; //si existe la sección se cogen los eventos de esa sección
            query = entityManager.createQuery(queryEventos, Evento.class);
            query.setParameter("seccionId", seccionId);
        }
        else{
            query = entityManager.createQuery(queryEventos, Evento.class);
        }

        query.setParameter("nombre", "%" + nombre + "%");
        query.setParameter("inicio", fechaInicio);
        query.setMaxResults(10);
        query.setFirstResult(offset);

        List<Evento> eventos = query.getResultList();

        return eventos.stream().map(Evento::toTransfer).collect(Collectors.toList());
    }

    @GetMapping(path = "/seccion/cargarMas", produces = "application/json")
    @Transactional
    @ResponseBody
    public List<Evento.Transfer> cargarMasEventos(
            @RequestParam long seccionId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio, //necesito indicar el formato en que viene la fecha
            @RequestParam int offset) {

        Seccion seccion = entityManager.find(Seccion.class, seccionId);
        TypedQuery<Evento> query;
        String queryEventos = "SELECT e FROM Evento e WHERE e.fechaCierre > :inicio AND e.fechaCreacion < :inicio ORDER BY e.fechaCierre ASC"; //por defecto se cogen todos

        if (seccion != null && seccion.isEnabled()) {
            queryEventos = "SELECT e FROM Evento e WHERE (e.fechaCierre > :inicio AND e.fechaCreacion < :inicio AND e.seccion.id = :seccion) ORDER BY e.fechaCierre ASC"; //si existe la sección se cogen los eventos de esa sección
            query = entityManager.createQuery(queryEventos, Evento.class);
            query.setParameter("seccion", seccionId);
        }
        else{
            query = entityManager.createQuery(queryEventos, Evento.class);
        }

        query.setParameter("inicio", fechaInicio);
        query.setMaxResults(10);
        query.setFirstResult(offset);
        List<Evento> eventos = query.getResultList();

        return eventos.stream().map(Transferable::toTransfer).collect(Collectors.toList());
    }

    @GetMapping("/seccion/{id}")
    public String eventosSeccion(@PathVariable long id, Model model){
        //obtengo los eventos (solo los 10 primeros que no hayan sucedido ya)
        Seccion seccion = entityManager.find(Seccion.class, id);
        TypedQuery<Evento> query;
        String queryEventos = "SELECT e FROM Evento e WHERE e.fechaCierre > :ahora ORDER BY e.fechaCierre ASC"; //por defecto se cogen todos

        if (seccion != null && seccion.isEnabled()) {
            queryEventos = "SELECT e FROM Evento e WHERE (e.fechaCierre > :ahora AND e.seccion.id = :seccion) ORDER BY e.fechaCierre ASC"; //si existe la sección se cogen los eventos de esa sección
            query = entityManager.createQuery(queryEventos, Evento.class);
            query.setParameter("seccion", id);
        }
        else{
            query = entityManager.createQuery(queryEventos, Evento.class);
        }

        LocalDateTime ahora = LocalDateTime.now();
        query.setParameter("ahora", ahora);
        query.setMaxResults(10);
        List<Evento> eventos = query.getResultList();

        //obtengo las secciones
        String querySecciones = "SELECT s FROM Seccion s WHERE s.enabled = true ORDER BY s.grupo ASC";
        List<Seccion> secciones = entityManager.createQuery(querySecciones).getResultList();

        //añado los eventos y las secciones al modelo
        model.addAttribute("eventos", eventos);
        model.addAttribute("secciones", secciones);
        model.addAttribute("selectedSeccion",id);
        model.addAttribute("fechaCreacion", ahora);

        return "index";
    }

    @GetMapping("/seccion/{id}/pic")
    public StreamingResponseBody getPic(@PathVariable long id) throws IOException {
        File f = localData.getFile("seccion", ""+id+".png");
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
    public String apostar(@PathVariable long id, Model model, HttpSession session){
        String queryApuestas = "SELECT a FROM FormulaApuesta a WHERE a.evento.id = :id";
        List<FormulaApuesta> apuestas = entityManager.createQuery(queryApuestas).setParameter("id", id).getResultList();

        String queryVariables = "SELECT v FROM Variable v WHERE v.evento.id = :id";
        List<Variable> variables = entityManager.createQuery(queryVariables).setParameter("id", id).getResultList();

        Evento eventoSel = entityManager.find(Evento.class, id);

        model.addAttribute("apuestas", apuestas);
        model.addAttribute("variables", variables);
        model.addAttribute("eventoSel", eventoSel);

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
        String queryApuestas = "SELECT a FROM Apuesta a";
        List<Apuesta> apuestas = entityManager.createQuery(queryApuestas, Apuesta.class).getResultList();
        model.addAttribute("apuestas", apuestas);
        return "misApuestas-todas";
    }

    @GetMapping("/misApuestas/determinadas")
    public String apuestasDeterminadas(Model model){
        String queryDeterminadas = "SELECT a FROM Apuesta a WHERE a.formulaApuesta.resultado IN ('GANADO', 'PERDIDO')";
        List<Apuesta> apuestasDeterminadas = entityManager.createQuery(queryDeterminadas, Apuesta.class).getResultList();
        model.addAttribute("apuestasDeterminadas", apuestasDeterminadas);
        return "misApuestas-determinadas";
    }

    @GetMapping("/misApuestas/pendientes")
    public String apuestasPendientes(Model model){
        String queryPendientes = "SELECT a FROM Apuesta a WHERE a.formulaApuesta.resultado = 'INDETERMINADO'";
        List<Apuesta> apuestasPendientes = entityManager.createQuery(queryPendientes, Apuesta.class).getResultList();
        model.addAttribute("apuestasPendientes", apuestasPendientes);
        return "misApuestas-pendientes";
    }

    @GetMapping("/admin/usuarios")
    public String usuarios(Model model){
        return "usuarios";
    }

    @GetMapping("/admin/usuarios/usuarioDetalles")
    public String usuarioDetalles(Model model){
        return "usuarioDetalles";
    }

    @GetMapping("/admin/usuarios/transacciones")
    public String transacciones(Model model){
        return "transacciones";
    }
}
