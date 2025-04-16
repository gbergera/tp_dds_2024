package ar.edu.utn.frba.dds.models.entities.ubicacion.georef;

import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.ListadoDeDirecciones;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.ListadoDeLocalidades;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.ListadoDeMunicipios;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.ListadoDeProvincias;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServiceGeorefQueries {
    @GET("provincias")
    Call<ListadoDeProvincias> provincias();
    @GET("municipios")
    Call<ListadoDeMunicipios> municipios();

    @GET("municipios")
    Call<ListadoDeMunicipios> municipiosPorProvincia(@Query("provincia") int idProvincia, @Query("campos") String campos, @Query("max") int max);
    @GET("localidades")
    Call<ListadoDeLocalidades> localidades();
    @GET("localidades")
    Call<ListadoDeLocalidades> localidadesPorProvincia(@Query("provincia") int idProvincia, @Query("campos") String campos);
    @GET("localidades")
    Call<ListadoDeLocalidades> localidadesPorMunicipio(@Query("municipio") int idMunicipio, @Query("max") int max);

    @GET("direcciones")
    Call<ListadoDeDirecciones> direcciones(@Query("direccion") String direccion, @Query("provincia") String provincia);
}
