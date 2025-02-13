package es.ucm.fdi.iw.model;

import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class Etiqueta {
    private String nombre;
    private List<Evento> eventos; //eventos en los que sale
}
