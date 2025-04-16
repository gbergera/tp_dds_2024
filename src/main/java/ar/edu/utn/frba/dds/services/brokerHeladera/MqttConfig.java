package ar.edu.utn.frba.dds.services.brokerHeladera;

public class MqttConfig {
    public static final String broker = "tcp://localhost:1883";
    public static final String clientId = "mqtt-explorer-38d4f9e8";
    public static final String clientIdApertura = "mqtt-explorer-38d4f9f6";
    public static final String aperturaTopic = "/heladera/apertura";

    public static final Integer qos = 2;



}
