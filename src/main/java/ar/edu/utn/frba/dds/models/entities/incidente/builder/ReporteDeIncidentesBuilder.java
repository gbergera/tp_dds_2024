package ar.edu.utn.frba.dds.models.entities.incidente.builder;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.incidente.ReporteDeIncidentes;
import ar.edu.utn.frba.dds.models.entities.incidente.TipoIncidente;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Localidad;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;

public class ReporteDeIncidentesBuilder implements IReporteDeIncidentesBuilder {
    private ReporteDeIncidentes reporte;

    public ReporteDeIncidentesBuilder() {
        this.reporte = new ReporteDeIncidentes();  // Iniciar una nueva instancia en el constructor
    }

    @Override
    public IReporteDeIncidentesBuilder agregarHeladera(Heladera heladera) {
        reporte.setHeladeraAfectada(heladera);
        return this;
    }

    @Override
    public IReporteDeIncidentesBuilder agregarIncidente(TipoIncidente tipoIncidente) {
        reporte.setIncidente(tipoIncidente);
        return this;
    }

    @Override
    public IReporteDeIncidentesBuilder agregarFechaYHora(LocalDateTime fechaYHora) {
        reporte.setFechaYHora(fechaYHora);
        return this;
    }

    @Override
    public IReporteDeIncidentesBuilder agregarTecnico(Localidad localidad) throws MessagingException, IOException {
        reporte.buscaryAsignarTecnico(localidad);
        return this;
    }

    @Override
    public IReporteDeIncidentesBuilder agregarFoto(String foto) {
        reporte.setFoto(foto);
        return this;
    }

    @Override
    public IReporteDeIncidentesBuilder agregarDescripcion(String descripcion) {
        reporte.setDescripcion(descripcion);
        return this;
    }

    @Override
    public IReporteDeIncidentesBuilder agregarComunicador(Colaborador colaborador) {
        reporte.setComunicador(colaborador);
        return this;
    }

    @Override
    public ReporteDeIncidentes construir() {
        ReporteDeIncidentes reporteConstruido = this.reporte;
        this.reporte = new ReporteDeIncidentes();  // Crear una nueva instancia para el próximo uso
        return reporteConstruido;
    }
}
