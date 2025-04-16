package ar.edu.utn.frba.dds.models.repositories;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.ModeloHeladera;
import ar.edu.utn.frba.dds.models.entities.persona.Usuario;
import ar.edu.utn.frba.dds.models.entities.puntos.Rubro;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Localidad;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Municipio;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Provincia;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

public class GeneralRepository implements WithSimplePersistenceUnit, ICrudRepository {

    public Object controlarSiExisteRubro(String descripcion) {
        try {
            Rubro rubro = entityManager().createQuery("from " + Rubro.class.getName() + " where lower(descripcion) = :descripcion", Rubro.class)
                    .setParameter("descripcion", descripcion.toLowerCase())
                    .getSingleResult();
            refresh(rubro);
            return rubro;
        } catch (NoResultException e) {
            return null;
        }
    }

    public Object controlarSiExisteProvincia(String nombre) {
        try {
            List<Provincia> resultList = entityManager().createQuery("from " + Provincia.class.getName() + " where lower(nombre) = :nombre", Provincia.class)
                    .setParameter("nombre", nombre.toLowerCase())
                    .getResultList();

            if (resultList.isEmpty()) {
                return null;
            } else {
                Provincia provincia = resultList.get(0);
                refresh(provincia);
                return provincia;
            }
        } catch (NoResultException e) {
            return null;
        }
    }

    public Object controlarSiExisteMunicipio(String nombre) {
        try {
            List<Municipio> resultList = entityManager().createQuery("from " + Municipio.class.getName() + " where lower(nombre) = :nombre", Municipio.class)
                    .setParameter("nombre", nombre.toLowerCase())
                    .getResultList();

            if (resultList.isEmpty()) {
                return null;
            } else {
                Municipio municipio = resultList.get(0);
                refresh(municipio);
                return municipio;
            }
        } catch (NoResultException e) {
            return null;
        }
    }

    public Object controlarSiExisteLocalidad(String nombre) {
        try {
            List<Localidad> resultList = entityManager().createQuery("from " + Localidad.class.getName() + " where lower(nombre) = :nombre", Localidad.class)
                    .setParameter("nombre", nombre.toLowerCase())
                    .getResultList();

            if (resultList.isEmpty()) {
                return null;
            } else {
                Localidad localidad = resultList.get(0);
                refresh(localidad);
                return localidad;
            }
        } catch (NoResultException e) {
            return null;
        }
    }

    public Boolean contraloarSiExisteHeladeraPorNombre(String nombre) {
        try {
            Heladera heladera = entityManager().createQuery("from " + Heladera.class.getName() + " where lower(nombre) = :nombre", Heladera.class)
                    .setParameter("nombre", nombre.toLowerCase())
                    .getSingleResult();
            refresh(heladera);
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    public Boolean controlarSiExisteModeloHeladera(String nombre) {
        try {
            ModeloHeladera modeloHeladera = entityManager().createQuery("from " + ModeloHeladera.class.getName() + " where lower(nombre) = :nombre", ModeloHeladera.class)
                    .setParameter("nombre", nombre.toLowerCase())
                    .getSingleResult();
            refresh(modeloHeladera);
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    public Boolean controlarSiExisteTopicMQTTDeHeladera(String topic) {
        try {
            Heladera heladera = entityManager().createQuery("from " + Heladera.class.getName() + " where lower(topicMQTT) = :topic", Heladera.class)
                    .setParameter("topic", topic.toLowerCase())
                    .getSingleResult();
            refresh(heladera);
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }


    private void clearEntityManager() {
        entityManager().clear();  // Método que limpia el EntityManager
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
