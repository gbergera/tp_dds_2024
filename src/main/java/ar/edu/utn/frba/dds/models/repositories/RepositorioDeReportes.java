package ar.edu.utn.frba.dds.models.repositories;
import ar.edu.utn.frba.dds.models.entities.reportes.ReporteGenerado;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import java.time.LocalDate;
import java.util.List;

public class RepositorioDeReportes implements ICrudRepository, WithSimplePersistenceUnit {



    public void agregarReporte(ReporteGenerado reporte) {
        EntityTransaction tx = entityManager().getTransaction();
        try {
            if (!tx.isActive()) {
                tx.begin();
            }
            entityManager().persist(reporte);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }

    public List<ReporteGenerado> obtenerReportesUltimaSemana() {
        //LocalDate haceUnaSemana = LocalDate.now().minusWeeks(1);
        //TypedQuery<ReporteGenerado> query = entityManager().createQuery(
               // "SELECT r FROM ReporteGenerado r WHERE r.fecha >= :fecha", ReporteGenerado.class);
        //query.setParameter("fecha", haceUnaSemana);

        //return query.getResultList();
        return null;
    }

    @Override
    public List buscarTodos() {
        return null;
    }

    @Override
    public Object buscarUltimo(String filtro) {
        return null;
    }

    @Override
    public Object buscar(Long id) {
        return null;
    }

    @Override
    public void guardar(Object o) {

    }

    @Override
    public void actualizar(Object o) {

    }

    @Override
    public void eliminar(Object o) {

    }
}

