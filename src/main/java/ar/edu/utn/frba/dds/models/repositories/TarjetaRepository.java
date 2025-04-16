package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta.Tarjeta;
import ar.edu.utn.frba.dds.models.entities.incidente.TipoIncidente;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.logging.Logger;

public class TarjetaRepository implements ICrudRepository, WithSimplePersistenceUnit {
    private Logger logger = Logger.getLogger(TarjetaRepository.class.getName());
    @Override
    public List buscarTodos() {
        return entityManager().createQuery("from " + Tarjeta.class.getName()).getResultList();
    }

    @Override
    public Object buscarUltimo(String filtro) {
        return null;
    }

    @Override
    public Object buscar(Long id) {
        return entityManager().find(Tarjeta.class, id);
    }
    public Tarjeta buscarPorCodigo(String codigoAlfaNumerico) {
        String hql = "SELECT t FROM Tarjeta t WHERE t.codigoAlfaNumerico = :codigoAlfaNumerico";

        try {
            return entityManager()
                    .createQuery(hql, Tarjeta.class)
                    .setParameter("codigoAlfaNumerico", codigoAlfaNumerico)
                    .getSingleResult();  // Devuelve una sola tarjeta que coincide con el código
        } catch (NoResultException e) {
            logger.info("No se encontró ninguna tarjeta con el código: " + codigoAlfaNumerico);
            return null;  // Si no hay resultado, retorna null
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
}
