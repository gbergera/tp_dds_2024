package ar.edu.utn.frba.dds.models.entities.notificacion.apiWpp;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.util.Set;
import java.util.logging.Logger;

public class ServicioWhatsapp {
    private static ServicioWhatsapp instancia = null;
    private static String mensaje;

    public static ServicioWhatsapp instancia(){
        if(instancia== null){
            instancia = new ServicioWhatsapp();
        }
        return instancia;
    }

    public static void enviarMensaje(String telefono, String mensajeANotificar, String asunto){
        Twilio.init(WppConfig.ACCOUNT_SID, WppConfig.AUTH_TOKEN);
        Logger logger = Logger.getLogger(ServicioWhatsapp.class.getName());

        mensaje = mensajeANotificar;
        logger.info("Se va a mandar el mensaje: " + mensaje);
        Message message = Message.creator(
                        new PhoneNumber("whatsapp:" + "+549"+telefono),
                        new PhoneNumber("whatsapp:+14155238886"),
                        "Asunto: " + asunto + "\n" + "\n" + mensaje)
                .create();

       logger.info(message.getSid());

    }
}
