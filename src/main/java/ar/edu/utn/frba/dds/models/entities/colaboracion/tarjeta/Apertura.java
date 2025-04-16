package ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "apertura")
public class Apertura {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "fecha_y_hora")
    private LocalDateTime fechaYHora;

    @ManyToOne
    @JoinColumn(name = "heladera_id")
    private Heladera heladera;

    @ManyToOne
    @JoinColumn(name = "solictud_apertura_id")
    private SolicitudApertura solicitudApertura;
}
