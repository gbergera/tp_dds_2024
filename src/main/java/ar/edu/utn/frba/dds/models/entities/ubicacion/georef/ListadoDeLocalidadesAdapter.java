package ar.edu.utn.frba.dds.models.entities.ubicacion.georef;

import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.ListadoDeLocalidades;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Localidad;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ListadoDeLocalidadesAdapter extends TypeAdapter<ListadoDeLocalidades> {
    @Override
    public void write(JsonWriter out, ListadoDeLocalidades value) throws IOException {
        // Implement serialization logic if needed
        out.beginObject();
        out.name("localidades").beginArray();
        for (Localidad localidad : value.getLocalidades()) {
            out.beginObject();
            out.name("id").value(localidad.getId());
            out.name("nombre").value(localidad.getNombre());

            out.endObject();
        }
        out.endArray();
        out.endObject();
    }

    @Override
    public ListadoDeLocalidades read(JsonReader in) throws IOException {
        ListadoDeLocalidades listadoDeLocalidades = new ListadoDeLocalidades();
        List<Localidad> localidades = new ArrayList<>();

        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            if (name.equals("localidades")) {
                in.beginArray();
                while (in.hasNext()) {
                    Localidad localidad = new Localidad();
                    in.beginObject();
                    while (in.hasNext()) {
                        String fieldName = in.nextName();
                        if (fieldName.equals("id")) {
                            localidad.setId(in.nextLong());
                        } else if (fieldName.equals("nombre")) {
                            localidad.setNombre(in.nextString());
                        }else {
                                in.skipValue(); // Skip other fields
                            }
                    }
                    in.endObject();
                    localidades.add(localidad);
                }
                in.endArray();
            } else if (name.equals("cantidad")) {
                // Handle the "cantidad" field if it exists
                int cantidad = in.nextInt();
                listadoDeLocalidades.setCantidad(cantidad);
            } else {
                in.skipValue();
            }
        }
        in.endObject();

        listadoDeLocalidades.setLocalidades(localidades);
        return listadoDeLocalidades;
    }
}