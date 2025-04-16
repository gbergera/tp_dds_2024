package ar.edu.utn.frba.dds.models.entities.formulario;

import ar.edu.utn.frba.dds.converters.LocalDateTimeConverter;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Setter
@Getter
@Entity
@Table(name = "formulario_contestado")
public class FormularioContestado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = LocalDateTimeConverter.class)
    @Column(name = "fecha_contestado")
    private LocalDateTime fechaContestado;

    @ManyToOne
    @JoinColumn(name = "formulario_id", referencedColumnName = "id")
    private Formulario formulario;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "formulario_contestado_id")
    private List<Respuesta> respuestas = new ArrayList<>();

    public void agregarRespuesta(Respuesta respuesta){
        this.respuestas.add(respuesta);
    }
}
