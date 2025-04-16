package ar.edu.utn.frba.dds.models.repositories;

import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlertaRepository implements ICrudRepository, WithSimplePersistenceUnit {
    private List<Map<String, Object>> alertas = new ArrayList<>();

    public void agregarAlerta(Map<String, Object> alerta) {
             alertas.add(alerta);
    }
    public Map<String, Object> buscarAlertaPorHeladera(String idHeladera) {
        for (Map<String, Object> alerta : alertas) {
            if (alerta.get("heladera").equals(idHeladera)) {
                return alerta;
            }
        }
        return null;
    }
    public void eliminarAlerta(Map<String, Object> alerta) {
        alertas.remove(alerta);
    }

    public List<Map<String, Object>> obtenerAlertas() {
        return new ArrayList<>(alertas);
    }

    public void limpiarAlertas() {
        alertas.clear(); // Limpia las alertas si es necesario
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
    public void refresh(Object o) {
        if (entityManager().contains(o)) {
            entityManager().refresh(o);
        }
    }
}