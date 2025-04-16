package ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Vianda;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "uso_tarjeta")
@NoArgsConstructor
public class UsoTarjeta {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "fecha_de_uso")
    private LocalDate fechaDeUso;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "vianda_id")
    private Vianda vianda;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "heladera_id")
    private Heladera heladeraUsada;

    public UsoTarjeta(LocalDate fechaDeUso, Vianda vianda, Heladera heladeraUsada) {
        this.fechaDeUso = fechaDeUso;
        this.vianda = vianda;
        this.heladeraUsada = heladeraUsada;
    }
}
