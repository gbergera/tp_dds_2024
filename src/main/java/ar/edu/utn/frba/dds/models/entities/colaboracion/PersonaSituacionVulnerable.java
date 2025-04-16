package ar.edu.utn.frba.dds.models.entities.colaboracion;

import ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta.Tarjeta;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.persona.Documento;
import ar.edu.utn.frba.dds.models.entities.ubicacion.DireccionAdaptada;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "persona_situacion_vulnerable")
public class PersonaSituacionVulnerable {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Enumerated(EnumType.STRING)
    @Column(name = "situacion")
    private Situacion situacion;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "direccion_id", referencedColumnName = "id")
    private DireccionAdaptada direccion;

    @Embedded
    private Documento documento;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id")
    private List<Hijo> hijos;

    @ManyToOne
    @JoinColumn(name = "colaborador_responsable_id")
    private Colaborador colaboradorResponsable;

    @OneToMany(mappedBy = "persona", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<Tarjeta> tarjetas;

    public PersonaSituacionVulnerable() {

        this.tarjetas = new ArrayList<>();
        this.hijos = new ArrayList<>();

    }

    public void agregarTarjeta(Tarjeta tarjeta){
        this.tarjetas.add(tarjeta);
    }

    public Integer hijosMenoresACargo(){
        return this.hijos.size();
        }

}
