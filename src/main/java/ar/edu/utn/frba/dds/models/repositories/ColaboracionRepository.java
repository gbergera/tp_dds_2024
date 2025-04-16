package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.colaboracion.Colaboracion;
import ar.edu.utn.frba.dds.models.entities.puntos.Oferta;
import ar.edu.utn.frba.dds.models.entities.puntos.Rubro;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import javax.persistence.EntityTransaction;
import java.util.List;

public class ColaboracionRepository implements ICrudRepository, WithSimplePersistenceUnit {
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

    public List buscarOfertas() {
        List ofertas = entityManager().createQuery("from " + Oferta.class.getName()).getResultList();
        ofertas.forEach(this::refresh);
        return ofertas;
    }
    public List<Oferta> buscarOfertasPorColaborador(Long colaboradorId) {
        List<Oferta> ofertas = entityManager()
                .createQuery("FROM " + Oferta.class.getName() + " o WHERE o.colaborador.id = :colaboradorId", Oferta.class)
                .setParameter("colaboradorId", colaboradorId)
                .getResultList();
        ofertas.forEach(this::refresh);
        return ofertas;
    }
    public Oferta buscarOfertaPorId(Long id) {
        Oferta oferta = entityManager().find(Oferta.class, id);
        refresh(oferta);
        return oferta;
    }

    public List buscarRubros() {
        List rubros = entityManager().createQuery("from " + Rubro.class.getName()).getResultList();
        rubros.forEach(this::refresh);
        return rubros;
    }

    public void refresh(Object o) {
        if (entityManager().contains(o)) {
            entityManager().refresh(o);
        }
    }

}
