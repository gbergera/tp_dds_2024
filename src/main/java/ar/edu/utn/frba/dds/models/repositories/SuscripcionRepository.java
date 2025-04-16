package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.colaboracion.Colaboracion;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.persona.TipoDocumento;
import ar.edu.utn.frba.dds.models.entities.suscripciones.Suscripcion;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.logging.Logger;

public class SuscripcionRepository implements ICrudRepository, WithSimplePersistenceUnit {
  private Logger logger = Logger.getLogger(this.getClass().getName());

  @Override
  public List buscarTodos() {
    List suscripciones = entityManager().createQuery("from " + Suscripcion.class.getName()).getResultList();
    suscripciones.forEach(this::refresh);
    return suscripciones;
  }

  @Override
  public Object buscarUltimo(String filtro) {
    return null;
  }

  @Override
  public Object buscar(Long id) {
    Suscripcion suscripcion = entityManager().find(Suscripcion.class, id);
    refresh(suscripcion);
    return suscripcion;
  }

  @Override
  public void guardar(Object o) {

    entityManager().persist(o);
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


  public List buscarPorUsuarioId(Long usuarioId) {
    try {
      List suscripciones = entityManager().createQuery("from " + Suscripcion.class.getName() + " c where c.colaborador.id = :usuarioId")
              .setParameter("usuarioId", usuarioId)
              .getResultList();
      suscripciones.forEach(this::refresh);
      return suscripciones;
    } catch (NoResultException e) {
      return null;
    }
  }

  public List buscarPorHeladerDesperfecto(Long heladeraID) {

    String sql = "SELECT s.* FROM Suscripcion s " +
            "WHERE s.tipo_suscripcion = :tipo_suscripcion " +
            "AND s.heladera_id = :heladera_id";



    return entityManager().createNativeQuery(sql, Suscripcion.class)
            .setParameter("tipo_suscripcion", "SUB_HELADERA_DESPERFECTO")
            .setParameter("heladera_id", heladeraID)
            .getResultList();
  }



  public void cancelarSuscripcion(Long id) {
    try {

      entityManager().createQuery("DELETE FROM " + Suscripcion.class.getName() + " c WHERE c.id = :id")
          .setParameter("id", id)
          .executeUpdate();

    } catch (NoResultException e) {
      logger.info("No se encontró la suscripción con ID: " + id);
    }
  }

  public void refresh(Object o) {
    if (entityManager().contains(o)) {
      entityManager().refresh(o);
    }
  }

}
