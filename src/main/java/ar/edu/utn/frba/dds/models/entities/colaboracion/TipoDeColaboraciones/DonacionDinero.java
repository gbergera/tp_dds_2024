package ar.edu.utn.frba.dds.models.entities.colaboracion.TipoDeColaboraciones;

import ar.edu.utn.frba.dds.converters.LocalDateConverter;
import ar.edu.utn.frba.dds.models.entities.colaboracion.Colaboracion;
import ar.edu.utn.frba.dds.models.entities.colaboracion.Frecuencia;
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
@DiscriminatorValue("DONACION_DINERO")
@NoArgsConstructor
public class DonacionDinero extends Colaboracion {

    @Column(name = "cantidad")
    private Integer cantidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "ferecuencia")
    private Frecuencia frecuencia;

    public DonacionDinero(Integer cantidad, LocalDateTime fechaColaboracion) {
        this.setFechaColaboracion(fechaColaboracion);
        this.cantidad = cantidad;
        this.setFueExitosa(true);
    }

    @Override
    public Double calcularPuntaje() {
        return cantidad * PuntajeConfig.pesosDonadosMultiplicador;
    }

    @Override
    public Integer getCantidad() {
        return cantidad;
    }
}
