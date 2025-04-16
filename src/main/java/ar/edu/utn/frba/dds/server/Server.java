package ar.edu.utn.frba.dds.server;

import ar.edu.utn.frba.dds.config.ServiceLocator;
import ar.edu.utn.frba.dds.middlewares.AuthMiddleware;
import ar.edu.utn.frba.dds.middlewares.NotFoundMiddleware;
import ar.edu.utn.frba.dds.models.entities.puntos.CalculadorDePuntaje;
import ar.edu.utn.frba.dds.server.handlers.AppHandlers;
import ar.edu.utn.frba.dds.server.utils.JavalinRenderer;
import ar.edu.utn.frba.dds.server.utils.PrettyProperties;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import io.javalin.Javalin;
import ar.edu.utn.frba.dds.server.init.Initializer;
import io.javalin.config.JavalinConfig;
import io.javalin.http.HttpStatus;

import java.io.IOException;
import java.util.function.Consumer;

public class Server {
    private static Javalin app = null;

    public static Javalin app() {
        if(app == null)
            throw new RuntimeException("App no inicializada");
        return app;
    }

    public static void init() {
        if (app == null) {
            Integer port = Integer.parseInt(PrettyProperties.getInstance().propertyFromName("server_port"));
            app = Javalin.create(config()).start(port);

            AuthMiddleware.apply(app);
            NotFoundMiddleware.apply(app);
            AppHandlers.applyHandlers(app);
            Router router = new Router();
            router.init(app);

            // Iniciar el registro de métricas
            Metrics.iniciar(app);

            // Iniciar el servidor de métricas
            MetricsServer metricsServer = new MetricsServer(Metrics.getRegistry());
            metricsServer.start();


            if (Boolean.parseBoolean(PrettyProperties.getInstance().propertyFromName("dev_mode"))) {
                Initializer.init();
            }
        }
    }

    private static Consumer<JavalinConfig> config() {
        return config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "/public";
            });

            config.fileRenderer(new JavalinRenderer().register("hbs", (path, model, context) -> {
                Handlebars handlebars = new Handlebars();
                Template template = null;
                try {
                    template = handlebars.compile(
                            "templates/" + path.replace(".hbs", ""));
                    return template.apply(model);
                } catch (IOException e) {
                    e.printStackTrace();
                    context.status(HttpStatus.NOT_FOUND);
                    return "No se encuentra la página indicada...";
                }
            }));
        };
    }
}
