package ar.edu.utn.frba.dds.models.entities.persona.builder;

import ar.edu.utn.frba.dds.models.entities.colaboracion.Colaboracion;
import ar.edu.utn.frba.dds.models.entities.notificacion.MetodoDeNotificacion;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.persona.Documento;

public interface IColaboradorBuilder {
    IColaboradorBuilder agregarNombre(String nombre);
    IColaboradorBuilder agregarApellido(String apellido);
    IColaboradorBuilder agregarDocumento(Documento documento);
    IColaboradorBuilder agregarMail(String mail);
    IColaboradorBuilder agregarColaboraciones(Colaboracion colaboracion);
    IColaboradorBuilder agregarMetodoNotificacion(MetodoDeNotificacion metodoNotificacion);
    Colaborador construir();
}
