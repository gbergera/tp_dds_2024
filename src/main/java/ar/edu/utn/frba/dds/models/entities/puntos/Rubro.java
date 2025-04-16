package ar.edu.utn.frba.dds.models.entities.puntos;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "rubro")
public class Rubro {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "descripcion")
    private String descripcion;
}
