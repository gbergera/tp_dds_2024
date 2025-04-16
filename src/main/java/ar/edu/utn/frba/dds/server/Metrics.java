package ar.edu.utn.frba.dds.server;

import io.javalin.Javalin;
import io.micrometer.core.instrument.*;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmHeapPressureMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.util.concurrent.TimeUnit;


public class Metrics {
    private static final PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    private static Counter httpRequestsCounter;
    private static Timer httpResponseTimeTimer;
    private static Counter successCounter; // Declare successCounter
    private static Counter errorCounter;
    private static Counter requestStatusCounter;

    public static void iniciar(Javalin app) {
        // Configuración del registry para Prometheus
        registry.config().commonTags("app", "nutrevidas");

        try (var jvmGcMetrics = new JvmGcMetrics();
             var jvmHeapPressureMetrics = new JvmHeapPressureMetrics()) {
            jvmGcMetrics.bindTo(registry);
            jvmHeapPressureMetrics.bindTo(registry);
        }
        new JvmMemoryMetrics().bindTo(registry);
        new ProcessorMetrics().bindTo(registry);
        new FileDescriptorMetrics().bindTo(registry);

        // Inicializar métricas de HTTP
        httpRequestsCounter = Counter.builder("http_requests_total")
                .description("Total number of HTTP requests")
                .register(registry);

        httpResponseTimeTimer = Timer.builder("http_response_time_seconds")
                .description("HTTP response time in seconds")
                .register(registry);

        successCounter = Counter.builder("http_requests_success_total")
                .description("Total number of successful HTTP requests")
                .register(registry);

        errorCounter = Counter.builder("http_requests_error_total")
                .description("Total number of error HTTP requests")
                .register(registry);

        // Middleware para contar las solicitudes y medir el tiempo de respuesta
        app.before(ctx -> {
            // Iniciar el temporizador antes de manejar la solicitud
            ctx.attribute("startTime", System.nanoTime());
        });

        app.after(ctx -> {
            // Contar la solicitud HTTP
            httpRequestsCounter.increment();
            // Medir el tiempo de respuesta
            long startTime = ctx.attribute("startTime");
            httpResponseTimeTimer.record((long) ((System.nanoTime() - startTime) / 1e9), TimeUnit.SECONDS);

            int statusCode = ctx.status().getCode();
            if (statusCode >= 200 && statusCode < 500)  {
                successCounter.increment();
            } else {
                errorCounter.increment();
            }

            // Contar solicitudes por ruta y estado
            String route = ctx.path();
            requestStatusCounter = Counter.builder("http_requests_status")
                    .description("HTTP requests by route and status")
                    .tag("route", route)
                    .tag("status", String.valueOf(statusCode))
                    .register(registry);
            requestStatusCounter.increment();
        });

    }

    public static PrometheusMeterRegistry getRegistry() {
        return registry;
    }
}
