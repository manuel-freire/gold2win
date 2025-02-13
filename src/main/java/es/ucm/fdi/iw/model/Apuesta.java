package es.ucm.fdi.iw.model;


import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Apuesta {
    private List<CampoApostable> camposApostados;
    private double cantidadApostada;
    private User apostante;
}
