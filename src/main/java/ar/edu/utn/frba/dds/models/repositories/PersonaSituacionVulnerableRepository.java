package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.colaboracion.PersonaSituacionVulnerable;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import java.util.List;
import javax.persistence.EntityTransaction;

public class PersonaSituacionVulnerableRepository implements ICrudRepository, WithSimplePersistenceUnit {

        @Override
        public List buscarTodos() {
            List personas = entityManager().createQuery("from " + PersonaSituacionVulnerable.class.getName()).getResultList();
            personas.forEach(this::refresh);
            return personas;
        }

        @Override
        public Object buscarUltimo(String filtro) {
            return null;
        }

        @Override
        public Object buscar(Long id) {
            PersonaSituacionVulnerable persona = entityManager().find(PersonaSituacionVulnerable.class, id);
            refresh(persona);
            return persona;
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
