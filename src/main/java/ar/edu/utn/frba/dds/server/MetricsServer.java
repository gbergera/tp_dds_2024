package ar.edu.utn.frba.dds.server;

import io.javalin.Javalin;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusMeterRegistry;

public class MetricsServer {
    private final PrometheusMeterRegistry registry;

    public MetricsServer(PrometheusMeterRegistry registry) {
        this.registry = registry;
    }

    public void start() {
        Javalin metricsApp = Javalin.create()
                .get("/metrics", ctx -> {
                    ctx.contentType("text/plain; version=0.0.4").result(registry.scrape());
                    //String jsonMetrics = getMetricsAsJson(registry);
                    //ctx.contentType("application/json").result(jsonMetrics);
                })
                .start(7070); // Cambia el puerto según tu preferencia
    }

    private static String getMetricsAsJson(MeterRegistry registry) {
        // Aquí debes convertir las métricas a formato JSON
        // Puedes usar una biblioteca como Jackson o Gson para realizar esta conversión.

        // Ejemplo básico de conversión a JSON (deberías adaptarlo según tus necesidades)
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n  \"metrics\": [\n");

        // Itera sobre los registros de métricas y construye el JSON
        registry.getMeters().forEach(meter -> {
            jsonBuilder.append("    {");
            jsonBuilder.append("\"name\": \"").append(meter.getId().getName()).append("\",");
            jsonBuilder.append("\"value\": ").append(meter.measure().iterator().next().getValue());
            jsonBuilder.append("},\n");
        });

        // Elimina la última coma y agrega el cierre del JSON
        if (jsonBuilder.length() > 2) {
            jsonBuilder.setLength(jsonBuilder.length() - 2); // Elimina la última coma
        }
        jsonBuilder.append("\n  ]\n}");

        return jsonBuilder.toString();
    }
}
