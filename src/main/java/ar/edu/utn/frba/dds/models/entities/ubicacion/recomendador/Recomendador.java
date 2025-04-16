package ar.edu.utn.frba.dds.models.entities.ubicacion.recomendador;


import ar.edu.utn.frba.dds.models.entities.ubicacion.Ubicacion;
import ar.edu.utn.frba.dds.models.entities.ubicacion.recomendador.entities.ListadoDeUbicaciones;
import java.io.IOException;
import java.util.List;

public class Recomendador {
  private static ServiceRecomendador service = ServiceRecomendador.instancia();

  public static List<Ubicacion> pedirRecomendacion(Float latitud, Float longitud, Double radio) throws IOException {
    ListadoDeUbicaciones listadoDeUbicaciones = service.listadoDeUbicaciones( latitud,longitud,radio);
    return listadoDeUbicaciones.ubicaciones;
  }

}
