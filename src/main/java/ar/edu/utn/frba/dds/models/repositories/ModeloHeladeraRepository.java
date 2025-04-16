package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.ModeloHeladera;
import ar.edu.utn.frba.dds.models.entities.incidente.TipoIncidente;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.List;

public class ModeloHeladeraRepository implements ICrudRepository, WithSimplePersistenceUnit {
    @Override
    public List buscarTodos() {
        List<ModeloHeladera> modelos = entityManager().createQuery("from " + ModeloHeladera.class.getName(), ModeloHeladera.class).getResultList();
        modelos.forEach(this::refresh);
        return modelos;
    }


    @Override
    public Object buscarUltimo(String filtro) {
        return null;
    }

    @Override
    public Object buscar(Long id) {
        ModeloHeladera modelo = entityManager().find(ModeloHeladera.class, id);
        refresh(modelo);
        return modelo;
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
    public Object buscarPorNombre(String nombre) {
        try {
            ModeloHeladera modelo = entityManager()
                    .createQuery("SELECT h FROM ModeloHeladera h WHERE h.nombre = :nombre", ModeloHeladera.class)
                    .setParameter("nombre", nombre)
                    .getSingleResult();
            refresh(modelo);
            return modelo;
        } catch (NoResultException e) {
            return null;
        }
    }
    public void refresh(Object o) {
        if (entityManager().contains(o)) {
            entityManager().refresh(o);
        }
    }


}
