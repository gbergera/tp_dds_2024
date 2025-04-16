package ar.edu.utn.frba.dds.services.sensores;

import ar.edu.utn.frba.dds.services.brokerHeladera.MqttConfig;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.logging.Logger;

@Getter
@Setter
public class SensorTemperatura  implements MqttCallback {
    private ReceptorSensorTemperatura receptorSensorTemperatura;
    private String ultimaTemperaturaRegistrada;
    private Logger logger = Logger.getLogger(SensorTemperatura.class.getName());

    public void recibirDato(String temperaturaActual) throws IOException, MessagingException {
        this.ultimaTemperaturaRegistrada = temperaturaActual;
        receptorSensorTemperatura.evaluar(temperaturaActual);
    }

    @Override
    public void connectionLost(Throwable throwable) {
        logger.severe("Se perdió la conexión en Temperatura: " + throwable.getMessage());
        try {
            MqttClient cliente = new MqttClient(MqttConfig.broker, "reconectarTemperatura" + receptorSensorTemperatura.getHeladera().getId(), new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setAutomaticReconnect(true);
            cliente.setCallback(this);
            cliente.connect(connOpts);
            if (cliente.isConnected()) {
                logger.info("Re-conectado el sensor de Temperatura a topic: " + receptorSensorTemperatura.getHeladera().getTopicMQTT() + "temperatura");
                cliente.subscribe(receptorSensorTemperatura.getHeladera().getTopicMQTT() + "temperatura", MqttConfig.qos);
            }
        } catch (MqttException e) {
            logger.severe("Error reconectando: " + e.getMessage());
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        logger.info("Message arrived in temperatura on topic: " + s);
        logger.info("Received message: " + mqttMessage.toString());
        this.recibirDato(mqttMessage.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
