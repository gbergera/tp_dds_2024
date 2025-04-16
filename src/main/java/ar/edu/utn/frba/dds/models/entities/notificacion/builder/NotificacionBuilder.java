package ar.edu.utn.frba.dds.models.entities.notificacion.builder;

import ar.edu.utn.frba.dds.models.entities.notificacion.MetodoDeNotificacion;
import ar.edu.utn.frba.dds.models.entities.persona.INotificable;

import java.util.Set;

public interface NotificacionBuilder {
    NotificacionBuilder agregarReceptor(INotificable notificable);
    NotificacionBuilder agregarMensaje(String cuerpoDelMensaje);
    NotificacionBuilder agregarMetodoNotificacion(MetodoDeNotificacion metodoDeNotificacion);

    Mensaje construir();
}
