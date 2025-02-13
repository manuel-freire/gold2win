package es.ucm.fdi.iw.model;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class FilaMercado{
    private String nombre;
    private List<CampoApostable> cuotas;
    private TablaMercado taplaPadre;
}