package es.ucm.fdi.iw.model;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class TablaMercado {
    private List<ColumnaMercado> columnas;
    private List<FilaMercado> cuotas;
    private Evento evento;
}
