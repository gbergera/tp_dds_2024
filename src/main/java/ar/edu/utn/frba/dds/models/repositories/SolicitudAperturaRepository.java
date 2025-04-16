package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.colaboracion.Colaboracion;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta.SolicitudApertura;
import ar.edu.utn.frba.dds.models.entities.incidente.TipoIncidente;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import retrofit2.http.Query;

import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class SolicitudAperturaRepository implements ICrudRepository, WithSimplePersistenceUnit {
    private Logger logger = Logger.getLogger(SolicitudAperturaRepository.class.getName());
    @Override
    public List buscarTodos() {
        return entityManager().createQuery("from " + SolicitudApertura.class.getName()).getResultList();
    }

    @Override
    public Object buscarUltimo(String filtro) {
        return null;
    }

    @Override
    public Object buscar(Long id) {
        return entityManager().find(SolicitudApertura.class, id);
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

    public SolicitudApertura buscarPorHeladeraYTarjeta(Long idHeladera, Long idTarjeta) {

        String hql = "SELECT s FROM SolicitudApertura s " +
                "WHERE s.heladeraAAbrir.id = :idHeladera " + // Filtro por ID de la heladera
                "AND s.tarjeta.id = :idTarjeta " +          // Filtro por ID de la tarjeta
                "ORDER BY s.fechaSolicitud DESC";           // Ordena por la fecha de la solicitud (de más reciente a más antiguo)


        List<SolicitudApertura> resultados = entityManager()
                .createQuery(hql, SolicitudApertura.class)
                .setParameter("idHeladera", idHeladera)
                .setParameter("idTarjeta", idTarjeta)
                .getResultList();  // Devuelve una lista de resultados
        if (resultados.isEmpty()) {
            logger.info("No se encontraron solicitudes para la heladera y tarjeta proporcionadas.");
            return null;
        }

        return resultados.get(0);
    }

    public Colaboracion buscarColaboracionPorSolicitud(Long idSolicitud) {
        String hql = "SELECT c FROM Colaboracion c JOIN c.solicitudesApertura s WHERE s.id = :idSolicitud";
        try {
            return entityManager()
                    .createQuery(hql, Colaboracion.class)
                    .setParameter("idSolicitud", idSolicitud)
                    .getSingleResult();
        } catch (NoResultException e) {
            logger.severe("No se encontró una colaboración para la solicitud de apertura con ID " + idSolicitud);
            return null; // No Colaboracion found for the given SolicitudApertura
        }
    }
}
