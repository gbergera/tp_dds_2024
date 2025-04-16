package ar.edu.utn.frba.dds.models.entities.colaboracion;

import ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta.SolicitudApertura;
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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_colaboracion")
public abstract class Colaboracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fechaHoraColaboracion")
    private LocalDateTime fechaColaboracion;

    @OneToMany
    @JoinColumn(name = "colaboracion_id")
    private List<SolicitudApertura> solicitudesApertura = new ArrayList<>();

    @Column(name = "fue_exitosa")
    private Boolean fueExitosa;

    public abstract Double calcularPuntaje();

    public abstract Integer getCantidad();

    public void agregarSolicitud(SolicitudApertura solicitudApertura) {
        solicitudesApertura.add(solicitudApertura);
    }

}
