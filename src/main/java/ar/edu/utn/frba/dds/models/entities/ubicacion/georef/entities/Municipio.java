package ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "municipio")
public class Municipio {
    @Id
    @GeneratedValue
    public int id;

    @Column(name = "nombre")
    public String nombre;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "provincia_id")
    public Provincia provincia;
}
