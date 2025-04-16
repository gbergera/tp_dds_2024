package ar.edu.utn.frba.dds.models.entities.colaboracion.TipoDeColaboraciones;

import ar.edu.utn.frba.dds.converters.LocalDateConverter;
import ar.edu.utn.frba.dds.models.entities.colaboracion.Colaboracion;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Vianda;
import ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta.SolicitudApertura;
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
@DiscriminatorValue("DONACION_VIANDAS")
@NoArgsConstructor
public class DonacionViandas extends Colaboracion {

    @Column(name = "fechaCaducidad")
    private LocalDate fechaCaducidad;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.LAZY)
    @JoinColumn(name = "donacion_viandas_id")
    private List<Vianda> viandas = new ArrayList<>();

    @Column(name = "cantidad")
    private Integer cantidad;

    public Integer getCantidad() {
        return cantidad;
    }

    public DonacionViandas(LocalDateTime fechaColaboracion, Integer cantidad) {
        this.setFechaColaboracion(fechaColaboracion);
        this.cantidad = cantidad;
        this.viandas = new ArrayList<>();
        this.setFueExitosa(false);
    }


    @Override
    public Double calcularPuntaje() {
       return cantidad * PuntajeConfig.viandasDonadasMultiplicador;
    }

    public void agregarVianda(Vianda vianda1) {
        viandas.add(vianda1);
    }
    
}
