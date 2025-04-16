package ar.edu.utn.frba.dds.recomendador;


//import ar.edu.utn.frba.dds.models.entities.ubicacion.recomendador.Recomendador;


import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.ubicacion.Ubicacion;

import ar.edu.utn.frba.dds.models.entities.ubicacion.recomendador.ServiceRecomendador;
import org.junit.jupiter.api.Test;
import java.io.IOException;


public class TestRecepcionRecomendaciones {

 /* @Test
  public void listaDeRecomendaciones(){
    UbicacionAdaptada ubicacion = new Ubicacion();
    ubicacion.setLatitud(-34.598450F);
    ubicacion.setLongitud(-58.420065F);
    ubicacion.setRadio(50.00);

    List<Ubicacion> response = new ArrayList<Ubicacion>();
    response.add(ubicacion);
    response.add(ubicacion);

    Colaborador colaborador = new Colaborador();

    colaborador.setUbicacion(ubicacion);

    Recomendador recomendador = mock(Recomendador.class);
    when(recomendador.pedirRecomendacion(ubicacion)).thenReturn(response);

    colaborador.pedirUbicaciones(recomendador);

    Assertions.assertEquals(2,response.size());

  }*/

@Test
  public void traerRecomendaciones() throws IOException {
  /*ServiceRecomendador serviceRecomendador = ServiceRecomendador.instancia();
  Colaborador colaborador = new Colaborador();

  Ubicacion ubicacion = new Ubicacion();
  ubicacion.setLatitud(-34.598450F);
  ubicacion.setLongitud(-58.420065F);
  ubicacion.setRadio(50.00);

  colaborador.setUbicacion(ubicacion);

  System.out.println(serviceRecomendador.listadoDeUbicaciones(ubicacion.getLatitud(), ubicacion.getLongitud(), ubicacion.getRadio()).ubicaciones.size());
  System.out.println(colaborador.pedirUbicaciones());
*/
}
}

