package ar.edu.utn.frba.dds.services.sensores;

import ar.edu.utn.frba.dds.models.entities.incidente.ReporteDeIncidentes;
import ar.edu.utn.frba.dds.models.entities.incidente.TipoIncidente;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.incidente.builder.IReporteDeIncidentesBuilder;
import ar.edu.utn.frba.dds.models.entities.suscripciones.EnviadorAsincronico;
import ar.edu.utn.frba.dds.models.entities.suscripciones.Suscripcion;
import ar.edu.utn.frba.dds.models.entities.suscripciones.SuscripcionHeladeraDesperfecto;
import ar.edu.utn.frba.dds.models.repositories.*;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import lombok.Getter;
import lombok.Setter;

import javax.mail.MessagingException;
import javax.persistence.EntityTransaction;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Getter
@Setter
public class ReceptorSensorTemperatura implements WithSimplePersistenceUnit {
    private Heladera heladera;
    private LocalDateTime ultimaTemperaturaRegistrada;
    private IReporteDeIncidentesBuilder reporteDeIncidentesBuilder;
    private RegistroDeFallaRepository repoDeIncidentes;
    private HeladeraRepository repoDeHeladeras;
    private TipoIncidenteRepository repoTipoIncidente;
    private SuscripcionRepository repoSuscripciones;
    private Logger logger = Logger.getLogger(ReceptorSensorTemperatura.class.getName());

    public ReceptorSensorTemperatura(IReporteDeIncidentesBuilder reporteDeIncidentesBuilder,  RegistroDeFallaRepository repoDeIncidentes, HeladeraRepository repoDeHeladeras, TipoIncidenteRepository repoTipoIncidente, SuscripcionRepository repoSuscripciones) {
        this.reporteDeIncidentesBuilder = reporteDeIncidentesBuilder;
        this.repoDeIncidentes = repoDeIncidentes;
        this.repoDeHeladeras= repoDeHeladeras;
        this.repoTipoIncidente = repoTipoIncidente;
        this.repoSuscripciones = repoSuscripciones;
    }

    public void evaluar(String temperaturaActual) throws IOException, MessagingException {
        Double temperatura = Double.parseDouble(temperaturaActual);


        this.heladera.setUltimaTemperaturaRegistrada(temperatura);
        ultimaTemperaturaRegistrada = LocalDateTime.now();
        logger.info("Ultima temperatura registrada: " + temperaturaActual + " el: " + ultimaTemperaturaRegistrada);

        if (this.heladera.getTemperaturaMinima() > temperatura
                || temperatura > this.heladera.getTemperaturaMaxima()) {
            logger.info("Falla de temperatura registrada");
            this.notificar();
        }
    }
        public void notificar() throws IOException, MessagingException {

            TipoIncidente tipoIncidente = (TipoIncidente) this.repoTipoIncidente.buscarPorNombre("ALERTA DE TEMPERATURA");

            try {
                ReporteDeIncidentes reporte =
                    this.reporteDeIncidentesBuilder.agregarHeladera(this.heladera)
                            .agregarIncidente(tipoIncidente)
                            .agregarDescripcion("ALERTA DE TEMPERATURA")
                            .agregarFechaYHora(ultimaTemperaturaRegistrada)
                            .agregarTecnico(heladera.direccionActual().getLocalidad())
                            .construir();


                List<Suscripcion> suscripcionesANotificar = this.repoSuscripciones.buscarPorHeladerDesperfecto(heladera.getId());
                suscripcionesANotificar.forEach(s-> {
                    EnviadorAsincronico.instancia().serNotificadoPorAsync(s,heladera);
                });
                this.heladera.agregarFalla(reporte);
                this.heladera.setActiva(false);
                EntityTransaction tx = entityManager().getTransaction();
                if(!tx.isActive())
                    tx.begin();
                this.repoDeIncidentes.guardar(reporte);
                this.repoDeHeladeras.actualizar(this.heladera);
                tx.commit();
            } catch (IllegalStateException e) {
                logger.severe("No hay técnicos disponibles en la localidad: " + e.getMessage());
            }
        }

    }

