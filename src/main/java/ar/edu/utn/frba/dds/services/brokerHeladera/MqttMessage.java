package ar.edu.utn.frba.dds.services.brokerHeladera;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MqttMessage {
    private String idTarjeta;
    private String idHeladera;

    public MqttMessage(String idTarjeta, String idHeladera) {
        this.idTarjeta = idTarjeta;
        this.idHeladera = idHeladera;
    }
}
