package ar.edu.utn.frba.dds.server;


import ar.edu.utn.frba.dds.config.ServiceLocator;
import ar.edu.utn.frba.dds.controllers.ColaboracionController;
import ar.edu.utn.frba.dds.controllers.FactoryController;
import ar.edu.utn.frba.dds.controllers.PersonaVulnerableController;
import ar.edu.utn.frba.dds.controllers.UsuarioController;
import ar.edu.utn.frba.dds.controllers.*;
import ar.edu.utn.frba.dds.models.entities.persona.TipoPersona;
import ar.edu.utn.frba.dds.models.entities.ubicacion.Ubicacion;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.ServiceGeoref;
import ar.edu.utn.frba.dds.models.entities.ubicacion.recomendador.Recomendador;
import ar.edu.utn.frba.dds.services.APIReconocimientoService;
import ar.edu.utn.frba.dds.services.ColaboradorDTO;
import ar.edu.utn.frba.dds.controllers.*;
import ar.edu.utn.frba.dds.models.entities.reportes.ReporteGenerado;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.Javalin;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Router implements WithSimplePersistenceUnit {
    private static final Logger logger = Logger.getLogger(Router.class.getName());
    public void init(Javalin app) {
        /*
        app.before(ctx -> {
                entityManager().clear();
                //logger.info("EntityManager and second-level cache cleared successfully.");
        });
*/

        app.exception(Exception.class, (e, ctx) -> {
            logger.log(Level.SEVERE, "Internal server error at URL: " + ctx.fullUrl(), e);
            ctx.status(500);
            ctx.render("error.hbs");
        });

        app.get("/test", ctx -> ctx.result("Hola " + ctx.sessionAttribute("usuario-id") + " " + ctx.sessionAttribute("usuario-nombre")
        + " " + ctx.sessionAttribute("tipo-persona") + " " + ctx.sessionAttribute("colaborador-id")));

        //EJEMPLOS
        app.get("/saludo-para/{nombre}", ctx -> ctx.result("Hola " + ctx.pathParam("nombre")));

        //Query Params
        app.get("/saludo", ctx -> {
            ctx.result("Hola " + ctx.queryParam("nombre") + " " + ctx.queryParam("apellido"));
        });

        //GEOREF
        app.get("/provincias", ctx -> {
            try {
                ctx.json(ServiceGeoref.instancia().listadoDeProvincias());
            } catch (IOException e) {
                ctx.status(500).result("Error fetching provincias");
            }
        });
        app.get("/municipios", ctx -> {
            Integer provinciaId = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("provinciaId")));
            try {
                ctx.json(ServiceGeoref.instancia().listadoDeMunicipiosPorProvincia(provinciaId));
            } catch (IOException e) {
                ctx.status(500).result("Error fetching municipios");
            }
        });

        app.get("/localidades", ctx -> {
            Integer municipioId = Integer.parseInt(Objects.requireNonNull(ctx.queryParam("municipioId")));
            try {
                ctx.json(ServiceGeoref.instancia().listadoDeLocalidadesPorMunicipio(municipioId));
            } catch (IOException e) {
                ctx.status(500).result("Error fetching localidades");
            }
        });

        //PANTALLA DE LOGIN
        app.get("/", ctx -> ctx.redirect("/login"));
        app.get("/login", ctx -> ctx.render("login.hbs"));
        app.post("/login", ctx -> {
            ((UsuarioController) FactoryController.controller("Usuario")).verify(ctx);
        });

        //PANTALLA DE REGISTRO
        app.get("/registro", ctx -> {
            ((ColaboradorController) FactoryController.controller("Colaborador")).create(ctx);
        });
        app.post("/registro", ctx -> {
            ((ColaboradorController) FactoryController.controller("Colaborador")).save(ctx);
        });

        //PANTALLA SDE TECNICOS
        app.get("/tecnico", ctx -> {
            ((TecnicoController) FactoryController.controller("Tecnico")).index(ctx);
        }, TipoPersona.TECNICO);
        app.post("/tecnico/visita", ctx -> {
            ((TecnicoController) FactoryController.controller("Tecnico")).saveVisita(ctx);
        }, TipoPersona.TECNICO);
        app.get("/tecnico/visitas/{id}", ctx -> {
            ((TecnicoController) FactoryController.controller("Tecnico")).showVisitas(ctx);
        }, TipoPersona.TECNICO);

        app.get("/visitas", ctx -> {
            ((VisitasController) FactoryController.controller("Visita")).index(ctx);
        }, TipoPersona.JURIDICA);
        //PANTALLA DE HELADERAS
        app.get("/misheladeras", ctx -> {
            ((HeladeraController) FactoryController.controller("Heladera")).misHeladeras(ctx);
        }, TipoPersona.JURIDICA);
        app.get("/heladeras", ctx -> {
            ((HeladeraController) FactoryController.controller("Heladera")).index(ctx);
        }, TipoPersona.FISICA, TipoPersona.JURIDICA);

        app.get("/api/heladeras", ctx -> {
            ((HeladeraController) FactoryController.controller("Heladera")).getAll(ctx);
        }, TipoPersona.FISICA, TipoPersona.JURIDICA);

        app.get("/api/modelos-heladeras", ctx -> {
            ((HeladeraController)FactoryController.controller("Heladera")).getModelos(ctx);
        }, TipoPersona.FISICA, TipoPersona.JURIDICA);
        //PANTALLA DE PERFIL
        app.get("/perfil", ctx -> {
            ((ColaboradorController) FactoryController.controller("Colaborador")).show(ctx);
        }, TipoPersona.FISICA, TipoPersona.JURIDICA);
        app.post("/perfil", ctx -> {
            ((ColaboradorController) FactoryController.controller("Colaborador")).update(ctx);
        }, TipoPersona.FISICA, TipoPersona.JURIDICA);

        //PANTALLA DE PUNTOS
        app.get("/puntos", ctx -> {
            ((ColaboracionController) FactoryController.controller("Colaboracion")).indexPuntos(ctx);
        }, TipoPersona.FISICA, TipoPersona.JURIDICA);
        app.get("/miscompras", ctx -> {
            ((ColaboracionController) FactoryController.controller("Colaboracion")).indexCompras(ctx);
        }, TipoPersona.FISICA, TipoPersona.JURIDICA);
        app.post("/conseguir-producto/{id}", ctx -> {
            ((ColaboradorController) FactoryController.controller("Colaborador")).canjearOferta(ctx);
        }, TipoPersona.FISICA, TipoPersona.JURIDICA);
        app.get("/misofertas", ctx -> {
            ((ColaboracionController) FactoryController.controller("Colaboracion")).misOfertas(ctx);
        }, TipoPersona.FISICA, TipoPersona.JURIDICA);
        //PANTALLA DE ADMINISTRADOR
        app.get("/administrador/usuarios", ctx -> {
            ((AdminController) FactoryController.controller("Admin")).usuarios(ctx);
        }, TipoPersona.ADMINISTRADOR);
        app.get("/administrador/colaboradores", ctx -> {
            ((AdminController) FactoryController.controller("Admin")).colaboradores(ctx);
        }, TipoPersona.ADMINISTRADOR);
        app.get("/administrador/heladeras", ctx -> {
            ((AdminController) FactoryController.controller("Admin")).heladeras(ctx);
        }, TipoPersona.ADMINISTRADOR);
        app.get("/administrador/formularios", ctx -> {
            ((AdminController) FactoryController.controller("Admin")).formularios(ctx);
        }, TipoPersona.ADMINISTRADOR);
        app.get("/administrador/tecnicos", ctx -> {
            ((AdminController) FactoryController.controller("Admin")).tecnicos(ctx);
        }, TipoPersona.ADMINISTRADOR);
        app.get("/administrador/tarjetas", ctx -> {
            ((AdminController) FactoryController.controller("Admin")).tarjetas(ctx);
        }, TipoPersona.ADMINISTRADOR);
        app.get("/administrador/importadorCsv", ctx -> {
            ctx.render("admin/csvImport.hbs");
        },TipoPersona.ADMINISTRADOR);
        app.post("/administrador/importadorCsv", ctx -> {
            ((AdminController) FactoryController.controller("Admin")).saveCsv(ctx);   },TipoPersona.ADMINISTRADOR);
        app.post("/administrador/formulario", ctx -> {
            ((AdminController) FactoryController.controller("Admin")).saveFormulario(ctx);
        }, TipoPersona.ADMINISTRADOR);
        app.post("/administrador/formulario/{id}", ctx -> {
            ((AdminController) FactoryController.controller("Admin")).deleteFormulario(ctx);
        }, TipoPersona.ADMINISTRADOR);
        app.post("/administrador/usuario/{id}", ctx -> {
            ((AdminController) FactoryController.controller("Admin")).deleteUsuario(ctx);
        }, TipoPersona.ADMINISTRADOR);
        app.post("/administrador/colaborador/{id}", ctx -> {
            ((AdminController) FactoryController.controller("Admin")).deleteColaborador(ctx);
        }, TipoPersona.ADMINISTRADOR);
        app.post("/administrador/heladera/{id}", ctx -> {
            ((AdminController) FactoryController.controller("Admin")).deleteHeladera(ctx);
        }, TipoPersona.ADMINISTRADOR);
        app.post("/administrador/tecnico", ctx -> {
            ((AdminController) FactoryController.controller("Admin")).saveTecnico(ctx);
        }, TipoPersona.ADMINISTRADOR);
        app.get("/administrador/alertas", ctx -> {
            ((AdminController) FactoryController.controller("Admin")).alertas(ctx);
        }, TipoPersona.ADMINISTRADOR);
        app.get("/administrador/visitas", ctx -> {
            ((AdminController) FactoryController.controller("Admin")).visitas(ctx);
        }, TipoPersona.JURIDICA, TipoPersona.ADMINISTRADOR);
        app.get("/imagenes/{imageName}", ctx -> {
            String imageName = ctx.pathParam("imageName");
            // Definir la ruta desde donde se servirán las imágenes
            Path imagePath = Paths.get("upload", "imagenes", imageName);
            if (Files.exists(imagePath)) {
                // Detectar el tipo de contenido automáticamente
                String contentType = Files.probeContentType(imagePath);
                ctx.contentType(contentType);
                // Enviar el archivo
                ctx.result(Files.newInputStream(imagePath));
            } else {
                ctx.status(404).result("Imagen no encontrada.");
            }
        }, TipoPersona.FISICA, TipoPersona.JURIDICA, TipoPersona.ADMINISTRADOR, TipoPersona.TECNICO);

        app.get("/recomendacion", ctx -> {

            String latParam = ctx.queryParam("lat");
            String lngParam = ctx.queryParam("lng");
            String radiusParam = ctx.queryParam("radius");

            try {
                float lat = Float.parseFloat(latParam);
                float lng = Float.parseFloat(lngParam);
                double radius = Double.parseDouble(radiusParam);

                // Obtener las ubicaciones recomendadas
                Recomendador recomendador = new Recomendador();
                List<Ubicacion> ubicaciones = recomendador.pedirRecomendacion(lat, lng, radius);

                if (ubicaciones != null && !ubicaciones.isEmpty()) {
                    // Mapea las ubicaciones a un formato que el cliente pueda usar
                    List<Map<String, Object>> recomendaciones = new ArrayList<>();
                    for (Ubicacion ubicacion : ubicaciones) {
                        Map<String, Object> rec = new HashMap<>();
                        rec.put("lat", ubicacion.getLatitud());   // Latitude
                        rec.put("lng", ubicacion.getLongitud());  // Longitude
                        recomendaciones.add(rec);
                    }

                    ctx.json(Map.of("recomendaciones", recomendaciones)); // Devuelve las recomendaciones en formato JSON
                } else {
                    ctx.json(Map.of("recomendaciones", new ArrayList<>())); // Devuelve un arreglo vacío si no hay recomendaciones
                }
            } catch (NumberFormatException e) {
                ctx.status(400).result("Parámetros de consulta no válidos.");
            } catch (Exception e) {
                ctx.status(500).result("Error al procesar la solicitud.");
            }
        });


        // PANTALLA DE ALERTAS
        app.get("/alertas-test", ctx -> ctx.render("alertas.hbs"), TipoPersona.FISICA, TipoPersona.JURIDICA);

        // PANTALLA DE REPORTES
        app.get("/reportes", ctx -> {
            ReporteController reporteController = ServiceLocator.instanceOf(ReporteController.class);
            List<ReporteGenerado> reportes = reporteController.obtenerReportes();
            TipoPersona tipoPersona = ctx.sessionAttribute("tipo-persona");

            // Agregar el atributo "reporteActive" en true al mapa
            ctx.render("reportes.hbs", Map.of(
                    "reportes", reportes,
                    "reporteActive", true,
                    "isJuridica", tipoPersona == TipoPersona.JURIDICA
            ));
        }, TipoPersona.JURIDICA);

        app.get("/reportes/{fecha}/download", ctx -> {
            ReporteController reporteController = ServiceLocator.instanceOf(ReporteController.class);
            String fecha = ctx.pathParam("fecha");
            reporteController.descargarReportePorFecha(fecha, ctx);
        }, TipoPersona.FISICA, TipoPersona.JURIDICA);
        app.get("/reportes/test", ctx -> {
            ctx.render("reportes.hbs");
        }, TipoPersona.FISICA, TipoPersona.JURIDICA);
        app.get("/reportes/{fecha}/generar-instantaneo", ctx -> {
            ReporteController reporteController = ServiceLocator.instanceOf(ReporteController.class);
            String fecha = ctx.pathParam("fecha");
            reporteController.descargarReportePorFecha(fecha, ctx);
        }, TipoPersona.FISICA, TipoPersona.JURIDICA);

        //RUTAS SUSCRIPCIONES
        app.get("/suscripciones",ctx -> {
            ((SuscripcionesController) FactoryController.controller("Suscripciones")).index(ctx);},TipoPersona.FISICA, TipoPersona.JURIDICA);

        app.post("/suscripciones",ctx -> {((SuscripcionesController) FactoryController.controller("Suscripciones")).save(ctx);},TipoPersona.FISICA, TipoPersona.JURIDICA);

        app.post("suscripciones/eliminar", ((SuscripcionesController) FactoryController.controller("Suscripciones")) :: delete, TipoPersona.FISICA, TipoPersona.JURIDICA);

        //RUTAS REGISTRO FALLAS
        app.get("/registroDeFallas",ctx -> {((RegistroDeFallaController) FactoryController.controller("Registro-Fallas")).index(ctx);},TipoPersona.FISICA, TipoPersona.JURIDICA);

        app.post("/registroDeFallas",ctx -> {((RegistroDeFallaController) FactoryController.controller("Registro-Fallas")).save(ctx);},TipoPersona.FISICA, TipoPersona.JURIDICA);

        app.get("/api/tiposFallas", ctx -> {
            ((TipoIncidenteController) FactoryController.controller("TipoIncidente")).getAll(ctx);
        }, TipoPersona.FISICA, TipoPersona.JURIDICA);
        app.get("/api/tiposFallas/fallas", ctx -> {
            ((TipoIncidenteController) FactoryController.controller("TipoIncidente")).traerFallas(ctx);
        }, TipoPersona.FISICA, TipoPersona.JURIDICA);


        app.get("/dashboard", ctx -> {
            TipoPersona tipoPersona = ctx.sessionAttribute("tipo-persona");
            String tipoPersonaString = tipoPersona != null ? tipoPersona.name() : null;

            Map<String, Object> model = new HashMap<>();
            model.put("isFisica", tipoPersona == TipoPersona.FISICA);
            model.put("isJuridica", tipoPersona == TipoPersona.JURIDICA);
            model.put("colaborarActive",true);

            model.put("tipoPersona", tipoPersonaString);
            ctx.render("dashboard.hbs", model);

        }, TipoPersona.FISICA, TipoPersona.JURIDICA);
        app.post("/dashboard", ctx -> {
            ((ColaboracionController) FactoryController.controller("Colaboracion")).save(ctx);
        }, TipoPersona.FISICA, TipoPersona.JURIDICA);

        app.get("/success", ctx -> {
            ctx.render("success.hbs");
        }, TipoPersona.FISICA, TipoPersona.JURIDICA, TipoPersona.ADMINISTRADOR, TipoPersona.TECNICO);
        app.get("/deleteSuccess", ctx -> {
                    ctx.render("deleteSuccess.hbs");
        },TipoPersona.FISICA, TipoPersona.JURIDICA);
        app.get("/logout", ctx -> {
            ctx.sessionAttribute("usuario-id", null);
            ctx.sessionAttribute("usuario-nombre", null);
            ctx.sessionAttribute("tipo-persona", null);
            ctx.sessionAttribute("colaborador-id", null);
            ctx.redirect("/login");
        });

        app.get("/registroDePersonas", ctx -> {
            ((PersonaVulnerableController)FactoryController.controller("PersonaVulnerable")).index(ctx);
        }, TipoPersona.FISICA, TipoPersona.JURIDICA);

        app.get("/reconocimientos", ctx -> {
            Map<String, Object> model = new HashMap<>();
            model.put("reconocimientosActive", true);
            TipoPersona tipoPersona = ctx.sessionAttribute("tipo-persona");
            model.put("isJuridica", tipoPersona == TipoPersona.JURIDICA);
            ctx.render("reconocimiento.hbs", model);
      }, TipoPersona.JURIDICA);

      app.post("/reconocimientos", ctx -> {
        Double puntos = Double.valueOf(ctx.formParam("puntos"));
        Integer donaciones = Integer.valueOf(ctx.formParam("donaciones"));
        Integer maxima = Integer.valueOf(ctx.formParam("maximaCantidad"));
        APIReconocimientoService service = new APIReconocimientoService();
        List<ColaboradorDTO> colaboradoresDTO = service.obtenerReconocimientos(puntos, donaciones, maxima);
        TipoPersona tipoPersona = ctx.sessionAttribute("tipo-persona");

        Map<String, Object> model = new HashMap<>();
        model.put("reconocimientosActive", true);
        model.put("colaboradores", colaboradoresDTO);
        model.put("isJuridica", tipoPersona == TipoPersona.JURIDICA);
        ctx.render("reconocimiento.hbs", model);
      }, TipoPersona.JURIDICA);

        // Endpoint para recomendar ubicaciones aleatorias dentro de un radio especificado
        app.get("/api/recomendacion", ctx -> {

            // Obtener los parámetros de latitud, longitud y radio
            Float latitud = Float.parseFloat(ctx.queryParam("lat"));
            Float longitud = Float.parseFloat(ctx.queryParam("long"));
            Double radio = Double.parseDouble(ctx.queryParam("radio"));

            // Generar ubicaciones aleatorias cercanas
            List<Ubicacion> ubicacionesGeneradas = generarUbicacionesAleatorias(latitud, longitud, radio, 5); // Genera 5 ubicaciones

            // Preparar el resultado en el formato requerido
            Map<String, List<Map<String, Float>>> resultado = new HashMap<>();
            List<Map<String, Float>> ubicacionesJson = new ArrayList<>();

            for (Ubicacion u : ubicacionesGeneradas) {
                Map<String, Float> ubicacionMap = new HashMap<>();
                ubicacionMap.put("latitud", u.getLatitud());
                ubicacionMap.put("longitud", u.getLongitud());
                ubicacionesJson.add(ubicacionMap);
            }

            resultado.put("ubicaciones", ubicacionesJson);

            // Devolver el resultado en formato JSON
            ctx.json(resultado);
        });

        app.get("/alertas", ctx -> {
            HeladeraController heladeraController = (HeladeraController) FactoryController.controller("Heladera");
            heladeraController.mostrarAlertas(ctx);

        }, TipoPersona.FISICA, TipoPersona.JURIDICA);

        app.get("/validarNombreHeladera/", context ->{
            ColaboracionController colaboracionController = ServiceLocator.instanceOf(ColaboracionController.class);
            colaboracionController.validarNombreHeladera(context);
        }, TipoPersona.FISICA, TipoPersona.JURIDICA);

        app.get("/validarTopicMqtt/", context ->{
            ColaboracionController colaboracionController = ServiceLocator.instanceOf(ColaboracionController.class);
            colaboracionController.validarTopicMQTT(context);
        }, TipoPersona.FISICA, TipoPersona.JURIDICA);

    }


    // Método para generar ubicaciones aleatorias cercanas a una coordenada dada
    private static List<Ubicacion> generarUbicacionesAleatorias(Float latitud, Float longitud, Double radio, int cantidad) {
        List<Ubicacion> ubicaciones = new ArrayList<>();
        Random random = new Random();

        // Generar ubicaciones dentro del radio dado
        for (int i = 0; i < cantidad; i++) {
            double maxOffset = radio / 111.32; // Aproximación para convertir km en grados

            // Variar la latitud y la longitud aleatoriamente dentro del rango
            double latOffset = (random.nextDouble() * 2 - 1) * maxOffset;
            double longOffset = (random.nextDouble() * 2 - 1) * maxOffset;

            Float nuevaLatitud = latitud + (float) latOffset;
            Float nuevaLongitud = longitud + (float) longOffset;

            Ubicacion nuevaUbicacion = new Ubicacion();
            nuevaUbicacion.setLatitud(nuevaLatitud);
            nuevaUbicacion.setLongitud(nuevaLongitud);

            ubicaciones.add(nuevaUbicacion);
        }

        return ubicaciones;


    }

}
