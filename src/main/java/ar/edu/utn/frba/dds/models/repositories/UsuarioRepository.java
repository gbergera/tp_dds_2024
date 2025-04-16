package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.persona.Usuario;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import javax.persistence.*;
import java.util.List;

public class UsuarioRepository implements ICrudRepository, WithSimplePersistenceUnit {
    @Override
    public List buscarTodos() {
        List<Usuario> usuarios = entityManager().createQuery("from " + Usuario.class.getName(), Usuario.class).getResultList();
        return usuarios;
    }

    @Override
    public Object buscarUltimo(String filtro) {
        return null;
    }

    @Override
    public Object buscar(Long id) {
        Usuario usuario = entityManager().find(Usuario.class, id);
        return usuario;
    }

    @Override
    public void guardar(Object o) {


        entityManager().persist(o);
    }
    public void guardar2(Object o) {
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();
        entityManager().persist(o);
        tx.commit();
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

    public void darDeBaja(Usuario usuario) {
        usuario.setEstado(false);


        entityManager().merge(usuario);


    }

    public Object buscarPorNombre(String nombre) {
        try {
            Usuario usuario = entityManager().createQuery("from " + Usuario.class.getName() + " where nombre = :nombre", Usuario.class)
                    .setParameter("nombre", nombre)
                    .getSingleResult();
            return usuario;
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
