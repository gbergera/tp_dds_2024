package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.incidente.ReporteDeIncidentes;
import ar.edu.utn.frba.dds.models.entities.incidente.TipoIncidente;
import ar.edu.utn.frba.dds.models.entities.incidente.builder.ReporteDeIncidentesBuilder;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.persona.TipoPersona;
import ar.edu.utn.frba.dds.models.entities.reportes.ReportesFallaHeladera;
import ar.edu.utn.frba.dds.models.entities.suscripciones.EnviadorAsincronico;
import ar.edu.utn.frba.dds.models.entities.suscripciones.Suscripcion;
import ar.edu.utn.frba.dds.models.entities.suscripciones.SuscripcionHeladeraDesperfecto;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Localidad;
import ar.edu.utn.frba.dds.models.repositories.ColaboradorRepository;
import ar.edu.utn.frba.dds.models.repositories.GeneralRepository;
import ar.edu.utn.frba.dds.models.repositories.HeladeraRepository;
import ar.edu.utn.frba.dds.models.repositories.RegistroDeFallaRepository;
import ar.edu.utn.frba.dds.models.repositories.SuscripcionRepository;
import ar.edu.utn.frba.dds.models.repositories.TecnicoRepository;
import ar.edu.utn.frba.dds.models.repositories.TipoIncidenteRepository;
import ar.edu.utn.frba.dds.server.utils.ICrudViewsHandler;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;

import javax.mail.MessagingException;
import javax.persistence.EntityTransaction;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RegistroDeFallaController extends Controller implements ICrudViewsHandler {
  private RegistroDeFallaRepository repoDeFallas;
  private ColaboradorRepository repositorioColaboradores;
  private HeladeraRepository repositorioHeladeras;
  private TipoIncidenteRepository repositorioTipoIncidentes;
  private Logger logger = Logger.getLogger(RegistroDeFallaController.class.getName());


  public RegistroDeFallaController(RegistroDeFallaRepository repoDeFallas, TipoIncidenteRepository repoTipoIncidentes,ColaboradorRepository repositorioColaboradores, HeladeraRepository heladeraRepository,TipoIncidenteRepository repositorioTipoIncidentes) {
    this.repoDeFallas = repoDeFallas;
    this.repositorioColaboradores = repositorioColaboradores;
    this.repositorioHeladeras = heladeraRepository;
    this.repositorioTipoIncidentes = repositorioTipoIncidentes;
  }

    @Override
    public void index(Context context) {
      Long colaboradorId = Long.valueOf(Objects.requireNonNull(context.sessionAttribute("colaborador-id")));
      List reportesDeFallas = this.repoDeFallas.buscarTodasPorColaboradorId(colaboradorId);
      Map<String, Object> model = new HashMap<>();
      model.put("fallas", reportesDeFallas);
      model.put("fallaActive", true);
      TipoPersona tipoPersona = context.sessionAttribute("tipo-persona");
      model.put("isJuridica", tipoPersona == TipoPersona.JURIDICA);
      context.render("registroFalla.hbs", model);
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
    public void save(Context context) throws MessagingException, IOException {
      try{
        // Crea la carpeta para almacenar imágenes si no existe
        String uploadDir = "upload/imagenes"; // Cambia esta ruta según sea necesario
        File directory = new File(uploadDir);
        if (!directory.exists()) {
          directory.mkdirs(); // Crea la carpeta si no existe
        }

        // Obtiene la imagen del formulario
        UploadedFile imageFile = context.uploadedFile("fotos"); // El nombre debe coincidir con el atributo 'name' del input de archivo

        // Inicializa la variable para la ruta de la imagen
        String imagePath = null;

        // Verifica que el archivo subido no sea nulo y que tenga contenido válido
        if (imageFile != null && imageFile.size() > 0 && imageFile.contentType() != null && !imageFile.contentType().equals("application/octet-stream")) {
          // Crea un nombre de archivo único usando UUID
          String uniqueFileName = UUID.randomUUID().toString() + "." + imageFile.contentType().split("/")[1]; // Añade tipo de archivo (ej: .jpg, .png)

          // Crea la ruta donde se guardará la imagen
          File file = new File(directory, uniqueFileName);

          try (InputStream input = imageFile.content();
               FileOutputStream output = new FileOutputStream(file)) {

            // Copia el contenido del archivo subido al sistema de archivos
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
              output.write(buffer, 0, bytesRead);
            }

            // Guarda el nombre del archivo en la base de datos
            imagePath = uniqueFileName; // Almacena solo el nombre de la imagen

          } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al guardar la imagen: " + e.getMessage(), e);
            context.status(500).result("Error al guardar la imagen: " + e.getMessage());
            context.render("error.hbs");
            return; // Termina el método si hay un error
          }

        } else {
          // No se subió ningún archivo válido
          logger.info("No se subió ninguna imagen válida.");
        }

        ReporteDeIncidentesBuilder builder = new ReporteDeIncidentesBuilder();
        Long colaboradorId = Long.valueOf(Objects.requireNonNull(context.sessionAttribute("colaborador-id")));
        String descripcion = context.formParam("descripcion");
        String nombreHeladera = context.formParam("heladera");
        String idIncidente = context.formParam("tipo-falla");
        Colaborador comunicador = (Colaborador) this.repositorioColaboradores.buscar(colaboradorId);
        Heladera heladera = (Heladera) this.repositorioHeladeras.buscarPorNombre(nombreHeladera);
        TipoIncidente incidente = (TipoIncidente) this.repositorioTipoIncidentes.buscar(Long.valueOf(idIncidente));
        Localidad localidadHeladera = heladera.direccionActual().getLocalidad();
        List<Suscripcion> suscripcionesANotificar = heladera.getSuscripciones().stream()
            .filter(s -> s instanceof SuscripcionHeladeraDesperfecto)
            .toList();


        LocalDateTime fechaYHora = LocalDateTime.now();

        ReporteDeIncidentes reporte = builder
                .agregarDescripcion(descripcion)
                .agregarComunicador(comunicador)
                .agregarFoto(imagePath)
                .agregarHeladera(heladera)
                .agregarIncidente(incidente)
                .agregarFechaYHora(fechaYHora)
                .construir();

        try {
          reporte.buscaryAsignarTecnico(localidadHeladera);
          suscripcionesANotificar.forEach(s-> {
              EnviadorAsincronico.instancia().serNotificadoPorAsync(s,heladera);
          });

        } catch (IllegalStateException e) {
            logger.severe("No hay técnicos disponibles en la localidad.");
          context.status(400);
          Map<String, Object> model = new HashMap<>();
          model.put("error", "No hay técnicos disponibles en la localidad.");
          context.render("error.hbs", model);
          //return;
        }
        heladera.agregarFalla(reporte);
        heladera.setActiva(false);
        EntityTransaction tx = entityManager().getTransaction();
        if (!tx.isActive())
          tx.begin();
        this.repoDeFallas.guardar(reporte);
        this.repositorioHeladeras.actualizar(heladera);
        tx.commit();
        Map<String, Object> model = new HashMap<>();
        model.put("redireccion", "/registroDeFallas");
        context.render("success.hbs",model);
      }catch (Exception e){
        logger.severe("Error al guardar el reporte: " + e.getMessage());
        context.status(500).result("Error al guardar el reporte");
        context.render("error.hbs");
      }
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
  }

