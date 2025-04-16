package ar.edu.utn.frba.dds.services.brokerHeladera;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.logging.Logger;

public class MqttSender {
    private String broker;
    private String clientId;
    private MqttClient client;
    private Logger logger = Logger.getLogger(MqttSender.class.getName());

    public MqttSender(String broker, String clientId) {
        this.broker = broker;
        this.clientId = clientId;
        try {
            this.client = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            client.connect(connOpts);
        } catch (MqttException me) {
            logger.severe("Error al conectar al broker: " + me.getMessage());
        }
    }

    public void enviarMensaje(String topic, String content, int qos) {
        try {
            client.publish(topic, content.getBytes(), qos, false);
            logger.info("Mensaje enviado a topic: " + topic);
        } catch (MqttException me) {
           logger.severe("Error al enviar mensaje: " + me.getMessage());
        }
    }

    public void cerrarConexion() {
        try {
            if (client != null) {
                client.disconnect();
                client.close();
            }
        } catch (MqttException me) {
           logger.severe("Error al cerrar la conexión: " + me.getMessage());
        }
    }
}
