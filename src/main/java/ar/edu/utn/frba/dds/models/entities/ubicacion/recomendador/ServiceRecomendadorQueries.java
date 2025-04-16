package ar.edu.utn.frba.dds.models.entities.ubicacion.recomendador;


import ar.edu.utn.frba.dds.models.entities.ubicacion.recomendador.entities.ListadoDeUbicaciones;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServiceRecomendadorQueries {
  @GET("recomendacion")
  Call<ListadoDeUbicaciones> recomendacion(@Query("lat") Float latitud, @Query("long") Float longitud,@Query("radio") Double radio);
}
