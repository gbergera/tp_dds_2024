package ar.edu.utn.frba.dds.models.entities.suscripciones;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.notificacion.Notificador;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.Mensaje;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnviadorAsincronico {
    private static EnviadorAsincronico instancia = null;
    private Logger logger = Logger.getLogger(EnviadorAsincronico.class.getName());

    // Crear un pool de hilos reutilizable
    private static final ExecutorService executorService = Executors.newFixedThreadPool(3);

    public static EnviadorAsincronico instancia(){
        if(instancia == null){
            instancia = new EnviadorAsincronico();
        }
        return instancia;
    }

    public void serNotificadoPorAsync(Suscripcion suscripcion, Heladera heladera) {
        CompletableFuture.runAsync(() -> {
                    try {
                       logger.info("Enviador En async");
                        suscripcion.serNotificadoPor(heladera);
                    } catch (IOException | MessagingException e) {
                        logger.log(Level.SEVERE, "Error al enviar la notificación: " + e.getMessage(), e);
                    }
                }, executorService)
                .thenRun(() -> logger.info("Notificación enviada correctamente."))
                .exceptionally(ex -> {
                    logger.log(Level.SEVERE, "Error al enviar la notificación: " + ex.getMessage(), ex);
                    return null;
                });
    }

    public void notificar(Mensaje mensaje) {
        CompletableFuture.runAsync(() -> {
                    try {
                        logger.info("Enviador En async");
                        Notificador.instancia().enviar(mensaje);
                    } catch (IOException | MessagingException e) {
                        logger.log(Level.SEVERE, "Error al enviar la notificación: " + e.getMessage(), e);
                    }
                }, executorService)
                .thenRun(() -> logger.info("Notificación enviada correctamente."))
                .exceptionally(ex -> {
                    logger.log(Level.SEVERE, "Error al enviar la notificación: " + ex.getMessage(), ex);
                    return null;
                });
    }

    // Método para cerrar el ExecutorService cuando la aplicación se apaga
    public void shutdown() {
        if (!executorService.isShutdown()) {
            executorService.shutdown();  // Para detener el pool de hilos cuando ya no se necesiten
        }
    }
}
