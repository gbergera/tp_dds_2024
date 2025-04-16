package ar.edu.utn.frba.dds.server;

import ar.edu.utn.frba.dds.config.ServiceLocator;
import ar.edu.utn.frba.dds.models.entities.puntos.CalculadorDePuntaje;
import ar.edu.utn.frba.dds.models.entities.reportes.GeneradorDeReportes;

import ar.edu.utn.frba.dds.services.brokerHeladera.BrokerHeladera;
import ar.edu.utn.frba.dds.services.sensores.SensorManager;



public class App {
    public static void main(String[] args) {
        Server.init();

        BrokerHeladera.main();

        CalculadorDePuntaje calculadorDePuntaje = ServiceLocator.instanceOf(CalculadorDePuntaje.class);
        calculadorDePuntaje.iniciarCron();

        GeneradorDeReportes generadorDeReportes = ServiceLocator.instanceOf(GeneradorDeReportes.class);
        generadorDeReportes.iniciarCron();

        SensorManager sensorManager = new SensorManager();
        sensorManager.initializeSensors();

    }
}
