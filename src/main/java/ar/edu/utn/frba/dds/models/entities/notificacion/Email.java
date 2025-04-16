package ar.edu.utn.frba.dds.models.entities.notificacion;

import ar.edu.utn.frba.dds.models.entities.notificacion.apiGmail.ServicioGmail;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.persona.INotificable;

import javax.mail.MessagingException;
import javax.persistence.*;

import java.io.IOException;

public class Email implements MetodoDeNotificacion{

    @Override
    public void notificar(INotificable notificable, String cuerpoDelMensaje, String asunto) throws MessagingException, IOException {
        ServicioGmail.enviarMensaje(notificable.getCorreo(), cuerpoDelMensaje, asunto);
    }
}
