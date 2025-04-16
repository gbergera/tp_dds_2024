package ar.edu.utn.frba.dds.services.brokerHeladera;


import ar.edu.utn.frba.dds.config.ServiceLocator;
import ar.edu.utn.frba.dds.services.ServicioApertura;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.logging.Logger;

public class BrokerHeladera {

    static ServicioApertura servicioApertura = ServiceLocator.instanceOf(ServicioApertura.class);

    public static void main() {
        MemoryPersistence persistence = new MemoryPersistence();
        Logger logger = Logger.getLogger(BrokerHeladera.class.getName());

        try {
            MqttClient clienteApertura = new MqttClient(MqttConfig.broker, MqttConfig.clientIdApertura, persistence);
            MqttConnectOptions connOptsApertura = new MqttConnectOptions();
            connOptsApertura.setCleanSession(true);
            connOptsApertura.setKeepAliveInterval(60);
            connOptsApertura.setAutomaticReconnect(true);

            clienteApertura.setCallback(servicioApertura);


            clienteApertura.connect(connOptsApertura);

            if (clienteApertura.isConnected()) {
                logger.info("CONECTADOS");
                clienteApertura.subscribe(MqttConfig.aperturaTopic, MqttConfig.qos);
            }


        } catch (MqttException e) {
            logger.severe("Error: " + e.getMessage());
        }
    }
}