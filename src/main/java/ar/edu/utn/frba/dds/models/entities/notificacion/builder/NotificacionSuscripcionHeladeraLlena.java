package ar.edu.utn.frba.dds.models.entities.notificacion.builder;

import ar.edu.utn.frba.dds.models.entities.notificacion.MetodoDeNotificacion;
import ar.edu.utn.frba.dds.models.entities.persona.INotificable;

public class NotificacionSuscripcionHeladeraLlena implements NotificacionBuilder{
    Mensaje mensaje = new Mensaje("Heladera llena");
    @Override
    public NotificacionBuilder agregarReceptor(INotificable notificable) {
        mensaje.setNotificable(notificable);
        return this;
    }

    @Override
    public NotificacionBuilder agregarMensaje(String cuerpoDelMensaje) {
        mensaje.setCuerpoDelMensaje(cuerpoDelMensaje);
        return this;
    }

    @Override
    public NotificacionBuilder agregarMetodoNotificacion(MetodoDeNotificacion metodoDeNotificacion) {
        mensaje.setMetodoDeNotificacion(metodoDeNotificacion);
        return this;
    }

    @Override
    public Mensaje construir() {
        return this.mensaje;
    }
}
