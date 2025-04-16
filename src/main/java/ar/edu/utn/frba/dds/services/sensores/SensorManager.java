package ar.edu.utn.frba.dds.services.sensores;

import ar.edu.utn.frba.dds.config.ServiceLocator;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.incidente.builder.ReporteDeIncidentesBuilder;
import ar.edu.utn.frba.dds.models.repositories.HeladeraRepository;
import ar.edu.utn.frba.dds.models.repositories.RegistroDeFallaRepository;
import ar.edu.utn.frba.dds.models.repositories.SuscripcionRepository;
import ar.edu.utn.frba.dds.models.repositories.TipoIncidenteRepository;
import ar.edu.utn.frba.dds.services.brokerHeladera.MqttConfig;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class SensorManager {
    private Logger logger = Logger.getLogger(SensorManager.class.getName());
    public void initializeSensors() {
        HeladeraRepository heladeraRepository = ServiceLocator.instanceOf(HeladeraRepository.class);
        List<Heladera> heladeras = heladeraRepository.buscarTodos();

        for (Heladera heladera : heladeras) {
            try {
                conectarSensorTemperatura(heladera);
                conectarSensorMovimiento(heladera);
            } catch (MqttException e) {
                logger.severe("Error conectando sensor: " + heladera.getNombre() + " " + e.getMessage());

            }
        }
    }
    private void conectarSensorTemperatura(Heladera heladera) throws MqttException {
        MqttClient cliente = new MqttClient(MqttConfig.broker, "heladera" + heladera.getNombre() + "temperatura", new MemoryPersistence());
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setAutomaticReconnect(true);
        SensorTemperatura sensorTemperatura = new SensorTemperatura();
        ReceptorSensorTemperatura receptorTemperatura = new ReceptorSensorTemperatura(ServiceLocator.instanceOf(ReporteDeIncidentesBuilder.class), ServiceLocator.instanceOf(RegistroDeFallaRepository.class), ServiceLocator.instanceOf(HeladeraRepository.class), ServiceLocator.instanceOf(TipoIncidenteRepository.class), ServiceLocator.instanceOf(SuscripcionRepository.class));
        receptorTemperatura.setHeladera(heladera);
        sensorTemperatura.setReceptorSensorTemperatura(receptorTemperatura);
        cliente.setCallback(sensorTemperatura);
        cliente.connect(connOpts);

        FallaConexion cron = new FallaConexion(receptorTemperatura, ServiceLocator.instanceOf(ReporteDeIncidentesBuilder.class), ServiceLocator.instanceOf(TipoIncidenteRepository.class), ServiceLocator.instanceOf(RegistroDeFallaRepository.class),ServiceLocator.instanceOf(HeladeraRepository.class));

        cron.iniciarCron();


        if (cliente.isConnected()) {
           logger.info("Sensor de Temperatura conectado para heladera: " + heladera.getNombre());
            cliente.subscribe(heladera.getTopicMQTT() + "temperatura", MqttConfig.qos);
        }
    }

    private void conectarSensorMovimiento(Heladera heladera) throws MqttException {
        MqttClient cliente = new MqttClient(MqttConfig.broker, "heladera" + heladera.getNombre() + "movimiento", new MemoryPersistence());
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setAutomaticReconnect(true);
        SensorMovimiento sensorMovimiento = new SensorMovimiento();
        ReceptorSensorMovimiento receptorMovimiento = new ReceptorSensorMovimiento(ServiceLocator.instanceOf(ReporteDeIncidentesBuilder.class), ServiceLocator.instanceOf(RegistroDeFallaRepository.class), ServiceLocator.instanceOf(HeladeraRepository.class), ServiceLocator.instanceOf(TipoIncidenteRepository.class));
        receptorMovimiento.setHeladera(heladera);
        sensorMovimiento.setReceptorSensorMovimiento(receptorMovimiento);
        cliente.setCallback(sensorMovimiento);
        cliente.connect(connOpts);
        if (cliente.isConnected()) {
           logger.info("Sensor de Movimiento conectado para heladera: " + heladera.getNombre());
            cliente.subscribe(heladera.getTopicMQTT() + "movimiento", MqttConfig.qos);
        }
    }
}
