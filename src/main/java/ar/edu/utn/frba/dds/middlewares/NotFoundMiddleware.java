package ar.edu.utn.frba.dds.middlewares;

import ar.edu.utn.frba.dds.server.exceptions.NotFoundException;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;

public class NotFoundMiddleware {
    public static void apply(Javalin app) {
        app.error(HttpStatus.NOT_FOUND.getCode(), ctx -> {
            throw new NotFoundException();
        });
    }
}
