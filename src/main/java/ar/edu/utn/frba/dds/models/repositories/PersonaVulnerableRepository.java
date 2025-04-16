package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.colaboracion.Colaboracion;
import ar.edu.utn.frba.dds.models.entities.colaboracion.PersonaSituacionVulnerable;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import javax.persistence.*;
import java.util.List;

public class PersonaVulnerableRepository implements ICrudRepository, WithSimplePersistenceUnit {
    @Override
    public List buscarTodos() {
        List colaboraciones = entityManager().createQuery("from " + Colaboracion.class.getName()).getResultList();
        colaboraciones.forEach(this::refresh);
        return colaboraciones;
    }

    @Override
    public Object buscarUltimo(String filtro) {
        return null;
    }

    @Override
    public Object buscar(Long id) {
        Colaboracion colaboracion = entityManager().find(Colaboracion.class, id);
        refresh(colaboracion);
        return colaboracion;
    }
    public List<PersonaSituacionVulnerable> buscarPorColaboradorResponsable(Long id) {

        Query query = entityManager().createQuery("SELECT p FROM PersonaSituacionVulnerable p WHERE p.colaboradorResponsable.id = :id");
        query.setParameter("id", id);
        List personas = query.getResultList();
        personas.forEach(this::refresh);
        return personas;
    }

    @Override
    public void guardar(Object o) {
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();

        entityManager().persist(o);
        tx.commit();
        refresh(o);
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
