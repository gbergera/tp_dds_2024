package ar.edu.utn.frba.dds.models.entities.suscripciones;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.notificacion.Email;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.Mensaje;
import ar.edu.utn.frba.dds.models.entities.notificacion.Notificador;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.NotificacionBuilder;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.NotificacionSuscripcionDesperfectoHeladera;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import lombok.Getter;
import lombok.Setter;

import javax.mail.MessagingException;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
@Setter
@Getter
@Entity
@DiscriminatorValue("SUB_HELADERA_DESPERFECTO")
public class SuscripcionHeladeraDesperfecto extends Suscripcion {


    @Override
    public void verificarCondicion(Heladera heladera) throws MessagingException, IOException {
        if (heladera.getActiva()) {
            EnviadorAsincronico.instancia().serNotificadoPorAsync(this, heladera);
        }
    }



    @Override
    public void serNotificadoPor(Heladera heladera) throws IOException, MessagingException {
        this.notificacionBuilder = new NotificacionSuscripcionDesperfectoHeladera();

        // Obtener la lista de ubicaciones de heladeras cercanas
        List<String> listaDeUbicaciones = heladera.getHeladerasCercanas().stream().map(h -> {
            try {
                return h.direccionActual().direccionCompleta();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        if(listaDeUbicaciones.isEmpty()){
            String cuerpoMensaje;
            if(colaborador.getMetodoDeNotificacion().getClass().equals(Email.class)){
                cuerpoMensaje = "<html>"
                        + "<body style='font-family: Arial, sans-serif; color: #333;'>"
                        + "    <h2 style='color: #d9534f;'>Notificación de Desperfecto en Heladera</h2>"
                        + "    <p>Estimado/a,</p>"
                        + "    <p>La heladera ubicada en la dirección: <strong>" + heladera.direccionActual().direccionCompleta() + "</strong> ha sufrido un desperfecto.</p>"
                        + "    <p>Te recomendamos verificar el mapa para llevar las viandas a la heladera más cercana.</p>"
                        + "    <p style='margin-top: 20px;'>¡Gracias por tu atención!</p>"
                        + "    <p>Atentamente,</p>"
                        + "    <p><em>El equipo de soporte</em></p>"
                        + "</body>"
                        + "</html>";
            }else{
                cuerpoMensaje = "\uD83D\uDD34 Notificación de Desperfecto en Heladera \uD83D\uDD34\n" +
                        "\n" +
                        "Estimado/a,\n" +
                        "\n" +
                        "La heladera ubicada en la dirección: " + heladera.direccionActual().direccionCompleta() + " ha sufrido un desperfecto.\n" +
                        "\n" +
                        "Te recomendamos verificar el mapa para llevar las viandas a la heladera más cercana.\n" +
                        "\n" +
                        "¡Gracias por tu atención!\n" +
                        "\n" +
                        "Atentamente, El equipo de soporte";
            }
            Mensaje mensaje = notificacionBuilder.agregarReceptor(colaborador)
                    .agregarMensaje(cuerpoMensaje)
                    .agregarMetodoNotificacion(colaborador.getMetodoDeNotificacion())
                .construir();
            // Enviar la notificación
            Notificador.instancia().enviar(mensaje);
        }
        else{
            String cuerpoMensaje;
            String ubicacionesConComas = String.join(", ", listaDeUbicaciones);
            // Construir el mensaje de notificación
            if(colaborador.getMetodoDeNotificacion().getClass().equals(Email.class)){
                cuerpoMensaje = "<html>"
                        + "<body style='font-family: Arial, sans-serif; color: #333;'>"
                        + "    <h2 style='color: #d9534f;'>Notificación de Desperfecto en Heladera</h2>"
                        + "    <p>Estimado/a,</p>"
                        + "    <p>La heladera ubicada en la dirección: <strong>" + heladera.direccionActual().direccionCompleta() + "</strong> ha sufrido un desperfecto.</p>"
                        + "    <p>Por favor, se recomienda llevar las viandas a las siguientes heladeras cercanas:</p>"
                        + "    <ul style='margin-left: 20px;'>"
                        + "        <li>" + ubicacionesConComas.replace(",", "</li><li>") + "</li>"
                        + "    </ul>"
                        + "    <p style='margin-top: 20px;'>Gracias por su atención y comprensión.</p>"
                        + "    <p>Atentamente,</p>"
                        + "    <p><em>Equipo de Soporte</em></p>"
                        + "</body>"
                        + "</html>";
            }else{
                cuerpoMensaje = "\uD83D\uDD34 Notificación de Desperfecto en Heladera \uD83D\uDD34\n" +
                        "\n" +
                        "Estimado/a,\n" +
                        "\n" +
                        "La heladera ubicada en la dirección: " + heladera.direccionActual().direccionCompleta() + " ha sufrido un desperfecto.\n" +
                        "\n" +
                        "Por favor, se recomienda llevar las viandas a las siguientes heladeras cercanas:\n" +
                        "\n" +
                        ubicacionesConComas.replace(",", "\n- ") + "\n" +
                        "Gracias por su atención y comprensión.\n" +
                        "\n" +
                        "Atentamente, Equipo de Soporte";
            }
            Mensaje mensaje = notificacionBuilder.agregarReceptor(colaborador)
                    .agregarMensaje(cuerpoMensaje)
                    .agregarMetodoNotificacion(colaborador.getMetodoDeNotificacion())
                .construir();
            // Enviar la notificación
            Notificador.instancia().enviar(mensaje);
        }


    }

}
