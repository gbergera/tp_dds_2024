package ar.edu.utn.frba.dds.models.entities.formulario;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "respuesta")
public class Respuesta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "detalle")
    private String detalle;

    @ManyToOne
    @JoinColumn(name = "pregunta_id", referencedColumnName = "id")
    private Pregunta pregunta;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "opcion_respuesta",
            joinColumns = @JoinColumn(name = "respuesta_id"),
            inverseJoinColumns = @JoinColumn(name = "opcion_id"))
    private List<Opcion> opcionesMarcadas;

    public void agregarOpcion(Opcion opcion){
        this.opcionesMarcadas.add(opcion);
    }
}
