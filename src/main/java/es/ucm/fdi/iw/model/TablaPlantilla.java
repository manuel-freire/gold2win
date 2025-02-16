package es.ucm.fdi.iw.model;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TablaPlantilla{
    private List<String> columnas;
    private String titulo;
    private long id;
    private Seccion seccion;
}