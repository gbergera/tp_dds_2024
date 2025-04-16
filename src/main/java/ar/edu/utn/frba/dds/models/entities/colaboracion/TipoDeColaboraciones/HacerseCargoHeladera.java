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
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Entity
@DiscriminatorValue("HELADERA_A_CARGO")
@NoArgsConstructor
public class HacerseCargoHeladera extends Colaboracion {

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "colaboracion_heladera_a_cargo_id")
    private List<Heladera> heladerasACargo = new ArrayList<>();

    @Column(name = "cantidad")
    private Integer cantidad;

    @Override
    public Integer getCantidad() {
        return cantidad;
    }

    @Override
    public Double calcularPuntaje() {
        return this.periodoHeladeraActiva() * PuntajeConfig.cantidadHeladerasActivasMultiplicador;
    }

    public Integer periodoHeladeraActiva(){
        Integer diasHeladerasActivas = 0;
        for(Heladera h: heladerasACargo){
            diasHeladerasActivas += h.diasActiva();
        }
        return (int) (diasHeladerasActivas /30);
    }

    public Integer cantidadHeladerasActivas(){
        Integer cantHeladerasACargoActivas = 0;

        for(Heladera h: heladerasACargo){
            if(h.getActiva()){
                cantHeladerasACargoActivas ++;
            }
        }
        return cantHeladerasACargoActivas;
    }

    public void agregarHeladera(Heladera heladera) {
        this.heladerasACargo.add(heladera);
        this.cantidad = this.heladerasACargo.size();
        this.setFueExitosa(true);
    }

    public void sacarHeladera(Heladera heladera) {
        this.heladerasACargo.remove(heladera);
        this.cantidad = this.heladerasACargo.size();
    }

}
