package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.config.ServiceLocator;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.incidente.ReporteDeIncidentes;
import ar.edu.utn.frba.dds.models.entities.incidente.builder.ReporteDeIncidentesBuilder;
import ar.edu.utn.frba.dds.models.entities.persona.Tecnico;
import ar.edu.utn.frba.dds.models.entities.persona.Visita;
import ar.edu.utn.frba.dds.models.repositories.*;
import ar.edu.utn.frba.dds.server.utils.ICrudViewsHandler;
import ar.edu.utn.frba.dds.services.sensores.FallaConexion;
import ar.edu.utn.frba.dds.services.sensores.ReceptorSensorTemperatura;
import ar.edu.utn.frba.dds.services.sensores.SensorTemperatura;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class TecnicoController extends Controller implements ICrudViewsHandler {
    private TecnicoRepository repoDeTecnicos;
    private Logger logger = Logger.getLogger(TecnicoController.class.getName());

    public TecnicoController(TecnicoRepository repoDeTecnicos) {
        this.repoDeTecnicos = repoDeTecnicos;
    }

    @Override
    public void index(Context context) {
        Long tecnicoId = Long.valueOf(context.sessionAttribute("tecnico-id"));
        Tecnico tecnico = (Tecnico) repoDeTecnicos.buscar(tecnicoId);
        List<ReporteDeIncidentes> incidentes = tecnico.getIncidentesAAteneder();

        // Filtrar y ordenar los incidentes
        List<ReporteDeIncidentes> sortedIncidentes = incidentes.stream()
                .sorted(Comparator.comparing(incidente -> {
                    ReporteDeIncidentes reporte = (ReporteDeIncidentes) incidente;
                    Boolean solucionado = reporte.getSolucionado();
                    return solucionado == null || !solucionado;
                }, Comparator.reverseOrder()).thenComparing(incidente -> {
                    ReporteDeIncidentes reporte = (ReporteDeIncidentes) incidente;
                    return reporte.getFechaYHora();
                }, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());

        Map<String, Object> model = new HashMap<>();
        model.put("tecnico", tecnico);
        model.put("incidentes", sortedIncidentes);
        context.render("tecnicoIncidentes.hbs", model);

    }

    @Override
    public void show(Context context) {
        Map<String, Object> model = new HashMap<>();
    }

    @Override
    public void create(Context context) {
        Map<String, Object> model = new HashMap<>();
    }

    @Override
    public void save(Context context) {
        Map<String, Object> model = new HashMap<>();
    }

    @Override
    public void edit(Context context) {
        Map<String, Object> model = new HashMap<>();
    }

    @Override
    public void update(Context context) {
        Map<String, Object> model = new HashMap<>();
    }

    @Override
    public void delete(Context context) {
        Map<String, Object> model = new HashMap<>();
    }

    public void showVisitas(Context ctx) {
        Long incidenteId = Long.valueOf(ctx.pathParam("id"));
        Long tecnicoId = Long.valueOf(ctx.sessionAttribute("tecnico-id"));
        Tecnico tecnico = (Tecnico) repoDeTecnicos.buscar(tecnicoId);
        ReporteDeIncidentes incidente = repoDeTecnicos.findIncidenteById(incidenteId);
        List<Visita> visitas = repoDeTecnicos.findVisitasByIncidenteId(incidenteId);

        if (incidente != null && tecnico.getIncidentesAAteneder().contains(incidente)) {
            Map<String, Object> model = new HashMap<>();
            model.put("incidente", incidente);
            model.put("visitas", visitas);
            ctx.render("visitas.hbs", model);
        } else {
            ctx.status(404).result("Incidente no encontrado");
        }
    }

    public void saveVisita(Context ctx) {
        try{
            String uploadDir = "upload/imagenes"; // Cambia esta ruta según sea necesario
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            UploadedFile imageFile = ctx.uploadedFile("foto");
            String imagePath = null;
            if (imageFile != null && imageFile.size() > 0 && imageFile.contentType() != null && !imageFile.contentType().equals("application/octet-stream")) {
                String uniqueFileName = UUID.randomUUID().toString() + "." + imageFile.contentType().split("/")[1]; // Añade tipo de archivo (ej: .jpg, .png)
                File file = new File(directory, uniqueFileName);
                try (InputStream input = imageFile.content();
                     FileOutputStream output = new FileOutputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = input.read(buffer)) != -1) {
                        output.write(buffer, 0, bytesRead);
                    }
                    imagePath = uniqueFileName;

                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Error al guardar la imagen: " + e.getMessage(), e);
                    ctx.status(500).result("Error al guardar la imagen: " + e.getMessage());
                    ctx.render("error.hbs");
                    return; // Termina el método si hay un error
                }
            } else {
               logger.info("No se subió ninguna imagen válida.");
            }
                Long tecnicoId = Long.valueOf(ctx.sessionAttribute("tecnico-id"));
                Tecnico tecnico = (Tecnico) repoDeTecnicos.buscar(tecnicoId);
                Long incidenteId = Long.valueOf(ctx.formParam("reporteId"));
                ReporteDeIncidentes incidente = tecnico.getIncidentesAAteneder().stream().filter(i -> i.getId().equals(incidenteId)).findFirst().orElse(null);
                Visita visita = new Visita();
                visita.setFechaYHora(ctx.formParam("fechaYHora") != null ? LocalDateTime.parse(ctx.formParam("fechaYHora")) : LocalDateTime.now());
                visita.setHeladeraVisitada(incidente.getHeladeraAfectada());
                visita.setFoto(imagePath);
                visita.setReporte(incidente);
                visita.setSolucionado(ctx.formParam("solucionado") != null);
                visita.setDescripcion(ctx.formParam("descripcion"));
                tecnico.agregarVisita(visita);
                if (visita.getSolucionado()) {
                    //SI TIRENE MAS INCIDENTES SIN SOLUCIONAR NO ESTA SOLUCIONADO
                    incidente.setSolucionado(true);
                    Heladera heladera = incidente.getHeladeraAfectada();
                    Integer unsolvedIncidents = (int) heladera.getRegistroDeFallos().stream()
                            .filter(i -> Boolean.FALSE.equals(i.getSolucionado()))
                            .count();
                    if (!(unsolvedIncidents >0)) {
                        heladera.setActiva(true);
                        logger.info("Heladera " + heladera.getId() + " activada.");
                        SensorTemperatura sensorTemperatura = new SensorTemperatura();
                        ReceptorSensorTemperatura receptorTemperatura = new ReceptorSensorTemperatura(ServiceLocator.instanceOf(ReporteDeIncidentesBuilder.class), ServiceLocator.instanceOf(RegistroDeFallaRepository.class), ServiceLocator.instanceOf(HeladeraRepository.class), ServiceLocator.instanceOf(TipoIncidenteRepository.class), ServiceLocator.instanceOf(SuscripcionRepository.class));
                        receptorTemperatura.setHeladera(heladera);
                        sensorTemperatura.setReceptorSensorTemperatura(receptorTemperatura);
                        FallaConexion cron = new FallaConexion(receptorTemperatura, ServiceLocator.instanceOf(ReporteDeIncidentesBuilder.class), ServiceLocator.instanceOf(TipoIncidenteRepository.class), ServiceLocator.instanceOf(RegistroDeFallaRepository.class),ServiceLocator.instanceOf(HeladeraRepository.class));
                        cron.iniciarCron();
                       logger.info("Cron reiniciado");
                    }
                    repoDeTecnicos.actualizarHeladera(heladera);
                }

                repoDeTecnicos.actualizar(tecnico);
                HashMap<String, Object> model = new HashMap<>();
                model.put("redireccion", "/tecnico");
                ctx.render("success.hbs", model);
        }catch (Exception e){
            logger.log(Level.SEVERE, "Error al guardar la visita: " + e.getMessage(), e);
            ctx.status(500).result("Error al guardar el reporte");
            HashMap<String, Object> model = new HashMap<>();
            model.put("redireccion", "/tecnico");
            ctx.render("error.hbs");
        }
    }
}
