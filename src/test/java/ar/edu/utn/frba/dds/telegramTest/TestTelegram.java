package ar.edu.utn.frba.dds.telegramTest;

import ar.edu.utn.frba.dds.models.entities.notificacion.apiGmail.ServicioGmail;
import ar.edu.utn.frba.dds.models.entities.notificacion.apiTelegram.ServicioTelegram;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;

import javax.mail.MessagingException;
import java.io.IOException;

public class TestTelegram {

    @Test
    public void envioMail() throws MessagingException, IOException {
       // ServicioTelegram.enviarMensaje("7165155164", "Hola soy un Bot", "Mensaje de prueba");

    }
}
