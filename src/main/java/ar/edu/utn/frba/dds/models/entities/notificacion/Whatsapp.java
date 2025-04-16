package ar.edu.utn.frba.dds.models.entities.notificacion;

import ar.edu.utn.frba.dds.models.entities.notificacion.apiWpp.ServicioWhatsapp;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.persona.INotificable;

import javax.mail.MessagingException;
import javax.persistence.*;

import java.io.IOException;
import java.time.Period;

public class Whatsapp implements MetodoDeNotificacion{

    @Override
    public void notificar(INotificable notificable, String cuerpoDelMensaje, String asunto) throws MessagingException, IOException {
        ServicioWhatsapp.enviarMensaje(notificable.getTelefono(), cuerpoDelMensaje, asunto);
    }
}
