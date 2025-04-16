package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.repositories.ICrudRepository;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.List;

public class HeladeraRepository implements ICrudRepository, WithSimplePersistenceUnit{


    @Override
    public List buscarTodos() {
        List heladeras = entityManager().createQuery("from " + Heladera.class.getName()).getResultList();
        heladeras.forEach(this::refresh);
        return heladeras;
    }

    @Override
    public Object buscarUltimo(String filtro) {
        return null;
    }

    @Override
    public Object buscar(Long id) {
        Heladera heladera = entityManager().find(Heladera.class, id);
        refresh(heladera);
        return heladera;
    }

    public Object buscarPorNombre(String nombre) {
        try {
            Heladera heladera = entityManager()
                    .createQuery("SELECT h FROM Heladera h WHERE h.nombre = :nombre", Heladera.class)
                    .setParameter("nombre", nombre)
                    .getSingleResult();
            refresh(heladera);
            return heladera;
        } catch (NoResultException e) {
            return null; // Maneja el caso en que no se encuentre ningún resultado
        }
    }
    public Object buscarPorId(Long id) {
        try {
            // Utiliza una consulta JPQL para buscar por el nombre
            return entityManager()
                    .createQuery("SELECT h FROM Heladera h WHERE h.id = :id", Heladera.class)
                    .setParameter("id", id)
                    .getSingleResult(); // Obtiene un único resultado
        } catch (NoResultException e) {
            return null; // Maneja el caso en que no se encuentre ningún resultado
        }
    }
    @Override
    public void guardar(Object o) {
        entityManager().persist(o);
    }

    @Override
    public void actualizar(Object o) {
        entityManager().merge(o);
    }

    @Override
    public void eliminar(Object o) {
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();

        entityManager().remove(o);
        tx.commit();
    }

    public Heladera buscarPorSuscripcionId(Long id) {
        try {
            Heladera heladera = entityManager()
                    .createQuery("SELECT h FROM Heladera h JOIN h.suscripciones s WHERE s.id = :id", Heladera.class)
                    .setParameter("id", id)
                    .getSingleResult();
            refresh(heladera);
            return heladera;
        } catch (NoResultException e) {
            return null; // Maneja el caso
        }
    }

    public void darDeBaja(Heladera col) {
        col.setActiva(false);
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();

        entityManager().merge(col);
        tx.commit();
        refresh(col);
    }

    public List<Heladera> buscarPorAdministrador(Long administradorID) {
        List<Heladera> heladeras = entityManager()
                .createQuery("FROM " + Heladera.class.getName() + " h WHERE h.administrador.id = :administradorID", Heladera.class)
                .setParameter("administradorID", administradorID)
                .getResultList();
        heladeras.forEach(this::refresh);
        return heladeras;
    }

    public void refresh(Object o) {
        if (entityManager().contains(o)) {
            entityManager().refresh(o);
        }
    }

    public List<Heladera> buscarPorLocalidad(String localidad) {
        String sql = "SELECT h.* FROM Heladera h " +
                "JOIN heladera_direccion dh ON h.id = dh.heladera_id " +
                "JOIN direccion d ON dh.direccion_id = d.id " +
                "JOIN localidad l ON d.localidad_id = l.id " +
                "WHERE l.nombre = :localidad";

        // Ejecutar la consulta nativa utilizando el EntityManager
        return entityManager().createNativeQuery(sql, Heladera.class)
                .setParameter("localidad", localidad)
                .getResultList();
    }
}
