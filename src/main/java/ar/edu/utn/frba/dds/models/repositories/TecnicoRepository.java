package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta.Tarjeta;
import ar.edu.utn.frba.dds.models.entities.incidente.ReporteDeIncidentes;
import ar.edu.utn.frba.dds.models.entities.persona.Tecnico;
import ar.edu.utn.frba.dds.models.entities.persona.Visita;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.List;

public class TecnicoRepository  implements ICrudRepository, WithSimplePersistenceUnit {


    @Override
    public List buscarTodos() {
        List<Tecnico> tecnicos = entityManager().createQuery("from " + Tecnico.class.getName()).getResultList();
        tecnicos.forEach(this::refresh);
        return tecnicos;
    }
    public List buscarPorLocalidad(String localidad){
        List<Tecnico> tecnicos = entityManager()
                .createQuery("from " + Tecnico.class.getName() + " where localidad = :localidad", Tecnico.class)
                .setParameter("localidad", localidad)
                .getResultList();
        tecnicos.forEach(this::refresh);
        return tecnicos;
    }
    @Override
    public Object buscarUltimo(String filtro) {
        return null;
    }

    @Override
    public Object buscar(Long id) {
        Tecnico tecnico = entityManager().find(Tecnico.class, id);
        refresh(tecnico);
        return tecnico;
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

    public Tecnico buscarPorUsuarioId(Long usuarioId) {
        try {
            Tecnico tecnico = (Tecnico) entityManager().createQuery("from " + Tecnico.class.getName() + " c where c.usuario.id = :usuarioId")
                    .setParameter("usuarioId", usuarioId)
                    .getSingleResult();
            refresh(tecnico);
            return tecnico;
        } catch(NoResultException e){
            return null;
        }
    }

    public List<Visita> findVisitasByIncidenteId(Long incidenteId) {
        return entityManager().createQuery(
                        "SELECT v FROM Visita v WHERE v.reporte.id = :incidenteId", Visita.class)
                .setParameter("incidenteId", incidenteId)
                .getResultList();
    }

    public ReporteDeIncidentes findIncidenteById(Long incidenteId) {
        try {
            return entityManager().createQuery(
                            "SELECT i FROM ReporteDeIncidentes i WHERE i.id = :incidenteId", ReporteDeIncidentes.class)
                    .setParameter("incidenteId", incidenteId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public void actualizarHeladera(Heladera heladera) {
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();

        entityManager().merge(heladera);
        tx.commit();
    }

    public List buscarTodasLasTarjetas(){
        return entityManager().createQuery("from " + Tarjeta.class.getName()).getResultList();
    }
}
