package ar.edu.utn.frba.dds.models.entities.colaboracion.TipoDeColaboraciones;

import ar.edu.utn.frba.dds.converters.LocalDateConverter;
import ar.edu.utn.frba.dds.models.entities.colaboracion.Colaboracion;
import ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta.Tarjeta;
import ar.edu.utn.frba.dds.models.entities.puntos.config.PuntajeConfig;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@DiscriminatorValue("ENTREGA_TARJETAS")
@NoArgsConstructor
public class EntregaTarjetas extends Colaboracion {

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "colaboracion_entrega_tarjetas_id")
    private List<Tarjeta> tarjetas = new ArrayList<>();

    @Column(name = "cantidad")
    private Integer cantidad;

    public EntregaTarjetas(LocalDateTime fechaColaboracion, Integer cantidad) {
        this.setFechaColaboracion(fechaColaboracion);
        this.cantidad = cantidad;
        this.tarjetas = new ArrayList<>();
        this.setFueExitosa(true);
    }

    public void AgregarTarjeta(Tarjeta tarjeta) {
        tarjetas.add(tarjeta);
    }

    @Override
    public Double calcularPuntaje() {
        if (cantidad == null) {
            cantidad = 1;
        }
        return cantidad * PuntajeConfig.tarjetasRepartidasMultiplicador;
    }

    @Override
    public Integer getCantidad() {
        return cantidad;
    }
}
