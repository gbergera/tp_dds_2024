package ar.edu.utn.frba.dds.models.entities.formulario;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "opcion")
public class Opcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "detalle")
    private String detalle;

    @Column(name = "es_correcta")
    private Boolean esCorrecta;
}
