package ar.edu.utn.frba.dds.models.entities.ubicacion.recomendador;

import ar.edu.utn.frba.dds.models.entities.ubicacion.Ubicacion;
import ar.edu.utn.frba.dds.models.entities.ubicacion.recomendador.config.RecomendadorConfig;
import ar.edu.utn.frba.dds.models.entities.ubicacion.recomendador.entities.ListadoDeUbicaciones;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.io.IOException;

public class ServiceRecomendador {
    private static ServiceRecomendador instancia = null;
    private static int maximaCantidadRegistrosDefault = 200;
    private Retrofit retrofit;

   private ServiceRecomendador() {
     this.retrofit = new Retrofit.Builder()
         .baseUrl(RecomendadorConfig.urlApi)
         .addConverterFactory(GsonConverterFactory.create())
         .build();
   }



    public static ServiceRecomendador instancia(){
    if(instancia== null){
      instancia = new ServiceRecomendador();
    }
    return instancia;
  }

  public ListadoDeUbicaciones listadoDeUbicaciones(Float latitud, Float longitud, Double radio) throws IOException {
     ServiceRecomendadorQueries serviceRecomendadorQueries = this.retrofit.create(ServiceRecomendadorQueries.class);
     Call<ListadoDeUbicaciones> requestListadoUbicaciones = serviceRecomendadorQueries.recomendacion(latitud,longitud,radio);
     Response<ListadoDeUbicaciones> responseListadoUbicaciones = requestListadoUbicaciones.execute();
     return responseListadoUbicaciones.body();
  }
}
