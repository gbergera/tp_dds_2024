package ar.edu.utn.frba.dds.models.entities.formulario;

import ar.edu.utn.frba.dds.converters.LocalDateTimeConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "formulario")
public class Formulario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Convert(converter = LocalDateTimeConverter.class)
    @Column(name = "fecha_creado")
    private LocalDateTime fechaFormulario;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "formulario_id")
    private List<Pregunta> preguntas = new ArrayList<>();

    public void agregarItem(Pregunta pregunta){
        this.preguntas.add(pregunta);
    }
}
