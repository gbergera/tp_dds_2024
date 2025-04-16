package ar.edu.utn.frba.dds.services.sensores;



import ar.edu.utn.frba.dds.models.entities.incidente.ReporteDeIncidentes;
import ar.edu.utn.frba.dds.models.entities.incidente.TipoIncidente;
import ar.edu.utn.frba.dds.models.entities.incidente.builder.IReporteDeIncidentesBuilder;
import ar.edu.utn.frba.dds.models.repositories.HeladeraRepository;
import ar.edu.utn.frba.dds.models.repositories.RegistroDeFallaRepository;
import ar.edu.utn.frba.dds.models.repositories.TipoIncidenteRepository;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import lombok.Getter;

import javax.mail.MessagingException;
import javax.persistence.EntityTransaction;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
public class FallaConexion implements WithSimplePersistenceUnit {
    private ReceptorSensorTemperatura receptorSensorTemperatura;
    private IReporteDeIncidentesBuilder reporteDeIncidentesBuilder;
    private TipoIncidenteRepository repoTipoIncidente;
    private RegistroDeFallaRepository repoDeIncidentes;
    private HeladeraRepository repoDeHeladeras;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> cronFuturo;
    private Logger logger = Logger.getLogger(FallaConexion.class.getName());
    public FallaConexion(ReceptorSensorTemperatura receptorSensorTemperatura, IReporteDeIncidentesBuilder reporteDeIncidentesBuilder, TipoIncidenteRepository repoTipoIncidente, RegistroDeFallaRepository repoDeIncidentes, HeladeraRepository repoDeHeladeras) {
        this.receptorSensorTemperatura = receptorSensorTemperatura;
        this.reporteDeIncidentesBuilder = reporteDeIncidentesBuilder;
        this.repoTipoIncidente = repoTipoIncidente;
        this.repoDeIncidentes = repoDeIncidentes;
        this.repoDeHeladeras = repoDeHeladeras;
    }
    public void iniciarCron() {
        cronFuturo = scheduler.scheduleAtFixedRate(() -> {
            try {
                logger.info("Por ejecutar funcion de cron");
                verificarFallaDeConexion();
                logger.info("Ejecutada funcion de cron");

            } catch (Exception e) {
                logger.log(Level.SEVERE,"Error al ejecutar la funcion de cron: " +  e.getMessage(), e);
            }
        }, 0, 1, TimeUnit.MINUTES); // Ejecuta cada 5 minutos
    }

    public void detenerCron() {
        if (cronFuturo != null && !cronFuturo.isCancelled()) {
            cronFuturo.cancel(true); // Detiene el cron
            logger.info("Cron detenido debido a una falla de conexión.");
        }
    }
    public void verificarFallaDeConexion() throws MessagingException, IOException {
        LocalDateTime ultimaTemperatura = receptorSensorTemperatura.getUltimaTemperaturaRegistrada();
        if (ultimaTemperatura == null) {
            logger.info("No se registro ninguna temperatura.");
            return;
        }

        LocalDateTime ahora = LocalDateTime.now();
        if (haPasadoMasDeCincoMinutos(ultimaTemperatura, ahora)) {
            logger.info("Falla en la conexión detectada.");
            TipoIncidente tipoIncidente = (TipoIncidente) this.repoTipoIncidente.buscarPorNombre("ALERTA FALLA DE CONEXIÓN");

            ReporteDeIncidentes reporte =
                    this.reporteDeIncidentesBuilder.agregarHeladera(receptorSensorTemperatura.getHeladera())
                            .agregarIncidente(tipoIncidente)
                            .agregarDescripcion("ALERTA DE CONEXION")
                            .agregarFechaYHora(receptorSensorTemperatura.getUltimaTemperaturaRegistrada())
                            .agregarTecnico(receptorSensorTemperatura.getHeladera().direccionActual().getLocalidad())
                            .construir();

            receptorSensorTemperatura.getHeladera().agregarFalla(reporte);
            receptorSensorTemperatura.getHeladera().setActiva(false);
            EntityTransaction tx = entityManager().getTransaction();
            if(!tx.isActive())
                tx.begin();

            this.repoDeIncidentes.guardar(reporte);
            this.repoDeHeladeras.actualizar(receptorSensorTemperatura.getHeladera());
            tx.commit();
            detenerCron();
        }
    }
    private boolean haPasadoMasDeCincoMinutos(LocalDateTime ultimaTemperatura, LocalDateTime ahora) {

        int diferenciaEnMinutos = (ahora.getHour() - ultimaTemperatura.getHour()) * 60 + (ahora.getMinute() - ultimaTemperatura.getMinute());
        return diferenciaEnMinutos >= 1;
    }
}