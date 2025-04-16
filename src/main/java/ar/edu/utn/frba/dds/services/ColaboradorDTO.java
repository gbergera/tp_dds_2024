package ar.edu.utn.frba.dds.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ColaboradorDTO {

    private String nombre;
    private String apellido;
    private String contacto;
    private int donaciones;
    private double puntos;


}
