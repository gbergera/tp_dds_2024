package ar.edu.utn.frba.dds.models.entities.colaboracion.comida;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
@Getter
@Setter
@Entity
@Table(name = "comida")
public class Comida {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "calorias")
    private Integer calorias;

    @Column(name = "peso")
    private Integer peso;

    @JsonIgnore
    @ElementCollection
    @CollectionTable(name = "comida_ingrediente", joinColumns = @JoinColumn(name = "comida_id", referencedColumnName = "id"))
    @Column (name = "ingrediente")
    private List<String> ingredientes;
}
