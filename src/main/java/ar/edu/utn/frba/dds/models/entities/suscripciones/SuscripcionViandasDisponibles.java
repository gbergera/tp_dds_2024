package ar.edu.utn.frba.dds.models.entities.suscripciones;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.notificacion.Email;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.Mensaje;
import ar.edu.utn.frba.dds.models.entities.notificacion.Notificador;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.NotificacionBuilder;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.NotificacionSuscripcionViandasDisponibles;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import lombok.Getter;
import lombok.Setter;

import javax.mail.MessagingException;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.io.IOException;
@Setter
@Getter

@Entity
@DiscriminatorValue("SUB_VIANDAS_DISPONIBLES")
public class SuscripcionViandasDisponibles extends Suscripcion {
    @Column(name = "cantidadMinima")
    private Integer cantidadMinima;

    @Override
    public void verificarCondicion(Heladera heladera) throws MessagingException, IOException {
        int viandasRestantes = heladera.getViandas().size();
        if (viandasRestantes <= this.cantidadMinima) {
            EnviadorAsincronico.instancia().serNotificadoPorAsync(this, heladera);
        }
    }

    @Override
    public void serNotificadoPor(Heladera heladera) throws IOException, MessagingException {
        this.notificacionBuilder = new NotificacionSuscripcionViandasDisponibles();
        String cuerpoMensaje;
        if(this.colaborador.getMetodoDeNotificacion().getClass().equals(Email.class)){
            cuerpoMensaje = "<html>"
                    + "<body style='font-family: Arial, sans-serif; color: #333;'>"
                    + "    <p style='font-size: 16px;'>La heladera ubicada en la dirección: <strong>"
                    + heladera.direccionActual().direccionCompleta() + "</strong> "
                    + "tiene <strong>" + heladera.getViandasColocadas() + "</strong> viandas disponibles.</p>"
                    + "</body>"
                    + "</html>";
        }else{
            cuerpoMensaje = "\uD83D\uDCCD La heladera ubicada en la dirección: " + heladera.direccionActual().direccionCompleta() + " tiene " + heladera.getViandasColocadas() + " viandas disponibles.";
        }
        Mensaje mensaje = notificacionBuilder.agregarReceptor(this.colaborador)
                .agregarMensaje(cuerpoMensaje)
                .agregarMetodoNotificacion(colaborador.getMetodoDeNotificacion())
                .construir();


        Notificador.instancia().enviar(mensaje);
    }

}
