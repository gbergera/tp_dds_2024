package ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "provincia")
public class Provincia {
    @Id
    @GeneratedValue
    public int id;

    @Column(name = "nombre")
    public String nombre;
}
