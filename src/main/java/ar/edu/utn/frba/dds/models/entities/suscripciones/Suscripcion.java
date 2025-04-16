package ar.edu.utn.frba.dds.models.entities.suscripciones;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;

import javax.mail.MessagingException;
import java.io.IOException;
import javax.persistence.*;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.NotificacionBuilder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_suscripcion", discriminatorType = DiscriminatorType.STRING)
@Setter
@Getter
public abstract class Suscripcion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @ManyToOne
    @JoinColumn(name = "colaborador_id")
    public Colaborador colaborador;

    @Transient
    private String nombreHeladera;

    @Transient
    public NotificacionBuilder notificacionBuilder;
    public abstract void verificarCondicion(Heladera heladera) throws MessagingException, IOException;
    public abstract void serNotificadoPor(Heladera heladera) throws IOException, MessagingException;
}
