package ar.edu.utn.frba.dds.services.brokerHeladera;

public class MqttMessageConEstado {
    private String idTarjeta;
    private String idHeladera;
    private String estado;

    public MqttMessageConEstado(String idTarjeta, String idHeladera, String estado) {
        this.idTarjeta = idTarjeta;
        this.idHeladera = idHeladera;
        this.estado = estado;
    }
}
