package ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta;

import ar.edu.utn.frba.dds.services.brokerHeladera.MqttConfig;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.services.brokerHeladera.MqttMessage;
import ar.edu.utn.frba.dds.models.entities.excepciones.TiempoTerminadoException;
import ar.edu.utn.frba.dds.services.brokerHeladera.MqttSender;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.logging.Logger;

@Setter
@Getter
@Entity
@Table(name = "solicitud_apertura")
public class SolicitudApertura{
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;

    @ManyToOne
    @JoinColumn(name = "heladera_id")
    private Heladera heladeraAAbrir;

    @Column(name = "exitosa")
    private Boolean fueExitosa = false;



    @ManyToOne
    @JoinColumn(name = "tarjeta_id")
    private Tarjeta tarjeta; // Este atributo debe existir
    public SolicitudApertura() {
    }


    public Double tiempoRestante(){
        LocalDateTime fechaActual = LocalDateTime.now();
        Duration duration = Duration.between(this.fechaSolicitud, fechaActual);

        return (double) ((TarjetaConfig.tiempoMaximmo * 3600 - duration.toSeconds()) / 60);

    }
      public void avisarSolicitud(String idTarjeta, String heladeraTopic){
        MqttSender mqttSender = new MqttSender(MqttConfig.broker, MqttConfig.clientId);
        MqttMessage mensaje = new MqttMessage(idTarjeta, heladeraAAbrir.getId().toString());
        Logger.getLogger("SolicitudApertura").info("Por enviar mensaje: " + idTarjeta + ", " +  heladeraAAbrir.getId().toString());
        Gson gson = new Gson();
        mqttSender.enviarMensaje(heladeraTopic, gson.toJson(mensaje), MqttConfig.qos);

    }

}
