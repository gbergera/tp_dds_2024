package ar.edu.utn.frba.dds.models.entities.persona;

import io.javalin.security.RouteRole;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
public enum TipoPersona implements RouteRole {
    FISICA,
    JURIDICA,
    TECNICO,
    ADMINISTRADOR
}
