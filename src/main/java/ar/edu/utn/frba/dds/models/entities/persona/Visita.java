package ar.edu.utn.frba.dds.models.entities.persona;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.incidente.ReporteDeIncidentes;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "visita")
public class Visita {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "heladera_id")
    private Heladera heladeraVisitada;

    @Column(name = "fechaYHora")
    private LocalDateTime fechaYHora;

    @Column(name = "solucionado")
    private Boolean solucionado;

    @Column(name = "descripcion")
    private String descripcion;

    @ManyToOne
    @JoinColumn(name = "reporte_id")
    private ReporteDeIncidentes reporte;

    @Column(name = "foto")
    private String foto;
}
