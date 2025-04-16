package ar.edu.utn.frba.dds.models.entities.ubicacion.georef;

import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.*;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.config.GeorefConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.time.LocalDate;

public class ServiceGeoref {
    private static ServiceGeoref instancia = null;
    private static int maximaCantidadRegistrosDefault = 200;
    private Retrofit retrofit;

    private ServiceGeoref() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ListadoDeLocalidades.class, new ListadoDeLocalidadesAdapter())
                .create();

        this.retrofit = new Retrofit.Builder()
                .baseUrl(GeorefConfig.urlApi)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static ServiceGeoref instancia(){
        if(instancia== null){
            instancia = new ServiceGeoref();
        }
        return instancia;
    }
    public ListadoDeProvincias listadoDeProvincias() throws IOException {
        ServiceGeorefQueries serviceGeorefQueries = this.retrofit.create(ServiceGeorefQueries.class);
        Call<ListadoDeProvincias> requestProvinciasArgentinas = serviceGeorefQueries.provincias();
        Response<ListadoDeProvincias> responseProvinciasArgentinas = requestProvinciasArgentinas.execute();
        return responseProvinciasArgentinas.body();
    }
    public ListadoDeMunicipios listadoDeMunicipios() throws IOException {
        ServiceGeorefQueries serviceGeorefQueries = this.retrofit.create(ServiceGeorefQueries.class);
        Call<ListadoDeMunicipios> requestListadoDeMunicipios = serviceGeorefQueries.municipios();
        Response<ListadoDeMunicipios> responseListadoDeMunicipios = requestListadoDeMunicipios.execute();
        return responseListadoDeMunicipios.body();
    }

    public ListadoDeMunicipios listadoDeMunicipiosPorProvincia(Integer id) throws IOException {
        ServiceGeorefQueries serviceGeorefQueries = this.retrofit.create(ServiceGeorefQueries.class);
        Call<ListadoDeMunicipios> requestListadoDeMunicipios = serviceGeorefQueries.municipiosPorProvincia(id, "id,nombre,provincia", maximaCantidadRegistrosDefault);
        Response<ListadoDeMunicipios> responseListadoDeMunicipios = requestListadoDeMunicipios.execute();
        return responseListadoDeMunicipios.body();
    }
    public ListadoDeLocalidades listadoDeLocalidades() throws IOException {
        ServiceGeorefQueries serviceGeorefQueries = this.retrofit.create(ServiceGeorefQueries.class);
        Call<ListadoDeLocalidades> requestListadoDeLocalidades = serviceGeorefQueries.localidades();
        Response<ListadoDeLocalidades> responseListadoDeLocalidades = requestListadoDeLocalidades.execute();
        return responseListadoDeLocalidades.body();
    }
    public ListadoDeLocalidades listadoDeLocalidadesPorProvincia(Integer id) throws IOException {
        ServiceGeorefQueries serviceGeorefQueries = this.retrofit.create(ServiceGeorefQueries.class);
        Call<ListadoDeLocalidades> requestListadoDeLocalidades = serviceGeorefQueries.localidadesPorProvincia(id, "id,nombre");
        Response<ListadoDeLocalidades> responseListadoDeLocalidades = requestListadoDeLocalidades.execute();
        return responseListadoDeLocalidades.body();
    }
    public ListadoDeLocalidades listadoDeLocalidadesPorMunicipio(Integer id) throws IOException {
        ServiceGeorefQueries serviceGeorefQueries = this.retrofit.create(ServiceGeorefQueries.class);
        Call<ListadoDeLocalidades> requestListadoDeLocalidades = serviceGeorefQueries.localidadesPorMunicipio(id, 100);
        Response<ListadoDeLocalidades> responseListadoDeLocalidades = requestListadoDeLocalidades.execute();
       return responseListadoDeLocalidades.body();
    }
    public ListadoDeDirecciones direccionExactaSegunProvincia(String calle, Integer altura, String provincia) throws IOException {
        ServiceGeorefQueries serviceGeorefQueries = this.retrofit.create(ServiceGeorefQueries.class);
        Call<ListadoDeDirecciones> requestListadoDeDirecciones = serviceGeorefQueries.direcciones(calle + altura.toString(), provincia);
        Response<ListadoDeDirecciones> responseListadoDeDirecciones = requestListadoDeDirecciones.execute();
        return responseListadoDeDirecciones.body();
    }
}
