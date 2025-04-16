package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.incidente.TipoIncidente;
import ar.edu.utn.frba.dds.models.entities.persona.Visita;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.List;

public class VisitasRepository implements ICrudRepository, WithSimplePersistenceUnit {
    @Override
    public List buscarTodos() {
        List<Visita> visitas = entityManager().createQuery("from " + Visita.class.getName(), Visita.class).getResultList();
        visitas.forEach(this::refresh);
        return visitas;
    }

    public List<Visita> obtenerVisitasPorAdministrador(Long colaboradorId) {
        String sql = "SELECT v.* FROM visita v " +
                "JOIN heladera h ON v.heladera_id = h.id " +
                "WHERE h.administrador_id = :colaboradorId ";

        return entityManager().createNativeQuery(sql, Visita.class).setParameter("colaboradorId", colaboradorId).getResultList();

    }

    public List<Visita> obtenerVisitasPorSuscripcion(Long colaboradorId) {
        String sql = "SELECT v.* FROM visita v " +
                "JOIN suscripcion s ON v.heladera_id = s.heladera_id " +
                "WHERE s.colaborador_id = :colaboradorId ";

        return entityManager().createNativeQuery(sql, Visita.class).setParameter("colaboradorId", colaboradorId).getResultList();

    }

    @Override
    public Object buscarUltimo(String filtro) {
        return null;
    }

    @Override
    public Object buscar(Long id) {
        Visita visita = entityManager().find(Visita.class, id);
        refresh(visita);
        return visita;
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

    public void refresh(Object o) {
        if (entityManager().contains(o)) {
            entityManager().refresh(o);
        }
    }

}
