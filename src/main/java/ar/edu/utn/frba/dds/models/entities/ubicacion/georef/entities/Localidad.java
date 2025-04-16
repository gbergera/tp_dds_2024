package ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.persona.Tecnico;
import ar.edu.utn.frba.dds.models.entities.suscripciones.Suscripcion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.mail.MessagingException;
import javax.persistence.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
@Setter
@Entity
@Table(name = "localidad")
public class Localidad {
    @Id
    @GeneratedValue
    public long id;

    @Column(name = "nombre")
    public String nombre;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "municipio_id")
    public Municipio municipio;


    @JsonIgnore
    @OneToMany(mappedBy = "areaCobertura", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    private List<Tecnico> tecnicos = new ArrayList<Tecnico>();



    public void agregarTecnico(Tecnico tecnico) {
        if (!tecnicos.contains(tecnico)) {
            tecnicos.add(tecnico);
        }
    }

    public Tecnico escogerTecnicoAlAzar(){
        Random random = new Random();
        int randomIndex = random.nextInt(tecnicos.size());

        return (Tecnico) tecnicos.get(randomIndex);
    }
}
