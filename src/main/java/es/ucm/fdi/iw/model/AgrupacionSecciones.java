package es.ucm.fdi.iw.model;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class AgrupacionSecciones {
    private String nombre;
    private List<Seccion> secciones;
}