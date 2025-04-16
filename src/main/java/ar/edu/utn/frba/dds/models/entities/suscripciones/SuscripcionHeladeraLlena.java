package ar.edu.utn.frba.dds.models.entities.suscripciones;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.notificacion.Email;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.Mensaje;
import ar.edu.utn.frba.dds.models.entities.notificacion.Notificador;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.NotificacionBuilder;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.NotificacionSuscripcionHeladeraLlena;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import lombok.Getter;
import lombok.Setter;

import javax.mail.MessagingException;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Setter
@Entity
@Getter
@DiscriminatorValue("SUB_HELADERA_LLENA")
public class SuscripcionHeladeraLlena extends Suscripcion {

    @Column(name = "cantidadMaxima")
    private Integer cantidadMaxima;

    @Override
    public void verificarCondicion(Heladera heladera) throws MessagingException, IOException {
        int viandasRestantes = heladera.getCapacidad() - heladera.getViandas().size();
        if (viandasRestantes <= cantidadMaxima) {
            EnviadorAsincronico.instancia().serNotificadoPorAsync(this, heladera);
        }
    }

    @Override
    public void serNotificadoPor(Heladera heladera) throws IOException, MessagingException {
        this.notificacionBuilder = new NotificacionSuscripcionHeladeraLlena();
        String cuerpoMensaje;
        if(colaborador.getMetodoDeNotificacion().getClass().equals(Email.class)){
            cuerpoMensaje = "<html>"
                    + "<body style='font-family: Arial, sans-serif; color: #333;'>"
                    + "    <p style='font-size: 16px;'>La heladera ubicada en la dirección: <strong>"
                    + heladera.direccionActual().direccionCompleta() + "</strong> "
                    + "ha superado el límite máximo establecido de viandas. "
                    + "Restan <strong>" + (heladera.getCapacidad() - heladera.getViandas().size()) + "</strong> espacios disponibles.</p>"
                    + "</body>"
                    + "</html>";
        }else{
            cuerpoMensaje = "\uD83D\uDCCD La heladera ubicada en la dirección: " +heladera.direccionActual().direccionCompleta() + " ha superado el límite máximo establecido de viandas. Restan " + (heladera.getCapacidad() - heladera.getViandas().size()) + " espacios disponibles.";
        }
        Mensaje mensaje = notificacionBuilder.agregarReceptor(colaborador)
                .agregarMensaje(cuerpoMensaje)
                .agregarMetodoNotificacion(colaborador.getMetodoDeNotificacion())
                .construir();

        Notificador.instancia().enviar(mensaje);
    }

}
