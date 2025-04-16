package ar.edu.utn.frba.dds.models.entities.notificacion.apiTelegram;


import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;


public class ServicioTelegram implements LongPollingSingleThreadUpdateConsumer {

    private static ServicioTelegram instancia = null;

    public static ServicioTelegram instancia(){
        if(instancia== null){
            instancia = new ServicioTelegram();
        }
        return instancia;
    }

    public static void enviarMensaje(String chatId, String mensajeANotificar, String asunto){
        TelegramClient telegramClient = new OkHttpTelegramClient(TelegramConfig.botToken);
        SendMessage sendMessage = new SendMessage(chatId, "Asunto: " + asunto + "\n" + "\n" + mensajeANotificar);
        try {
            // Execute it
            telegramClient.execute(sendMessage);


        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            if (messageText.equals("/suscribirse")) {
                String chatId = update.getMessage().getChatId().toString();
                String usuarioTelegram = update.getMessage().getFrom().getUserName();

                // List<Colaborador> todoloscolaboradores.filter(FiltrarPorNombredeUsuario);
                // si encuentra uno:
                // Colaborador colaboradorEncontrado.setChatID(chatId)
                // si no encuentra uno:
                // lanzar mensaje de error: "No hay colaboradores con tu nombre de usuario de telegram

            }
        }


        }
}


