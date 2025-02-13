package es.ucm.fdi.iw.model;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class CampoApostable {
    private Double cuota;
    private FilaMercado contexto;
    private ColumnaMercado columna;
    private List<Apuesta> apuestasHechas;
    private boolean resuleto; //indica si está resuelto (por defecto false)
    private boolean resultado; //indica si es el resultado correcto (no es valido hasta que resuelto es true)
}
