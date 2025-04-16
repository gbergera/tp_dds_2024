package ar.edu.utn.frba.dds.server.handlers;

import ar.edu.utn.frba.dds.server.exceptions.NotFoundException;
import io.javalin.Javalin;

public class NotFoundHandler implements IHandler {
    @Override
    public void setHandle(Javalin app) {
        app.exception(NotFoundException.class, (e, context) -> {
            context.status(404);
            context.render("404.hbs");
        });
    }
}
