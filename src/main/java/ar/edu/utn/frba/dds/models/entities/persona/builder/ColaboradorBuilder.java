package ar.edu.utn.frba.dds.models.entities.persona.builder;

import ar.edu.utn.frba.dds.models.entities.colaboracion.Colaboracion;
import ar.edu.utn.frba.dds.models.entities.notificacion.MetodoDeNotificacion;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.persona.Documento;

import java.util.ArrayList;

public class ColaboradorBuilder implements IColaboradorBuilder{
    Colaborador colaborador = new Colaborador();
    @Override
    public IColaboradorBuilder agregarNombre(String nombre) {
        colaborador.setNombre(nombre);
        return this;
    }

    @Override
    public IColaboradorBuilder agregarApellido(String apellido) {
        colaborador.setApellido(apellido);
        return this;
    }

    @Override
    public IColaboradorBuilder agregarDocumento(Documento documento) {
        colaborador.setDocumento(documento);
        return this;
    }

    @Override
    public IColaboradorBuilder agregarMail(String mail) {
        colaborador.setMail(mail);
        return this;
    }
    @Override
    public IColaboradorBuilder agregarMetodoNotificacion(MetodoDeNotificacion metodoDeNotificacion) {
        colaborador.setMetodoDeNotificacion(metodoDeNotificacion);
        return this;
    }

    @Override
    public IColaboradorBuilder agregarColaboraciones(Colaboracion colaboracion) {
        if (colaborador.getColaboraciones() == null) {
            colaborador.setColaboraciones(new ArrayList<>());
        }
        colaborador.agregarColaboracion(colaboracion);
        return this;
    }

    @Override
    public Colaborador construir() {
        Colaborador resultado = colaborador;
        this.colaborador = new Colaborador();
        return resultado;
    }
}
