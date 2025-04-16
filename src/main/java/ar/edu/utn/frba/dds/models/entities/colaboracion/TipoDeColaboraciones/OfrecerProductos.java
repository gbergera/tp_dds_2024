package ar.edu.utn.frba.dds.models.entities.colaboracion.TipoDeColaboraciones;

import ar.edu.utn.frba.dds.converters.LocalDateConverter;
import ar.edu.utn.frba.dds.models.entities.colaboracion.Colaboracion;
import ar.edu.utn.frba.dds.models.entities.puntos.Oferta;
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
@DiscriminatorValue("PRODUCTO_OFERTA")
public class OfrecerProductos extends Colaboracion {


    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "colaboracion_ofrecer_producto_id")
    private List<Oferta> ofertasOfrecidas;

    @Column(name = "cantidad")
    private Integer cantidad;

    public OfrecerProductos() {
        this.ofertasOfrecidas = new ArrayList<>();
        this.setFueExitosa(true);
    }


    @Override
    public Integer getCantidad() {
        return cantidad;
    }

    @Override
    public Double calcularPuntaje() {
        return 0.0;
    }

    public void agregarOferta(Oferta oferta) {
        this.ofertasOfrecidas.add(oferta);
        this.cantidad = this.ofertasOfrecidas.size();
    }
}
