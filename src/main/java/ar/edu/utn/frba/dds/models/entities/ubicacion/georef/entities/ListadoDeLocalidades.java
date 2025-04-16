package ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ListadoDeLocalidades {
    private int cantidad;

    private int inicio;

    private List<Localidad> localidades;

    private int total;

}
