package ar.edu.utn.frba.dds.services;

import ar.edu.utn.frba.dds.models.entities.colaboracion.Colaboracion;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta.Apertura;
import ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta.SolicitudApertura;
import ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta.Tarjeta;
import ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta.UsoTarjeta;
import ar.edu.utn.frba.dds.models.repositories.HeladeraRepository;
import ar.edu.utn.frba.dds.models.repositories.SolicitudAperturaRepository;
import ar.edu.utn.frba.dds.models.repositories.TarjetaRepository;
import ar.edu.utn.frba.dds.services.brokerHeladera.MqttConfig;
import ar.edu.utn.frba.dds.services.brokerHeladera.MqttMessageConEstado;
import ar.edu.utn.frba.dds.services.brokerHeladera.MqttSender;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityTransaction;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ServicioApertura implements MqttCallback, WithSimplePersistenceUnit {
    private static final Logger logger = LoggerFactory.getLogger(ServicioApertura.class);

    private SolicitudAperturaRepository repoDeSolicitudes;
    private HeladeraRepository repoDeHeladeras;
    private TarjetaRepository repoDeTarjetas;

    public ServicioApertura(SolicitudAperturaRepository repoDeSolicitudes, HeladeraRepository repoDeHeladeras, TarjetaRepository repoDeTarjetas) {
        this.repoDeSolicitudes = repoDeSolicitudes;
        this.repoDeHeladeras = repoDeHeladeras;
        this.repoDeTarjetas = repoDeTarjetas;
    }

    @Override
    public void connectionLost(Throwable throwable) {
        logger.error("MQTT connection lost", throwable);
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
        logger.info("LLEGO EL MENSAJE: ", mqttMessage.toString());
        // Convertir el payload del mensaje a String
        String payload = new String(mqttMessage.getPayload());

        // Parsear el JSON usando Gson
        JsonObject jsonObject = JsonParser.parseString(payload).getAsJsonObject();

        // Extraer los valores del JSON
        String idTarjeta = jsonObject.get("idTarjeta").getAsString();
        String idHeladera = jsonObject.get("idHeladera").getAsString();

        manejarApertura(idHeladera, idTarjeta);

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        logger.info("Delivery complete: {}", iMqttDeliveryToken.getMessageId());
    }

    public void manejarApertura(String idHeladera, String codigoAlfaNumerico) {
        Heladera heladera = (Heladera) this.repoDeHeladeras.buscarPorId(Long.valueOf(idHeladera));
        Tarjeta tarjeta = this.repoDeTarjetas.buscarPorCodigo(codigoAlfaNumerico);
        if (tarjeta == null) {
            logger.warn("Tarjeta no encontrada.");
            avisarApertura(codigoAlfaNumerico, heladera.getTopicMQTT(), heladera, "TARJETA NO ENCONTRADA");
            return;
        }
        if (heladera == null) {
            logger.warn("Heladera no encontrada.");
            //SI NO EXISTE EL ID DE HELADERA NO AVISA PORQ NO SABE A QUE TOPIC
            return;
        }

        switch (tarjeta.getTipoTarjeta()) {
            case PERSONA_VULNERABLE:
                if(tarjeta.cantidadUsosDiariosDisponibles(LocalDate.now()) > 0) {
                    if(!heladera.getViandas().isEmpty()) {
                        logger.info("Puede usar la tarjeta, le quedan " + (tarjeta.cantidadUsosDiariosDisponibles(LocalDate.now()) - 1) + " usos diarios.");
                        UsoTarjeta usoTarjeta = new UsoTarjeta();
                        usoTarjeta.setFechaDeUso(LocalDate.now());
                        usoTarjeta.setHeladeraUsada(heladera);
                        usoTarjeta.setVianda(heladera.getViandas().get(0));
                        tarjeta.agregarUso(usoTarjeta);

                        heladera.retirarVianda(usoTarjeta.getVianda());

                        EntityTransaction tx = entityManager().getTransaction();
                        if (!tx.isActive())
                            tx.begin();
                        this.repoDeTarjetas.actualizar(tarjeta);
                        this.repoDeHeladeras.actualizar(heladera);
                        tx.commit();

                        avisarApertura(tarjeta.getCodigoAlfaNumerico(), heladera.getTopicMQTT(), heladera, "ABRIR");
                    }else{
                        logger.warn("No hay viandas en la heladera.");
                        avisarApertura(tarjeta.getCodigoAlfaNumerico(), heladera.getTopicMQTT(), heladera, "SIN VIANDAS");
                    }
                }else{
                    logger.warn("No puede usar la tarjeta. No le quedan usos");
                    avisarApertura(tarjeta.getCodigoAlfaNumerico(), heladera.getTopicMQTT(), heladera, "SIN USOS DISPONIBLES");
                }

                break;
            case COLABORADOR:
                SolicitudApertura solicitudApertura = this.repoDeSolicitudes.buscarPorHeladeraYTarjeta(Long.valueOf(idHeladera), Long.valueOf(tarjeta.getId()));
                if (solicitudApertura != null && solicitudApertura.tiempoRestante() > 0 && !solicitudApertura.getFueExitosa()) {
                    Colaboracion colaboracion = repoDeSolicitudes.buscarColaboracionPorSolicitud(solicitudApertura.getId());

                    Apertura apertura = new Apertura();
                    apertura.setFechaYHora(LocalDateTime.now());
                    apertura.setSolicitudApertura(solicitudApertura);
                    apertura.setHeladera(heladera);
                    solicitudApertura.setFueExitosa(true);
                    tarjeta.agregarApertura(apertura);

                    //SI TODAS LAS SOLICCITUDES FUERON EXITOSAS, ENTIONCES LA COLABORACION LO ES
                    if (colaboracion.getSolicitudesApertura().stream().allMatch(SolicitudApertura::getFueExitosa)) {
                        colaboracion.setFueExitosa(true);
                    }

                    EntityTransaction tx = entityManager().getTransaction();
                    if (!tx.isActive())
                        tx.begin();
                    this.repoDeSolicitudes.guardar(apertura);
                    this.repoDeSolicitudes.actualizar(solicitudApertura);
                    this.repoDeTarjetas.actualizar(tarjeta);
                    this.repoDeSolicitudes.actualizar(colaboracion);
                    tx.commit();
                    logger.info("Apertura exitosa.");
                    avisarApertura(tarjeta.getCodigoAlfaNumerico(), heladera.getTopicMQTT(), heladera, "ABRIR");
                } else {
                    logger.warn("No se encontró ninguna solicitud de apertura válida.");
                    avisarApertura(tarjeta.getCodigoAlfaNumerico(), heladera.getTopicMQTT(), heladera, "SIN SOLICITUD");
                }
                break;
        }

    }

    public void avisarApertura(String idTarjeta, String heladeraTopic, Heladera heladera, String estado) {
        MqttSender mqttSender = new MqttSender(MqttConfig.broker, MqttConfig.clientId);
        MqttMessageConEstado mensaje = new MqttMessageConEstado(idTarjeta, heladera.getId().toString(), estado);
        Gson gson = new Gson();
        mqttSender.enviarMensaje(heladeraTopic, gson.toJson(mensaje), MqttConfig.qos);
    }

}
