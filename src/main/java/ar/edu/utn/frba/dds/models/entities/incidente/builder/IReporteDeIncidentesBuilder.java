package ar.edu.utn.frba.dds.models.entities.incidente.builder;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.incidente.ReporteDeIncidentes;
import ar.edu.utn.frba.dds.models.entities.incidente.TipoIncidente;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Localidad;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDateTime;

public interface IReporteDeIncidentesBuilder {
    IReporteDeIncidentesBuilder agregarHeladera(Heladera heladera);
    IReporteDeIncidentesBuilder agregarIncidente(TipoIncidente tipoIncidente);
    IReporteDeIncidentesBuilder agregarFechaYHora(LocalDateTime fechaYHora);
    IReporteDeIncidentesBuilder agregarTecnico(Localidad localidad) throws MessagingException, IOException;
    IReporteDeIncidentesBuilder agregarFoto(String foto);

    IReporteDeIncidentesBuilder agregarDescripcion(String descripcion);
    IReporteDeIncidentesBuilder agregarComunicador(Colaborador colaborador);





    ReporteDeIncidentes construir();
}
