package ar.edu.utn.frba.dds.models.entities.notificacion;

import ar.edu.utn.frba.dds.models.entities.notificacion.apiTelegram.ServicioTelegram;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.persona.INotificable;

import javax.mail.MessagingException;
import javax.persistence.*;

import java.io.IOException;

public class Telegram implements MetodoDeNotificacion{

    @Override
    public void notificar(INotificable notificable, String cuerpoDelMensaje, String asunto) throws MessagingException, IOException {
        ServicioTelegram.enviarMensaje(notificable.getTelegramChatId(), cuerpoDelMensaje, asunto);
    }
}
