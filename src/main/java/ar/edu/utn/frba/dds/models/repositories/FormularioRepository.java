package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.formulario.Formulario;
import ar.edu.utn.frba.dds.models.entities.formulario.FormularioContestado;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import javax.persistence.*;
import java.util.Comparator;
import java.util.List;

public class FormularioRepository implements ICrudRepository, WithSimplePersistenceUnit {
    @Override
    public List buscarTodos() {
        List formulariosContestados = entityManager().createQuery("from " + FormularioContestado.class.getName()).getResultList();
        formulariosContestados.forEach(this::refresh);
        return formulariosContestados;
    }

    public List buscarTodosFormularios() {
        List formularios = entityManager().createQuery("from " + Formulario.class.getName()).getResultList();
        formularios.forEach(this::refresh);
        return formularios;
    }


    @Override
    public Object buscarUltimo(String filtro) {
        List<Formulario> formularios = this.buscarTodosFormularios();
        return formularios.stream()
                .filter(formulario -> formulario.getNombre().toLowerCase().contains(filtro.toLowerCase()))
                .max(Comparator.comparing(Formulario::getFechaFormulario))
                .orElse(null);

    }

    @Override
    public Object buscar(Long id) {
        FormularioContestado formularioContestado = entityManager().find(FormularioContestado.class, id);
        refresh(formularioContestado);
        return formularioContestado;
    }

    public Formulario buscarFormulario(Long id) {
        Formulario formulario = entityManager().find(Formulario.class, id);
        refresh(formulario);
        return formulario;
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
    }

    public void eliminarPorId(Object id) {
        EntityTransaction tx = entityManager().getTransaction();
        if (!tx.isActive()) {
            tx.begin();
        }

        Formulario formulario = entityManager().find(Formulario.class, id);
        if (formulario != null) {
            entityManager().remove(formulario);
        }
        tx.commit();
    }

    public void refresh(Object o) {
        if (entityManager().contains(o)) {
            entityManager().refresh(o);
        }
    }
}
