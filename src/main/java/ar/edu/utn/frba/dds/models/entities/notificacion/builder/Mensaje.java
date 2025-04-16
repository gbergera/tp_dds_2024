package ar.edu.utn.frba.dds.models.entities.notificacion.builder;

import ar.edu.utn.frba.dds.models.entities.notificacion.MetodoDeNotificacion;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.persona.INotificable;
import ar.edu.utn.frba.dds.models.entities.persona.Tecnico;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter

public class Mensaje {

    private Long id;

    private String asunto;

    private String cuerpoDelMensaje;

    //VER COMO PONEMOS EL ID DEL RECEPTOR
    private INotificable notificable;

    private MetodoDeNotificacion metodoDeNotificacion;

    public Mensaje(String asunto) {
        this.asunto = asunto;
    }
    public Mensaje() {}


}
