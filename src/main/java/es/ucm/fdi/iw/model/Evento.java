package es.ucm.fdi.iw.model;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Evento {
    private List<TablaMercado> mercado; //todas las tablas con las cuotas
    private String titulo;
    private long id;
    private LocalDateTime fecha;
    private Seccion seccion; 
    private List<Etiqueta> etiquetas;
}
