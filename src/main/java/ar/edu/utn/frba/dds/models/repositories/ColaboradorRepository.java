package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.colaboracion.Colaboracion;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.persona.TipoDocumento;
import ar.edu.utn.frba.dds.models.entities.persona.Usuario;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.util.List;
import java.util.Optional;

public class ColaboradorRepository implements ICrudRepository, WithSimplePersistenceUnit {

    @Override
    public List buscarTodos() {
        List colaboradores = entityManager().createQuery("from " + Colaborador.class.getName()).getResultList();
        colaboradores.forEach(this::refresh);
        return colaboradores;
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



    public void abrirTransaccion() {
        EntityTransaction tx = entityManager().getTransaction();
        if (!tx.isActive()) {
            tx.begin();
        }
    }


    public void commitTransaccion() {
        EntityTransaction tx = entityManager().getTransaction();
        if (tx.isActive() && !tx.getRollbackOnly()) {
            tx.commit();
        }
    }


    public void rollbackTransaccion() {
        EntityTransaction tx = entityManager().getTransaction();
        if (tx.isActive()) {
            tx.rollback();
        }
    }

    @Override
    public void guardar(Object o) {
        entityManager().persist(o);
    }

    public void guardar3(Object o) {
        entityManager().merge(o);
    }
    public void guardar2(Object o) {
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();
        entityManager().persist(o);
        tx.commit();
    }

    @Override
    public void actualizar(Object colaborador) {
        try {
            entityManager().merge(colaborador);
        } catch (Exception e) {
            e.printStackTrace(); // Manejo de excepciones en caso de error
            throw new RuntimeException("Error al actualizar el colaborador: " + e.getMessage());
        }
    }
    public void darDeBaja(Colaborador col) {
        col.setActivo(false);
        entityManager().merge(col);
    }

    @Override
    public void eliminar(Object o) {
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();

        entityManager().remove(o);
        tx.commit();
    }

    public List buscarPorNombre(String nombre) {
        List colaboradores = entityManager().createQuery("from " + Colaborador.class.getName() + " where nombre = :nombre")
                .setParameter("nombre", nombre)
                .getResultList();
        colaboradores.forEach(this::refresh);
        return colaboradores;
    }


    // ColaboradorRepository.java

    public boolean existePorDocumento(TipoDocumento tipoDocumento, String numeroDocumento) {
        Long count = (Long) entityManager().createQuery(
                        "select count(c) from " + Colaborador.class.getName() + " c where c.documento.tipoDocumento = :tipoDocumento and c.documento.numeroDocumento = :numeroDocumento")
                .setParameter("tipoDocumento", tipoDocumento)
                .setParameter("numeroDocumento", numeroDocumento)
                .getSingleResult();
        return count > 0;
    }

    public Colaborador buscarPorDocumento(TipoDocumento tipoDocumento, String numeroDocumento) {
        try {
            Colaborador colaborador = (Colaborador) entityManager().createQuery(
                            "from " + Colaborador.class.getName() + " c where c.documento.tipoDocumento = :tipoDocumento and c.documento.numeroDocumento = :numeroDocumento")
                    .setParameter("tipoDocumento", tipoDocumento)
                    .setParameter("numeroDocumento", numeroDocumento)
                    .getSingleResult();
            refresh(colaborador);
            return colaborador;
        } catch (NoResultException e) {
            return null;
        }
    }


    public Colaborador buscarPorUsuarioId(Long usuarioId) {
        try {
                Colaborador colaborador = (Colaborador) entityManager().createQuery("from " + Colaborador.class.getName() + " c where c.usuario.id = :usuarioId")
                        .setParameter("usuarioId", usuarioId)
                        .getSingleResult();
                refresh(colaborador);
                return colaborador;
            } catch(NoResultException e){
                return null;
            }
    }


    public void refresh(Object o) {
        if (entityManager().contains(o)) {
            entityManager().refresh(o);
        }
    }


}
