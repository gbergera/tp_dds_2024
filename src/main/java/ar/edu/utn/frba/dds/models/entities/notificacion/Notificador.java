package ar.edu.utn.frba.dds.models.entities.notificacion;

import ar.edu.utn.frba.dds.models.entities.notificacion.builder.Mensaje;

import javax.mail.MessagingException;
import java.io.IOException;

public class Notificador {
    private static Notificador instancia = null;


    public static Notificador instancia(){
        if(instancia== null){
            instancia = new Notificador();
        }
        return instancia;
    }


    public void enviar(Mensaje mensaje) throws MessagingException, IOException{
        mensaje.getMetodoDeNotificacion().notificar(mensaje.getNotificable(),mensaje.getCuerpoDelMensaje(), mensaje.getAsunto());
    }
}
