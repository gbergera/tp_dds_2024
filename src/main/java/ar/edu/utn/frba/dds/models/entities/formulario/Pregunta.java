package ar.edu.utn.frba.dds.models.entities.formulario;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "pregunta")
public class Pregunta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "detalle")
    private String detalle;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_de_pregunta")
    private TipoDePregunta tipoDePregunta;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "pregunta_id")
    private List<Opcion> opciones = new ArrayList<>();

    public void agregarOpcion(Opcion opcion){
        this.opciones.add(opcion);
    }
}
