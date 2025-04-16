package ar.edu.utn.frba.dds.models.entities.colaboracion.comida;

import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "vianda")
public class Vianda {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne (cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "comida_id")
    private Comida comida;

    @JsonIgnore
    @Column(name = "fecha_caducidad")
    private LocalDate fechaCaducidad;

    @JsonIgnore
    @Column(name = "fecha_donacion")
    private LocalDate fechaDonacion;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "colaborador_id")
    private Colaborador colaborador;

    @JsonIgnore
    @ManyToOne (cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "heladera_id")
    private Heladera heladera;

    @JsonIgnore
    @Column(name = "entregada")
    private Boolean entregada;

    public void removerDeHeladera(){
        this.heladera.retirarVianda(this);
    }
}
