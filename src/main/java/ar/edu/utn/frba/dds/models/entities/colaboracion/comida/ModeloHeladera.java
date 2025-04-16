package ar.edu.utn.frba.dds.models.entities.colaboracion.comida;

import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.units.qual.C;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "modelo_heladera")
public class ModeloHeladera {
    @Id
    @GeneratedValue
    private Long id;
    @Column(name = "nombre")
    private String nombre;
    @Column(name = "temperatura_minima")
    private Double temperaturaMinima;
    @Column(name = "temperatura_maxima")
    private Double temperaturaMaxima;
}
