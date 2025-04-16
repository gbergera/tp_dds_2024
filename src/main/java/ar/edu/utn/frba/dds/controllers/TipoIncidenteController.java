package ar.edu.utn.frba.dds.controllers;


import ar.edu.utn.frba.dds.models.entities.incidente.ReporteDeIncidentes;
import ar.edu.utn.frba.dds.models.entities.incidente.TipoIncidente;
import ar.edu.utn.frba.dds.models.entities.incidente.builder.ReporteDeIncidentesBuilder;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.reportes.ReportesFallaHeladera;
import ar.edu.utn.frba.dds.models.entities.suscripciones.Suscripcion;
import ar.edu.utn.frba.dds.models.repositories.ColaboradorRepository;
import ar.edu.utn.frba.dds.models.repositories.GeneralRepository;
import ar.edu.utn.frba.dds.models.repositories.HeladeraRepository;
import ar.edu.utn.frba.dds.models.repositories.RegistroDeFallaRepository;
import ar.edu.utn.frba.dds.models.repositories.SuscripcionRepository;
import ar.edu.utn.frba.dds.models.repositories.TecnicoRepository;
import ar.edu.utn.frba.dds.models.repositories.TipoIncidenteRepository;
import ar.edu.utn.frba.dds.server.utils.ICrudViewsHandler;
import io.javalin.http.Context;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TipoIncidenteController extends Controller implements ICrudViewsHandler {
  private TipoIncidenteRepository repoTipoIncidentes;

  public TipoIncidenteController(TipoIncidenteRepository repoTipoIncidentes) {
    this.repoTipoIncidentes = repoTipoIncidentes;

  }

  @Override
  public void index(Context context) {
      Map<String, Object> model = new HashMap<>();

  }
  public void getAll(Context context) {
    List  tiposIncidente = this.repoTipoIncidentes.buscarTodos();
    context.json(tiposIncidente);
  }

  public void traerFallas(Context context) {
    List  fallas = this.repoTipoIncidentes.buscarFallas();
    context.json(fallas);
  }

  @Override
  public void show(Context context) {
    Map<String, Object> model = new HashMap<>();
  }

  @Override
  public void create(Context context) {
    Map<String, Object> model = new HashMap<>();
  }

  @Override
  public void save(Context context) {

  }

  @Override
  public void edit(Context context) {
    Map<String, Object> model = new HashMap<>();
  }

  @Override
  public void update(Context context) {
    Map<String, Object> model = new HashMap<>();
  }

  @Override
  public void delete(Context context) {
    Map<String, Object> model = new HashMap<>();
  }
}

