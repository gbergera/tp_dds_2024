package ar.edu.utn.frba.dds.middlewares;

import ar.edu.utn.frba.dds.models.entities.persona.TipoPersona;
import ar.edu.utn.frba.dds.server.exceptions.AccessDeniedException;
import ar.edu.utn.frba.dds.server.exceptions.NotLoggedException;
import io.javalin.Javalin;
import io.javalin.http.Context;

public class AuthMiddleware {
    public static void apply(Javalin app) {
        app.beforeMatched(ctx -> {
            var userRole = getUserRoleType(ctx);
            if (!ctx.routeRoles().isEmpty() && !ctx.routeRoles().contains(userRole)) {
                throw new AccessDeniedException();
            }
        });
    }

    private static TipoPersona getUserRoleType(Context context) {
        return context.sessionAttribute("tipo-persona");
    }
}
