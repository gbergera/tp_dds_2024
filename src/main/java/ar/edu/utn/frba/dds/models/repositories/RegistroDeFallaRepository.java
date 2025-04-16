package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.colaboracion.Colaboracion;
import ar.edu.utn.frba.dds.models.entities.incidente.ReporteDeIncidentes;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.persona.TipoDocumento;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import javax.persistence.EntityTransaction;
import java.util.List;

public class RegistroDeFallaRepository implements ICrudRepository, WithSimplePersistenceUnit {

  @Override
  public List buscarTodos() {
    List reportes = entityManager().createQuery("from " + ReporteDeIncidentes.class.getName()).getResultList();
    reportes.forEach(this::refresh);
    return reportes;
  }

  public List buscarTodasPorColaboradorId(Long id) {
    List reportes = entityManager().createQuery("from " + ReporteDeIncidentes.class.getName()
                    + " where comunicador_id = :id")
            .setParameter("id", id)
            .getResultList();
    reportes.forEach(this::refresh);
    return reportes;
  }

  @Override
  public Object buscarUltimo(String filtro) {
    return null;
  }

  @Override
  public Object buscar(Long id) {
    Colaborador colaborador = entityManager().find(Colaborador.class, id);
    refresh(colaborador);
    return colaborador;
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

  public void refresh(Object o) {
    if (entityManager().contains(o)) {
      entityManager().refresh(o);
    }
  }


}
