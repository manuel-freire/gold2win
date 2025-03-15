package es.ucm.fdi.iw.services;

import es.ucm.fdi.iw.model.Seccion;
import es.ucm.fdi.iw.model.VariableSeccion;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class SeccionService {

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Seccion guardarSeccion(Seccion seccion) {
        entityManager.persist(seccion); // Guardar en la base de datos
        return seccion;
    }

    @Transactional
    public void eliminarSeccion(Long id) {
        // Busca la entidad que deseas eliminar
        Seccion seccion = entityManager.find(Seccion.class, id);

        // Si la entidad existe, la elimina
        if (seccion != null) {
            //entityManager.remove(seccion);
            seccion.setEnabled(false);
            entityManager.merge(seccion);
        }
    }

    @Transactional
    public VariableSeccion guardarVariableSeccion(VariableSeccion seccionV) {
        entityManager.persist(seccionV); // Guardar en la base de datos
        return seccionV;
    }

    @Transactional
    public void eliminarVariableSeccion(VariableSeccion seccionV) {
        if (seccionV != null) {
            entityManager.remove(seccionV);
        }
    }
}