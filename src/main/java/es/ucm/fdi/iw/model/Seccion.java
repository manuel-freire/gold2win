package es.ucm.fdi.iw.model;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Seccion {
    private String nombre;
    private List<TablaPlantilla> plantilla;
    private List<Evento> eventos; 
    private List<AgrupacionSecciones> agrupaciones;
    //tiene imagen tambien pero se guarda en un archivo.
}
