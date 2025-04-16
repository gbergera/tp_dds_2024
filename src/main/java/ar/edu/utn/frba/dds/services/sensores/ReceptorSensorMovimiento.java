package ar.edu.utn.frba.dds.services.sensores;

import ar.edu.utn.frba.dds.models.entities.incidente.ReporteDeIncidentes;
import ar.edu.utn.frba.dds.models.entities.incidente.TipoIncidente;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.incidente.builder.IReporteDeIncidentesBuilder;
import ar.edu.utn.frba.dds.models.repositories.*;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import lombok.Getter;

import javax.mail.MessagingException;
import javax.persistence.EntityTransaction;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

@Getter
public class ReceptorSensorMovimiento implements WithSimplePersistenceUnit {

    private Heladera heladera;
    private IReporteDeIncidentesBuilder reporteDeIncidentesBuilder;
    private RegistroDeFallaRepository repoDeIncidentes;
    private HeladeraRepository repoDeHeladeras;
    private TipoIncidenteRepository repoTipoIncidente;
    private Logger logger = Logger.getLogger(ReceptorSensorMovimiento.class.getName());

    public ReceptorSensorMovimiento(IReporteDeIncidentesBuilder reporteDeIncidentesBuilder,  RegistroDeFallaRepository repoDeIncidentes, HeladeraRepository repoDeHeladeras, TipoIncidenteRepository repoTipoIncidente) {
        this.reporteDeIncidentesBuilder = reporteDeIncidentesBuilder;
        this.repoDeIncidentes = repoDeIncidentes;
        this.repoDeHeladeras= repoDeHeladeras;
        this.repoTipoIncidente = repoTipoIncidente;
    }

    public void setHeladera(Heladera heladera) {
        this.heladera = heladera;
    }

    public void evaluar(String movimiento) throws IOException, MessagingException {
        if (Objects.equals(movimiento, "TRUE")) {
            this.notificar();
        }
    }

    public void notificar() throws IOException, MessagingException {
        TipoIncidente tipoIncidente = (TipoIncidente) this.repoTipoIncidente.buscarPorNombre("ALERTA DE FRAUDE");

        try {
            ReporteDeIncidentes reporte =
                this.reporteDeIncidentesBuilder.agregarHeladera(this.heladera)
                        .agregarIncidente(tipoIncidente)
                        .agregarDescripcion("ALERTA DE FRAUDE")
                        .agregarFechaYHora(LocalDateTime.now())
                        .agregarTecnico(heladera.direccionActual().getLocalidad())
                        .construir();

            entityManager().clear();


        this.heladera.agregarFalla(reporte);
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
