package ar.edu.utn.frba.dds.models.entities.colaboracion.TipoDeColaboraciones;

import ar.edu.utn.frba.dds.converters.LocalDateConverter;
import ar.edu.utn.frba.dds.models.entities.colaboracion.Colaboracion;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.puntos.config.PuntajeConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@DiscriminatorValue("REDISTRIBUCION_VIANDAS")
@NoArgsConstructor
public class RedistribucionViandas extends Colaboracion {

    @Column(name = "fechaDistribucionRealizada")
    private LocalDate fechaDistribucionRealizada;

    @Column(name = "cantidad")
    private Integer cantidad;


    @ManyToOne (cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "heladeraOrigen_id", referencedColumnName = "id")
    private Heladera heladeraOrigen;

    @ManyToOne (cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "heladeraDestino_id", referencedColumnName = "id")
    private Heladera heladeraDestino;

    @Column(name = "motivo")
    private String motivo;
    @Override
    public Integer getCantidad() {
        return cantidad;
    }

    public RedistribucionViandas(LocalDateTime fechaColaboracion, Integer cantidad) {
       this.setFechaColaboracion(fechaColaboracion);
        this.cantidad = cantidad;
        this.setFueExitosa(false);
    }

    @Override
    public Double calcularPuntaje() {
        return cantidad * PuntajeConfig.viandasDisbtribuidasMultiplicador;
    }
}
