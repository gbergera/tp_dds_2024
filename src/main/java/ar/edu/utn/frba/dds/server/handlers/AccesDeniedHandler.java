package ar.edu.utn.frba.dds.server.handlers;

import ar.edu.utn.frba.dds.server.exceptions.AccessDeniedException;
import io.javalin.Javalin;


public class AccesDeniedHandler implements IHandler{
    @Override
    public void setHandle(Javalin app) {
        app.exception(AccessDeniedException.class, (e, context) -> {
            context.status(401);
            if(context.sessionAttribute("colaborador-id") == null && context.sessionAttribute("tecnico-id") == null){
                context.render("login.hbs");
            } else {
                context.render("401.hbs");
            }

        });
    }
}
