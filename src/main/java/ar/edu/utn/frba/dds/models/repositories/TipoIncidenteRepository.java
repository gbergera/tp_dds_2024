package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.incidente.ReporteDeIncidentes;
import ar.edu.utn.frba.dds.models.entities.incidente.TipoIncidente;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.List;

public class TipoIncidenteRepository implements ICrudRepository, WithSimplePersistenceUnit {
    @Override
    public List buscarTodos() {
        List<TipoIncidente> tipos = entityManager().createQuery("from " + TipoIncidente.class.getName(), TipoIncidente.class).getResultList();
        tipos.forEach(this::refresh);
        return tipos;
    }

    public List buscarFallas() {
        return entityManager().createQuery(
            "FROM " + TipoIncidente.class.getName() + " t WHERE t.descripcion NOT LIKE 'ALERTA DE %'",
            TipoIncidente.class
        ).getResultList();
    }

    @Override
    public Object buscarUltimo(String filtro) {
        return null;
    }

    @Override
    public Object buscar(Long id) {
        TipoIncidente tipo = entityManager().find(TipoIncidente.class, id);
        refresh(tipo);
        return tipo;
    }

    @Override
    public void guardar(Object o) {
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();

        entityManager().persist(o);
        tx.commit();
    }

    @Override
    public void actualizar(Object o) {
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();

        entityManager().merge(o);
        tx.commit();
    }

    @Override
    public void eliminar(Object o) {
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();

        entityManager().remove(o);
        tx.commit();
    }
    public Object buscarPorNombre(String descripcion) {
        try {
            TipoIncidente tipo = entityManager()
                    .createQuery("SELECT h FROM TipoIncidente h WHERE h.descripcion = :descripcion", TipoIncidente.class)
                    .setParameter("descripcion", descripcion)
                    .getSingleResult();
            refresh(tipo);
            return tipo;
        } catch (NoResultException e) {
            return null; // Maneja el caso en que no se encuentre ningún resultado
        }
    }
    public void refresh(Object o) {
        if (entityManager().contains(o)) {
            entityManager().refresh(o);
        }
    }


}
