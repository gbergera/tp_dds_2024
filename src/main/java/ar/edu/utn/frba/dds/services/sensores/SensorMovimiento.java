package ar.edu.utn.frba.dds.services.sensores;

import ar.edu.utn.frba.dds.services.brokerHeladera.MqttConfig;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.logging.Logger;

@Setter
@Getter
public class SensorMovimiento implements MqttCallback {
    private ReceptorSensorMovimiento receptorSensorMovimiento;
    private Logger logger = Logger.getLogger(SensorMovimiento.class.getName());


    public void recibirDato(String movimiento) {
        try {
            receptorSensorMovimiento.evaluar(movimiento);
        } catch (IOException | MessagingException e) {
            logger.severe("Error processing data: " + e.getMessage());
        }
    }

    @Override
    public void connectionLost(Throwable throwable) {
        logger.info("Se perdió la conexión en Movimiento: " + throwable.getMessage());

        try {
            MqttClient cliente = new MqttClient(MqttConfig.broker, "reconectarMovimiento" + receptorSensorMovimiento.getHeladera().getId(), new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setAutomaticReconnect(true);
            cliente.setCallback(this);
            cliente.connect(connOpts);
            if (cliente.isConnected()) {
                logger.info("Re-conectado el sensor de Movimiento");
                cliente.subscribe(receptorSensorMovimiento.getHeladera().getTopicMQTT() + "movimiento", MqttConfig.qos);
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
    public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
        logger.info("Message arrived in movimiento on topic: " + topic);
        logger.info("Received message: " + mqttMessage.toString());
        this.recibirDato(mqttMessage.toString());
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        // Método no utilizado en este contexto
    }
}
