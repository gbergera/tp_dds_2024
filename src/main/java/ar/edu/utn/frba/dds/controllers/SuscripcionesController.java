package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.persona.TipoPersona;
import ar.edu.utn.frba.dds.models.entities.suscripciones.Suscripcion;
import ar.edu.utn.frba.dds.models.entities.suscripciones.SuscripcionHeladeraDesperfecto;
import ar.edu.utn.frba.dds.models.entities.suscripciones.SuscripcionHeladeraLlena;
import ar.edu.utn.frba.dds.models.entities.suscripciones.SuscripcionViandasDisponibles;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.ServiceGeoref;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.ListadoDeLocalidades;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Localidad;
import ar.edu.utn.frba.dds.models.repositories.ColaboracionRepository;
import ar.edu.utn.frba.dds.models.repositories.ColaboradorRepository;
import ar.edu.utn.frba.dds.models.repositories.GeneralRepository;
import ar.edu.utn.frba.dds.models.repositories.HeladeraRepository;
import ar.edu.utn.frba.dds.models.repositories.SuscripcionRepository;
import ar.edu.utn.frba.dds.server.utils.ICrudViewsHandler;
import io.javalin.http.Context;
import javax.mail.MessagingException;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SuscripcionesController extends Controller implements ICrudViewsHandler {
  private SuscripcionRepository repoDeSuscripciones;
  private ColaboradorRepository repoDeColaboradores;
  private HeladeraRepository repoDeHeladeras;
  private Logger logger = Logger.getLogger(SuscripcionesController.class.getName());


  public SuscripcionesController(SuscripcionRepository repoDeSuscripciones,ColaboradorRepository repoDeColaboradores, HeladeraRepository repoDeHeladeras) {
    this.repoDeSuscripciones = repoDeSuscripciones;
    this.repoDeColaboradores = repoDeColaboradores;
    this.repoDeHeladeras = repoDeHeladeras;
  }

  @Override
  public void index(Context context) {

    Long colaborador = Long.valueOf(Objects.requireNonNull(context.sessionAttribute("colaborador-id")));
    List<Suscripcion>  suscripciones = this.repoDeSuscripciones.buscarPorUsuarioId(colaborador);
    Map<String, Object> model = new HashMap<>();
    model.put("suscripciones", suscripciones);
    model.put("suscripcionesActive",true);
    TipoPersona tipoPersona = context.sessionAttribute("tipo-persona");
    model.put("isJuridica", tipoPersona == TipoPersona.JURIDICA);

    // Fetch and add Heladera information
    for (Suscripcion suscripcion : suscripciones) {
      Heladera heladera = this.repoDeHeladeras.buscarPorSuscripcionId(suscripcion.getId());
      if (heladera != null) {
        suscripcion.setNombreHeladera(heladera.getNombre());
      } else {
        suscripcion.setNombreHeladera("No disponible");
      }
    }
    context.render("suscripciones.hbs",model);
  }

  @Override
  public void show(Context context) {

  }

  @Override
  public void create(Context context) {

  }

  @Override
  public void save(Context context) {

    try {
      Long colaboradorId = Long.valueOf(Objects.requireNonNull(context.sessionAttribute("colaborador-id")));
      Colaborador colaborador = (Colaborador) this.repoDeColaboradores.buscar(colaboradorId);
      String nombreHeladera = context.formParam("heladera");
      Heladera heladera = (Heladera) this.repoDeHeladeras.buscarPorNombre(nombreHeladera);
      String modalType = context.formParam("modal_type");
      List<Suscripcion> suscripcionesPorColaborador = this.repoDeSuscripciones.buscarPorUsuarioId(colaboradorId);

      EntityTransaction tx = entityManager().getTransaction();

      if (suscripcionesPorColaborador.stream().anyMatch(s -> s.getNombreHeladera().equals(nombreHeladera) && s.getClass().getSimpleName().equals(modalType))) {
        if (!tx.isActive())
          tx.begin();
        tx.commit();//lo puse por las dudas jsjs
        context.status(500).result("Error al guardar suscripción");
        context.render("error.hbs");

      } else {


        switch (modalType) {
          case "SuscripcionViandasDisponibles":
            SuscripcionViandasDisponibles nuevaSuscripcionViandasDisponibles = new SuscripcionViandasDisponibles();
            nuevaSuscripcionViandasDisponibles.setCantidadMinima(Integer.valueOf(Objects.requireNonNull(context.formParam("cantidad_viandas"))));
            nuevaSuscripcionViandasDisponibles.setColaborador(colaborador);
            heladera.agregarSuscripcion(nuevaSuscripcionViandasDisponibles);

            if (!tx.isActive())
              tx.begin();
            this.repoDeSuscripciones.guardar(nuevaSuscripcionViandasDisponibles);
            this.repoDeHeladeras.actualizar(heladera);
            tx.commit();
            entityManager().refresh(heladera);
            entityManager().refresh(nuevaSuscripcionViandasDisponibles);
            break;

          case "SuscripcionHeladeraLlena":
            SuscripcionHeladeraLlena nuevaSuscripcionHeladeraLlena = new SuscripcionHeladeraLlena();
            nuevaSuscripcionHeladeraLlena.setColaborador(colaborador);
            nuevaSuscripcionHeladeraLlena.setCantidadMaxima(Integer.valueOf(Objects.requireNonNull(context.formParam("cantidad_viandas_llena"))));
            heladera.agregarSuscripcion(nuevaSuscripcionHeladeraLlena);
            if (!tx.isActive())
              tx.begin();
            this.repoDeSuscripciones.guardar(nuevaSuscripcionHeladeraLlena);
            this.repoDeHeladeras.actualizar(heladera);
            tx.commit();
            entityManager().refresh(heladera);
            entityManager().refresh(nuevaSuscripcionHeladeraLlena);
            break;

          case "SuscripcionHeladeraDesperfecto":

            SuscripcionHeladeraDesperfecto nuevaSuscripcionHeladeraDesperfecto = new SuscripcionHeladeraDesperfecto();
            nuevaSuscripcionHeladeraDesperfecto.setColaborador(colaborador);
            heladera.agregarSuscripcion(nuevaSuscripcionHeladeraDesperfecto);
            if (!tx.isActive())
              tx.begin();
            this.repoDeSuscripciones.guardar(nuevaSuscripcionHeladeraDesperfecto);
            this.repoDeHeladeras.actualizar(heladera);
            tx.commit();
            entityManager().refresh(heladera);
            entityManager().refresh(nuevaSuscripcionHeladeraDesperfecto);
            break;
        }

        Map<String, Object> model = new HashMap<>();
        model.put("redireccion", "suscripciones");
        context.render("success.hbs");
      }
    }
      catch(Exception e){
        logger.log(Level.SEVERE, "Error al guardar suscripción: "+e.getMessage(), e);
        context.status(500).result("Error al guardar suscripción");
        context.render("error.hbs");
      }

    }

  @Override
  public void edit(Context context) {

  }

  @Override
  public void update(Context context) {

  }

  @Override
  public void delete(Context context) {
    Long id = Long.valueOf(context.formParam("id-eliminar"));
    EntityTransaction tx = entityManager().getTransaction();
    if(!tx.isActive())
      tx.begin();
    this.repoDeSuscripciones.cancelarSuscripcion(id);
    tx.commit();
    Map<String, Object> model = new HashMap<>();
    model.put("redireccion", "/suscripciones");
    context.render("deleteSuccess.hbs",model);
  }


}
