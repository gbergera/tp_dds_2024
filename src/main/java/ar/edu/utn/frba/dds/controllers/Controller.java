package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.entities.persona.Usuario;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;

public class Controller implements WithSimplePersistenceUnit {
    protected Usuario usuarioLogueado(Context ctx) {
        if(ctx.sessionAttribute("usuario-id") == null) return null;

        return entityManager()
                .find(Usuario.class, Long.parseLong(ctx.sessionAttribute("usuario-id")));
    }
}
