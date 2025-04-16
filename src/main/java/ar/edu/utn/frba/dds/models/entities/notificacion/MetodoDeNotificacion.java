package ar.edu.utn.frba.dds.models.entities.notificacion;

import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.persona.INotificable;

import javax.mail.MessagingException;
import java.io.IOException;


public interface MetodoDeNotificacion {

    void notificar(INotificable notificable, String cuerpoDelMensaje, String asunto) throws MessagingException, IOException;
}
