package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.config.ServiceLocator;
import ar.edu.utn.frba.dds.converters.MetodoDeNotificacionConverter;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.contrasenia.ValidacionContraseniaException;
import ar.edu.utn.frba.dds.models.entities.contrasenia.ValidadorContrasenia;
import ar.edu.utn.frba.dds.models.entities.formulario.Formulario;
import ar.edu.utn.frba.dds.models.entities.formulario.Opcion;
import ar.edu.utn.frba.dds.models.entities.formulario.Pregunta;
import ar.edu.utn.frba.dds.models.entities.formulario.TipoDePregunta;
import ar.edu.utn.frba.dds.models.entities.incidente.ReporteDeIncidentes;
import ar.edu.utn.frba.dds.models.entities.importacionArchivoCSV.ImportadorCSV;
import ar.edu.utn.frba.dds.models.entities.notificacion.MetodoDeNotificacion;
import ar.edu.utn.frba.dds.models.entities.notificacion.Notificador;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.NotificacionNuevoColaborador;
import ar.edu.utn.frba.dds.models.entities.persona.*;
import ar.edu.utn.frba.dds.models.entities.persona.builder.ColaboradorBuilder;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Localidad;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Municipio;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Provincia;
import ar.edu.utn.frba.dds.models.repositories.*;
import ar.edu.utn.frba.dds.server.utils.ICrudViewsHandler;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import io.javalin.http.UploadedFile;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.EntityTransaction;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminController implements ICrudViewsHandler, WithSimplePersistenceUnit {
    private UsuarioRepository usuarioRepository;
    private HeladeraRepository heladeraRepository;
    private ColaboradorRepository colaboradorRepository;
    private FormularioRepository formularioRepository;
    private TecnicoRepository tecnicoRepository;
    private GeneralRepository generalRepo;
    private VisitasRepository repoDeVisitas;
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private Logger logger = Logger.getLogger(AdminController.class.getName());

    public AdminController(UsuarioRepository usuarioRepository, HeladeraRepository heladeraRepository, ColaboradorRepository colaboradorRepository, FormularioRepository formularioRepository, TecnicoRepository tecnicoRepository, GeneralRepository generalRepo, VisitasRepository repoDeVisitas) {
        this.usuarioRepository = usuarioRepository;
        this.heladeraRepository = heladeraRepository;
        this.colaboradorRepository = colaboradorRepository;
        this.formularioRepository = formularioRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.generalRepo = generalRepo;
        this.repoDeVisitas = repoDeVisitas;
    }

    @Override
    public void index(Context context) {
        Map<String, Object> model = new HashMap<>();
        model.put("usuarios", usuarioRepository.buscarTodos());
        model.put("colaboradores", colaboradorRepository.buscarTodos());
        model.put("heladeras", heladeraRepository.buscarTodos());
        model.put("formularios", formularioRepository.buscarTodosFormularios());
        context.render("admin/usuarios.hbs", model);
    }

    public void saveFormulario(Context context) {
        Formulario formulario = new Formulario();
        formulario.setNombre(context.formParam("nombre"));
        formulario.setFechaFormulario(LocalDateTime.now());

        int questionCount = Integer.parseInt(Objects.requireNonNull(context.formParam("questionCount")));
        for (int i = 1; i <= questionCount; i++) {
            Pregunta pregunta = new Pregunta();
            pregunta.setDetalle(context.formParam("pregunta" + i + ".texto"));

            String tipoPregunta = context.formParam("pregunta" + i + ".tipo");
            if (Objects.equals(tipoPregunta, "single_choice")) {
                pregunta.setTipoDePregunta(TipoDePregunta.SINGLE_CHOICE);
                List<String> opciones = context.formParams("pregunta" + i + ".opciones[]");
                for (String opcionTexto : opciones) {
                    Opcion opcion = new Opcion();
                    opcion.setDetalle(opcionTexto);
                    opcion.setEsCorrecta(false); // Adjust according to your logic
                    pregunta.agregarOpcion(opcion);
                }
            } else if (Objects.equals(tipoPregunta, "open_ended")) {
                pregunta.setTipoDePregunta(TipoDePregunta.DESARROLLO);
            }


            formulario.agregarItem(pregunta);
        }
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();
        formularioRepository.guardar(formulario);
        tx.commit();
        entityManager().refresh(formulario);
        context.redirect("/administrador/formularios");
    }

    public void deleteFormulario(Context context) {
        Long id = Long.parseLong(context.pathParam("id"));
        formularioRepository.eliminarPorId(id);
        context.redirect("/administrador/formularios");
    }

    @Override
    public void show(Context context) {

    }

    @Override
    public void create(Context context) {

    }

    @Override
    public void save(Context context) {

    }

    @Override
    public void edit(Context context) {

    }

    @Override
    public void update(Context context) {

    }

    @Override
    public void delete(Context context) {

    }

    public void deleteUsuario(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        Usuario usu = (Usuario) usuarioRepository.buscar(id);
        if (usu != null) {
            usuarioRepository.darDeBaja(usu);
        }
        ctx.redirect("/administrador/usuarios");
    }

    public void deleteColaborador(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        Colaborador col = (Colaborador) colaboradorRepository.buscar(id);
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();
        colaboradorRepository.darDeBaja(col);
        tx.commit();
        ctx.redirect("/administrador/colaboradores");
    }

    public void deleteHeladera(Context ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        Heladera hel = (Heladera) heladeraRepository.buscar(id);
        heladeraRepository.darDeBaja(hel);
        ctx.redirect("/administrador/heladeras");
    }

    public void usuarios(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("usuarios", usuarioRepository.buscarTodos());
        ctx.render("admin/usuarios.hbs", model);
    }

    public void colaboradores(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("colaboradores", colaboradorRepository.buscarTodos());
        ctx.render("admin/colaboradores.hbs", model);
    }

    public void heladeras(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("heladeras", heladeraRepository.buscarTodos());
        ctx.render("admin/heladeras.hbs", model);
    }

    public void formularios(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("formularios", formularioRepository.buscarTodosFormularios());
        ctx.render("admin/formularios.hbs", model);
    }

    public void tecnicos(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("tecnicos", tecnicoRepository.buscarTodos());
        ctx.render("admin/tecnicos.hbs", model);
    }
    public void alertas(Context ctx) {
        try {
            List<Heladera> heladeras = heladeraRepository.buscarTodos();
            List<Map<String, Object>> alertasRelevantes = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

            for (Heladera heladera : heladeras) {
                List<ReporteDeIncidentes> incidentes = heladera.getRegistroDeFallos();

                for (ReporteDeIncidentes incidente : incidentes) {
                    // Verificar si el incidente no es null y tiene un incidente asociado
                    if (incidente != null && incidente.getIncidente() != null) {
                        String descripcion = incidente.getIncidente().getDescripcion();
                        // Verificar la descripción antes de compararla
                        if ("ALERTA DE CONEXION".equals(descripcion) ||
                            "ALERTA DE TEMPERATURA".equals(descripcion) ||
                            "ALERTA DE FRAUDE".equals(descripcion)) {

                            Map<String, Object> alerta = new HashMap<>();
                            alerta.put("tipo", descripcion);

                            // Formatear la fecha y hora del incidente
                            String fechaHoraActual = incidente.getFechaYHora().format(formatter);
                            alerta.put("fechaYHora", fechaHoraActual);
                            alerta.put("nombreHeladera", heladera.getNombre());
                            alerta.put("ubicacionHeladera", heladera.direccionActual().direccionCompleta());

                            alertasRelevantes.add(alerta);
                        }
                    }
                }
            }

            // Ordenar las alertas por fechaYHora de más reciente a menos reciente
            alertasRelevantes.sort((a, b) -> {
                LocalDateTime fechaA = LocalDateTime.parse((String) a.get("fechaYHora"), formatter);
                LocalDateTime fechaB = LocalDateTime.parse((String) b.get("fechaYHora"), formatter);
                return fechaB.compareTo(fechaA); // Orden descendente
            });

            Map<String, Object> model = new HashMap<>();
            TipoPersona tipoPersona = ctx.sessionAttribute("tipo-persona");
            model.put("isFisica", tipoPersona == TipoPersona.FISICA);
            model.put("isJuridica", tipoPersona == TipoPersona.JURIDICA);
            model.put("alertasActive", true);
            model.put("alertas", alertasRelevantes);
            // Renderizar la vista con las alertas ordenadas
            ctx.render("admin/alertas.hbs", model);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al mostrar las alertas: " + e.getMessage(), e);
            ctx.status(500).result("Error al mostrar las alertas: " + e.getMessage());
        }
    }

    public void saveTecnico(Context ctx) {
        try {
            ValidadorContrasenia validador = ServiceLocator.instanceOf(ValidadorContrasenia.class);

            Map<String, Object> model = new HashMap<>();

            if (this.usuarioRepository.buscarPorNombre( ctx.formParam("usuario")) != null) {
                model.put("error", "El nombre de usuario ya existe, por favor eliga otro");
                model.put("tecnicos", tecnicoRepository.buscarTodos());
                ctx.render("admin/tecnicos.hbs", model);
                return;
            }


            try {
                validador.validar( ctx.formParam("usuario"), ctx.formParam("password"));
            } catch (ValidacionContraseniaException e) {
                logger.log(Level.SEVERE, "Error al crear técnico: " + e.getMessage(), e);
                model.put("error", e.getMessage());
                model.put("tecnicos", tecnicoRepository.buscarTodos());
                ctx.render("admin/tecnicos.hbs", model);
                return;
            }

            if(!Objects.equals(ctx.formParam("password"), ctx.formParam("check-password"))){
                model.put("error", "Las contraseñas no coinciden");
                model.put("tecnicos", tecnicoRepository.buscarTodos());
                ctx.render("admin/tecnicos.hbs", model);
            }

            else {
                Tecnico tecnico = new Tecnico();
                tecnico.setNombre(ctx.formParam("nombre"));
                tecnico.setApellido(ctx.formParam("apellido"));
                tecnico.setTelefono(ctx.formParam("telefono"));
                tecnico.setMail(ctx.formParam("mail"));
                tecnico.setChatTelegramId(ctx.formParam("chatTelegramId"));
                tecnico.setUsuarioTelegram(ctx.formParam("usuarioTelegram"));
                String metodoNotificacionStr = ctx.formParam("metodoDeNotificacion");
                MetodoDeNotificacionConverter converter = new MetodoDeNotificacionConverter();
                tecnico.setMetodoDeNotificacion(converter.convertToEntityAttribute(metodoNotificacionStr));
                tecnico.setTipoPersona(TipoPersona.TECNICO);
                Usuario usuario = new Usuario();
                usuario.setNombreUsuario(ctx.formParam("usuario"));
                String bCryptedPassword = bCryptPasswordEncoder.encode(ctx.formParam("password"));
                usuario.setContrasenia(bCryptedPassword);
                usuario.setEstado(true);
                tecnico.setUsuario(usuario);
                this.usuarioRepository.guardar(usuario);

                String provinciaNombre = ctx.formParam("direccion.provincia");
                Provincia provincia = (Provincia) generalRepo.controlarSiExisteProvincia(provinciaNombre);
                if (provincia == null) {
                    provincia = new Provincia();
                    provincia.setNombre(provinciaNombre);
                }

                String municipioNombre = ctx.formParam("direccion.municipio");
                Municipio municipio = (Municipio) generalRepo.controlarSiExisteMunicipio(municipioNombre);
                if (municipio == null) {
                    municipio = new Municipio();
                    municipio.setNombre(municipioNombre);
                    municipio.setProvincia(provincia);
                }

                String localidadNombre = ctx.formParam("direccion.localidad");
                Localidad localidad = (Localidad) generalRepo.controlarSiExisteLocalidad(localidadNombre);
                if (localidad == null) {
                    localidad = new Localidad();
                    localidad.setNombre(localidadNombre);
                    localidad.setMunicipio(municipio);
                }
                tecnico.setAreaCobertura(localidad);

                EntityTransaction tx = entityManager().getTransaction();
                if(!tx.isActive())
                    tx.begin();
                this.tecnicoRepository.guardar(tecnico);
                tx.commit();
                entityManager().refresh(tecnico);
                ctx.status(HttpStatus.CREATED);
                model.put("redireccion", "/administrador/tecnicos");
                ctx.render("success.hbs", model);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al crear técnico: " + e.getMessage(), e);
            ctx.status(500).result("Error al crear técnico");
        }
    }

    public void tarjetas(@NotNull Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("tarjetas", tecnicoRepository.buscarTodasLasTarjetas());
        ctx.render("admin/tarjetas.hbs", model);
    }

    public void saveCsv(Context context) {
        logger.info("ENTRE AL METODO SAVE");

        UploadedFile archivo = context.uploadedFile("archivo");

        if (archivo != null) {
            try {
                logger.info("ENTRE AL ARCHIVO");
                // Save the uploaded file to a temporary location
                Path tempFile = Files.createTempFile("uploaded-", ".csv");
                try (InputStream inputStream = archivo.content()) {
                    Files.copy(inputStream, tempFile, StandardCopyOption.REPLACE_EXISTING);
                }

                // Procesar el archivo en ASYNC
                CompletableFuture.runAsync(() -> {
                    try {
                        ImportadorCSV importadorCSV = ServiceLocator.instanceOf(ImportadorCSV.class);
                        importadorCSV.cargarCSV(tempFile.toString(), ServiceLocator.instanceOf(Notificador.class), ServiceLocator.instanceOf(NotificacionNuevoColaborador.class), ServiceLocator.instanceOf(ColaboradorBuilder.class), ServiceLocator.instanceOf(ColaboradorRepository.class), ServiceLocator.instanceOf(UsuarioRepository.class));
                        logger.info("SALI DEL ARCHIVO");
                    } catch ( Exception e) {
                        logger.log(Level.SEVERE, "Error al procesar el archivo CSV: " + e.getMessage(), e);
                    }
                });
                Map<String, Object> model = new HashMap<>();
                model.put("redireccion", "/administrador/importadorCsv");
                model.put("mensaje", "El archivo se está procesando. Al finalizar, se notificará por correo a los colaboradores.");
                context.render("success.hbs", model);
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error al procesar el archivo CSV: " + e.getMessage(), e);
                context.result("Error al procesar el archivo.");
            }
        } else {
            context.result("No se ha seleccionado ningún archivo CSV.");
        }
    }
    public void visitas(Context context) {
        Map<String, Object> model = new HashMap<>();
        model.put("visitas", this.repoDeVisitas.buscarTodos());
        model.put("isAdmin", true);
        context.render("visitasMisHeladeras.hbs", model );
    }
}
