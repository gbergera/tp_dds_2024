package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.models.entities.colaboracion.*;
import ar.edu.utn.frba.dds.models.entities.colaboracion.TipoDeColaboraciones.*;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Comida;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.ModeloHeladera;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Vianda;
import ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta.SolicitudApertura;
import ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta.Tarjeta;
import ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta.TipoTarjeta;
import ar.edu.utn.frba.dds.models.entities.incidente.builder.ReporteDeIncidentesBuilder;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.NotificacionNuevoColaborador;
import ar.edu.utn.frba.dds.models.entities.persona.*;
import ar.edu.utn.frba.dds.models.entities.persona.builder.ColaboradorBuilder;
import ar.edu.utn.frba.dds.models.entities.puntos.CalculadorDePuntaje;
import ar.edu.utn.frba.dds.models.entities.puntos.Oferta;
import ar.edu.utn.frba.dds.models.entities.puntos.Rubro;
import ar.edu.utn.frba.dds.models.entities.puntos.TipoDeOferta;
import ar.edu.utn.frba.dds.services.sensores.*;
import ar.edu.utn.frba.dds.models.entities.ubicacion.DireccionAdaptada;
import ar.edu.utn.frba.dds.models.entities.ubicacion.GeocodingService;
import ar.edu.utn.frba.dds.models.entities.ubicacion.Ubicacion;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Localidad;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Municipio;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Provincia;
import ar.edu.utn.frba.dds.config.ServiceLocator;
import ar.edu.utn.frba.dds.models.entities.importacionArchivoCSV.ImportadorCSV;
import ar.edu.utn.frba.dds.models.entities.notificacion.Notificador;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.repositories.*;
import ar.edu.utn.frba.dds.server.utils.ICrudViewsHandler;
import ar.edu.utn.frba.dds.services.brokerHeladera.MqttConfig;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import io.javalin.http.Context;
import io.javalin.http.UploadedFile;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityTransaction;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ColaboracionController extends Controller implements ICrudViewsHandler, WithSimplePersistenceUnit {
    private ColaboracionRepository repoDeColaboraciones;
    private HeladeraRepository repoDeHeladeras;
    private ColaboradorRepository repoDeColaboradores;
    private GeneralRepository repoGeneral;
    private CalculadorDePuntaje calculadorDePuntaje;
    private SolicitudAperturaRepository repoDeSolicitudes;
    private ModeloHeladeraRepository repoDeModelos;
    private Logger logger = Logger.getLogger(ColaboracionController.class.getName());


    public ColaboracionController(ColaboracionRepository repoDeColaboraciones, HeladeraRepository repoDeHeladeras, ColaboradorRepository repoDeColaboradores, GeneralRepository repoGeneral, SolicitudAperturaRepository repoDeSolicitudes, ModeloHeladeraRepository repoDeModelos) {
        this.repoDeColaboraciones = repoDeColaboraciones;
        this.repoDeHeladeras = repoDeHeladeras;
        this.repoDeColaboradores = repoDeColaboradores;
        this.repoGeneral = repoGeneral;
        this.calculadorDePuntaje = new CalculadorDePuntaje();
        this.repoDeSolicitudes = repoDeSolicitudes;
        this.repoDeModelos = repoDeModelos;
    }

    @Override
    public void index(Context context) {

    }

    public void indexPuntos(Context context) {
        Map<String, Object> model = new HashMap<>();
        List <Oferta> ofertas = this.repoDeColaboraciones.buscarOfertas();
        List <Rubro> rubros = this.repoDeColaboraciones.buscarRubros();
        Long colaboradorId = Long.valueOf(Objects.requireNonNull(context.sessionAttribute("colaborador-id")));
        Colaborador colaborador = (Colaborador) this.repoDeColaboradores.buscar(colaboradorId);
        //CALCULA PUNTAJE CADA VEZ QUE ENTRA EL COLABORADOR A L APANTALLA DE PUNTOS
        /*this.calculadorDePuntaje.calcularPuntaje(colaborador);
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();
        this.repoDeColaboradores.actualizar(colaborador);
        tx.commit();*/
        model.put("colaborador", colaborador);
        model.put("ofertas", ofertas);
        model.put("rubros", rubros);
        model.put("puntosActive",true);

        TipoPersona tipoPersona = context.sessionAttribute("tipo-persona");
        model.put("isJuridica", tipoPersona == TipoPersona.JURIDICA);
        context.render("puntos.hbs", model);
    }

    @Override
    public void show(Context context) {

    }

    @Override
    public void create(Context context) {

    }

    @Override
    public void save(Context context) {
        logger.info("Se va a guardar la colaboracion: " + context.formParam("tipoColaboracion"));
        Long colaboradorId = Long.valueOf(Objects.requireNonNull(context.sessionAttribute("colaborador-id")));
        Colaborador colaborador = (Colaborador) this.repoDeColaboradores.buscar(colaboradorId);
        logger.info("Colaborador: " + colaborador);
        try {
            switch (context.formParam("tipoColaboracion")) {
                case "donacionDinero":
                   this.donacionDinero(context, colaborador);
                    break;
                case "donacionVianda":
                    this.donacionVianda(context, colaborador);
                    break;
                case "distribuirViandas":
                    this.distribuirViandas(context, colaborador);
                    break;
                case "encargarseHeladera":

                    this.encargarseHeladera(context, colaborador);
                    break;
                case "ofrecerProducto":
                    this.ofrecerProducto(context, colaborador);
                    break;
                case "registrarPersona":
                    this.registrarPersona(context, colaborador);
                    break;
                default:
                    context.status(400).result("Tipo de colaboración no válido: " +  context.formParam("tipoColaboracion"));
                    break;
            }
        } catch (IllegalArgumentException e) {
            logger.log(Level.SEVERE, "Valor no válido: " + e.getMessage(), e);
            context.status(400).result("Valor no válido: " + e);
            context.render("error.hbs");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al guardar la colaboración: " + e.getMessage(), e);
            context.status(500).result("Error al guardar la colaboración");
            context.render("error.hbs");
        }
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
    public void donacionDinero(Context context, Colaborador colaborador){
        DonacionDinero donacionDinero = new DonacionDinero();
        donacionDinero.setCantidad(Integer.valueOf(context.formParam("monto")));
        donacionDinero.setFrecuencia(Frecuencia.valueOf(context.formParam("frecuencia")));
        donacionDinero.setFechaColaboracion(LocalDateTime.now());
        donacionDinero.setFueExitosa(true);
        colaborador.agregarColaboracion(donacionDinero);
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();
        this.repoDeColaboradores.actualizar(colaborador);
        tx.commit();
        entityManager().refresh(colaborador);
        Map<String, Object> model = new HashMap<>();
        model.put("redireccion", "dashboard");
        context.render("success.hbs");
    }
    public void donacionVianda(Context context, Colaborador colaborador) throws MqttException {
        DonacionViandas donacionViandas = new DonacionViandas();
        Heladera heladera = (Heladera) this.repoDeHeladeras.buscarPorNombre(context.formParam("heladeraNombre"));

        // Hago la solicitud de apertura de heladera
        SolicitudApertura solicitudApertura = new SolicitudApertura();
        solicitudApertura.setFueExitosa(false);
        solicitudApertura.setHeladeraAAbrir(heladera);
        solicitudApertura.setFechaSolicitud(LocalDateTime.now());
        solicitudApertura.setTarjeta(colaborador.obtenerUltimaTarjeta());
        solicitudApertura.avisarSolicitud(colaborador.obtenerUltimaTarjeta().getCodigoAlfaNumerico(), heladera.getTopicMQTT());

        EntityTransaction tx1 = entityManager().getTransaction();
        if(!tx1.isActive())
            tx1.begin();
        this.repoDeSolicitudes.guardar(solicitudApertura);
        entityManager().flush();
        tx1.commit();
        entityManager().refresh(solicitudApertura);

        //Aviso a la heladera

        Comida comida = new Comida();
        comida.setNombre(context.formParam("nombreComida"));
        comida.setCalorias(Integer.valueOf(context.formParam("calorias")));
        comida.setPeso(Integer.valueOf(context.formParam("peso")));
        for(int i  = 0; i< Integer.valueOf(context.formParam("cantidad")); i ++){
            Vianda vianda = new Vianda();

            vianda.setComida(comida);
            vianda.setFechaCaducidad(LocalDate.parse(context.formParam("fechaCaducidad")));
            vianda.setFechaDonacion(LocalDate.now());

            heladera.agregarVianda(vianda);
            vianda.setHeladera(heladera);
            donacionViandas.agregarVianda(vianda);
            vianda.setColaborador(colaborador);
        }
        donacionViandas.setCantidad(Integer.valueOf(context.formParam("cantidad")));
        donacionViandas.setFechaColaboracion(LocalDateTime.now());
        donacionViandas.setFechaCaducidad(LocalDate.parse(context.formParam("fechaCaducidad")));
        //AGREGO SOLICITUD A LA DONACION
        donacionViandas.agregarSolicitud(solicitudApertura);
        donacionViandas.setFueExitosa(false);

        colaborador.agregarColaboracion(donacionViandas);
        colaborador.obtenerUltimaTarjeta().agregarHabilitacion(solicitudApertura);
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();
        this.repoDeHeladeras.actualizar(heladera);
        this.repoDeColaboradores.actualizar(colaborador);
        tx.commit();
        entityManager().refresh(heladera);
        entityManager().refresh(colaborador);
        Map<String, Object> model = new HashMap<>();
        model.put("redireccion", "dashboard");
        context.render("success.hbs");
    }

    public void distribuirViandas(Context context, Colaborador colaborador){
        RedistribucionViandas redistribucionViandas = new RedistribucionViandas();
        logger.info("Heladera origen: ");
        redistribucionViandas.setFechaDistribucionRealizada(LocalDate.now());
        redistribucionViandas.setFechaColaboracion(LocalDateTime.now());
        Heladera heladeraOrigen = (Heladera) this.repoDeHeladeras.buscarPorNombre(context.formParam("heladeraNombreOrigen"));
        redistribucionViandas.setHeladeraOrigen(heladeraOrigen);
        Heladera heladeraDestino = (Heladera) this.repoDeHeladeras.buscarPorNombre(context.formParam("heladeraNombreDestino"));
        redistribucionViandas.setHeladeraDestino(heladeraDestino);
        redistribucionViandas.setMotivo(context.formParam("motivo"));

        List<Vianda> viandasSeleccionadas = new ArrayList<>();
        List<String> viandasSeleccionadasIds = context.formParams("viandasSeleccionadas[]");
        logger.info("Viandas seleccionadas: " + viandasSeleccionadasIds);
        redistribucionViandas.setCantidad(viandasSeleccionadasIds.size());

        for (String viandaId : viandasSeleccionadasIds) {
            Vianda vianda = heladeraOrigen.getViandas().stream()
                    .filter(v -> v.getId().equals(Long.parseLong(viandaId)))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Vianda no encontrada en la heladera origen"));

            viandasSeleccionadas.add(vianda);
            logger.info("Vianda añadida con ID: " + vianda.getId());
        }


        for (Vianda vianda : viandasSeleccionadas) {
            logger.info("Moviendo vianda con ID: " + vianda.getId());
            vianda.setHeladera(heladeraDestino);
            heladeraOrigen.retirarVianda(vianda);
            heladeraDestino.agregarVianda(vianda);
        }
        logger.info("Viandas en heladera origen después de la transferencia: " + heladeraOrigen.getViandas());
        logger.info("Viandas en heladera destino después de la transferencia: " + heladeraDestino.getViandas());


        // Hago la solicitud de apertura de heladera
        SolicitudApertura solicitudAperturaOrigen = new SolicitudApertura();
        solicitudAperturaOrigen.setHeladeraAAbrir(heladeraOrigen);
        solicitudAperturaOrigen.setFechaSolicitud(LocalDateTime.now());
        solicitudAperturaOrigen.setTarjeta(colaborador.obtenerUltimaTarjeta());
        solicitudAperturaOrigen.avisarSolicitud(colaborador.obtenerUltimaTarjeta().getCodigoAlfaNumerico(), heladeraOrigen.getTopicMQTT());


        SolicitudApertura solicitudAperturaDestino = new SolicitudApertura();
        solicitudAperturaDestino.setHeladeraAAbrir(heladeraDestino);
        solicitudAperturaDestino.setFechaSolicitud(LocalDateTime.now());
        solicitudAperturaDestino.setTarjeta(colaborador.obtenerUltimaTarjeta());
        solicitudAperturaDestino.avisarSolicitud(colaborador.obtenerUltimaTarjeta().getCodigoAlfaNumerico(), heladeraDestino.getTopicMQTT());

        //AGREGO SOLICITUD A LA DONACION
        redistribucionViandas.agregarSolicitud(solicitudAperturaDestino);
        redistribucionViandas.agregarSolicitud(solicitudAperturaOrigen);
        redistribucionViandas.setFueExitosa(false);

        colaborador.agregarColaboracion(redistribucionViandas);
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();
        this.repoDeSolicitudes.guardar(solicitudAperturaOrigen);
        this.repoDeSolicitudes.guardar(solicitudAperturaDestino);
        this.repoDeColaboradores.actualizar(colaborador);
        tx.commit();
        entityManager().refresh(solicitudAperturaDestino);
        entityManager().refresh(solicitudAperturaOrigen);
        entityManager().refresh(colaborador);

        Map<String, Object> model = new HashMap<>();
        model.put("redireccion", "dashboard");
        context.render("success.hbs", model);
    }

    public void encargarseHeladera(Context context, Colaborador colaborador) throws MqttException {
        String nombreHeladera = context.formParam("nombreHeladera");
        String topicMQTT = context.formParam("topicMQTT");

        HacerseCargoHeladera encargarseHeladera = new HacerseCargoHeladera();
        encargarseHeladera.setFechaColaboracion(LocalDateTime.now());
        Heladera nuevaHeladera = new Heladera();
        nuevaHeladera.setNombre(nombreHeladera);
        nuevaHeladera.setActiva(true);
        nuevaHeladera.setFechaInicioFuncionamiento(LocalDate.now());
        nuevaHeladera.setAdministrador(colaborador);
        nuevaHeladera.setCapacidad(Integer.valueOf(context.formParam("capacidad")));
        nuevaHeladera.setTopicMQTT(topicMQTT);

        ModeloHeladera modeloHeladera = (ModeloHeladera) this.repoDeModelos.buscarPorNombre(context.formParam("modeloHeladera"));
        nuevaHeladera.setModelo(modeloHeladera);

        nuevaHeladera.setTemperaturaMinima(modeloHeladera.getTemperaturaMinima());
        nuevaHeladera.setTemperaturaMaxima(modeloHeladera.getTemperaturaMaxima());





        DireccionAdaptada direccion = new DireccionAdaptada();
        direccion.setCalle(context.formParam("direccion.calle"));
        direccion.setNumero(Integer.valueOf(context.formParam("direccion.numero")));


        List <Heladera> heladerasCercanas = this.repoDeHeladeras.buscarPorLocalidad(context.formParam("direccion.localidad"));
        for(Heladera h: heladerasCercanas){
            nuevaHeladera.agregarHeladeraCercana(h);
        }
        Provincia provincia = (Provincia) repoGeneral.controlarSiExisteProvincia(context.formParam("direccion.provincia"));
        if(provincia == null){
            provincia = new Provincia();
            provincia.setNombre(context.formParam("direccion.provincia"));
        }

        Municipio municipio = (Municipio) repoGeneral.controlarSiExisteMunicipio(context.formParam("direccion.municipio"));
        logger.info("Comparo: " + context.formParam("municipio") + " y " + municipio);
        if (municipio == null) {
            municipio = new Municipio();  // Crear una nueva instancia de Municipio si no existe
            municipio.setNombre(context.formParam("direccion.municipio"));
            municipio.setProvincia(provincia);
        }

        Localidad localidad = (Localidad) repoGeneral.controlarSiExisteLocalidad(context.formParam("direccion.localidad"));
        if(localidad == null){
            localidad = new Localidad();
            localidad.setNombre(context.formParam("direccion.localidad"));
            localidad.setMunicipio(municipio);
        }

        Ubicacion ubicacion = new Ubicacion();

        String fullAddress = direccion.getCalle() + " " + direccion.getNumero() + ", " + localidad.getNombre() + ", " + municipio.getNombre() + ", " + provincia.getNombre();
        try {
            double[] coordinates = GeocodingService.getCoordinates(fullAddress);
            ubicacion.setLatitud((float) coordinates[0]);
            ubicacion.setLongitud((float) coordinates[1]);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al obtener coordenadas: " + e.getMessage(), e);
        }


        direccion.setLocalidad(localidad);
        nuevaHeladera.cambiarDireccion(direccion);
        nuevaHeladera.cambiarUbicacion(ubicacion);




        encargarseHeladera.agregarHeladera(nuevaHeladera);
        encargarseHeladera.setFueExitosa(true);
        colaborador.agregarColaboracion(encargarseHeladera);
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();
        this.repoDeColaboradores.actualizar(colaborador);
        tx.commit();
        entityManager().refresh(colaborador);

        //Creo sensores
        Heladera heladeraRepo = (Heladera) this.repoDeHeladeras.buscarPorNombre(nuevaHeladera.getNombre());
        this.crearSensor(heladeraRepo, "Temperatura");
        this.crearSensor(heladeraRepo, "Movimiento");
        Map<String, Object> model = new HashMap<>();
        model.put("redireccion", "dashboard");
        context.render("success.hbs");
    }


    public void ofrecerProducto(Context context, Colaborador colaborador) {
        // Crea la carpeta para almacenar imágenes si no existe
        String uploadDir = "upload/imagenes"; // Cambia esta ruta según sea necesario
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs(); // Crea la carpeta si no existe
        }

        // Obtiene la imagen del formulario
        UploadedFile imageFile = context.uploadedFile("imagen"); // El nombre debe coincidir con el atributo 'name' del input de archivo

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
                logger.severe("Error al guardar la imagen: " + e.getMessage());
                context.status(500).result("Error al guardar la imagen: " + e.getMessage());
                return; // Termina el método si hay un error
            }

        } else {
            // No se subió ningún archivo válido
           logger.info("No se subió ninguna imagen válida.");
        }

        // Crea un nuevo objeto OfrecerProductos
        OfrecerProductos ofrecerProductos = new OfrecerProductos();
        ofrecerProductos.setFechaColaboracion(LocalDateTime.now());
        ofrecerProductos.setFueExitosa(true);

        // Crea una nueva oferta
        Oferta nuevaOferta = new Oferta();
        nuevaOferta.setNombre(context.formParam("nombre"));
        nuevaOferta.setPuntos(Double.valueOf(context.formParam("puntos")));
        nuevaOferta.setDetalle(context.formParam("descripcion"));
        nuevaOferta.setImagen(imagePath); // Establece el nombre de la imagen (puede ser null si no se subió)
        // Verifica si el rubro ya existe
        Rubro rubro = (Rubro) repoGeneral.controlarSiExisteRubro(context.formParam("rubro"));
        if (rubro == null) {
            rubro = new Rubro();
            rubro.setDescripcion(context.formParam("rubro"));
        }
        nuevaOferta.setRubro(rubro);
        nuevaOferta.setTipoOferta(TipoDeOferta.valueOf(context.formParam("tipo")));

        // Agrega la nueva oferta a las colaboraciones
        ofrecerProductos.agregarOferta(nuevaOferta);
        colaborador.agregarColaboracion(ofrecerProductos);
        nuevaOferta.setColaborador(colaborador);
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();
        this.repoDeColaboradores.actualizar(colaborador);
        tx.commit();
        entityManager().refresh(colaborador);
        Map<String, Object> model = new HashMap<>();
        model.put("redireccion", "dashboard");
        context.render("success.hbs");
    }

    public void registrarPersona(Context context, Colaborador colaborador) {
        EntregaTarjetas entregaTarjetas = new EntregaTarjetas();
        PersonaSituacionVulnerable persona = new PersonaSituacionVulnerable();
        persona.setNombre(context.formParam("nombre"));
        persona.setApellido(context.formParam("apellido"));
        persona.setFechaRegistro(LocalDateTime.now());
        persona.setFechaNacimiento(LocalDate.parse(context.formParam("fechaNacimiento")));
        persona.setSituacion(Situacion.valueOf(context.formParam("situacion")));

        persona.setColaboradorResponsable(colaborador);

        // Si la situación es CON_DOMICILIO, procesar la dirección
        if (Situacion.valueOf(context.formParam("situacion")).equals(Situacion.CON_DOMICILIO)) {
            DireccionAdaptada direccion = new DireccionAdaptada();
            direccion.setCalle(context.formParam("calle"));
            direccion.setNumero(Integer.valueOf(context.formParam("numero")));

            Provincia provincia = (Provincia) repoGeneral.controlarSiExisteProvincia(context.formParam("provincia"));
            if(provincia == null){
                provincia = new Provincia();
                provincia.setNombre(context.formParam("provincia"));
            }

            Municipio municipio = (Municipio) repoGeneral.controlarSiExisteMunicipio(context.formParam("municipio"));
            logger.info("Comparo: " + context.formParam("municipio") + " y " + municipio);
            if (municipio == null) {
                municipio = new Municipio();  // Crear una nueva instancia de Municipio si no existe
                municipio.setNombre(context.formParam("municipio"));
                municipio.setProvincia(provincia);
            }



            Localidad localidad = (Localidad) repoGeneral.controlarSiExisteLocalidad(context.formParam("localidad"));
            if(localidad == null){
                localidad = new Localidad();
                localidad.setNombre(context.formParam("localidad"));
                localidad.setMunicipio(municipio);
            }

            direccion.setLocalidad(localidad);

            persona.setDireccion(direccion);
        }

        // Procesar el documento si no es NADA
        if (TipoDocumento.valueOf(context.formParam("tipoDocumento")) != TipoDocumento.NADA) {
            Documento documento = new Documento();
            documento.setTipoDocumento(TipoDocumento.valueOf(context.formParam("tipoDocumento")));
            documento.setNumeroDocumento((context.formParam("nroDoc")));
            persona.setDocumento(documento);
        }

        // Procesar la lista de menores de edad
        List<String> nombresMenores = context.formParams("menoresNombres[]");
        List<String> edadesMenores = context.formParams("menoresEdades[]");

        if (nombresMenores != null && edadesMenores != null) {
            List<Hijo> hijos = new ArrayList<>();
            for (int i = 0; i < nombresMenores.size(); i++) {
                Hijo hijo = new Hijo();
                hijo.setNombre(nombresMenores.get(i));
                hijo.setApellido(context.formParam("apellido"));
                hijo.setFechaNacimiento(LocalDate.parse(edadesMenores.get(i)));
                hijos.add(hijo);
            }
            persona.setHijos(hijos);
        }

        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setPersona(persona);
        tarjeta.setTipoTarjeta(TipoTarjeta.PERSONA_VULNERABLE);
        tarjeta.setFechaInicioDeUso(LocalDate.now());
        tarjeta.setEstaHabilitada(true);
        tarjeta.setRepartidorDeTarjeta(colaborador);
        persona.agregarTarjeta(tarjeta);

        entregaTarjetas.AgregarTarjeta(tarjeta);
        entregaTarjetas.setFechaColaboracion(LocalDateTime.now());
        entregaTarjetas.setCantidad(1);
        entregaTarjetas.setFueExitosa(true);
        colaborador.agregarColaboracion(entregaTarjetas);
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();
        this.repoDeColaboradores.actualizar(colaborador);
        entityManager().flush();
        tx.commit();
        entityManager().refresh(colaborador);
        Map<String, Object> model = new HashMap<>();
        model.put("redireccion", "dashboard");
        context.render("success.hbs");
    }


    public String[] parseAdress(String address){
        String[] parts = address.split(", ");

        // La parte de la calle y el número está en la primera parte
        String streetAndNumber = parts[0];

        // Extraer el número usando expresiones regulares
        String number = streetAndNumber.replaceAll("[^0-9]", "").trim(); // Extrae solo el número
        String street = streetAndNumber.substring(0, streetAndNumber.lastIndexOf(" " + number)).trim(); // Calle es el resto
        String neighborhood = parts[1].trim(); // Barrio es la segunda parte
        String city = parts[2].trim(); // Ciudad es la tercera parte

        return new String[]{street, number, neighborhood, city};
    }

    public void indexCompras(Context context) {
        Map<String, Object> model = new HashMap<>();
        Long colaboradorId = Long.valueOf(Objects.requireNonNull(context.sessionAttribute("colaborador-id")));
        Colaborador colaborador = (Colaborador) this.repoDeColaboradores.buscar(colaboradorId);
        model.put("colaborador", colaborador);
        model.put("puntosActive",true);
        TipoPersona tipoPersona = context.sessionAttribute("tipo-persona");
        model.put("isJuridica", tipoPersona == TipoPersona.JURIDICA);
        context.render("miscompras.hbs", model);
    }
    public void misOfertas(Context context) {
        Map<String, Object> model = new HashMap<>();
        Long colaboradorId = Long.valueOf(Objects.requireNonNull(context.sessionAttribute("colaborador-id")));
        List <Oferta> ofertas = repoDeColaboraciones.buscarOfertasPorColaborador(colaboradorId);
        logger.info("Ofertas: " + ofertas);
        model.put("misofertas", ofertas);
        model.put("puntosActive",true);
        TipoPersona tipoPersona = context.sessionAttribute("tipo-persona");
        model.put("isJuridica", tipoPersona == TipoPersona.JURIDICA);
        context.render("misofertas.hbs", model);
    }


    public void crearSensor(Heladera heladera, String tipo) throws MqttException {
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        connOpts.setKeepAliveInterval(60);
        connOpts.setAutomaticReconnect(true);
        switch (tipo){
            case "Temperatura":
                MqttClient cliente = new MqttClient(MqttConfig.broker, "heladera" + heladera.getNombre() + "temperatura", new MemoryPersistence());
                SensorTemperatura sensorTemperatura = new SensorTemperatura();
                ReceptorSensorTemperatura receptorTemperatura = new ReceptorSensorTemperatura(ServiceLocator.instanceOf(ReporteDeIncidentesBuilder.class), ServiceLocator.instanceOf(RegistroDeFallaRepository.class), ServiceLocator.instanceOf(HeladeraRepository.class), ServiceLocator.instanceOf(TipoIncidenteRepository.class), ServiceLocator.instanceOf(SuscripcionRepository.class));
                receptorTemperatura.setHeladera(heladera);
                sensorTemperatura.setReceptorSensorTemperatura(receptorTemperatura);
                cliente.setCallback(sensorTemperatura);
                cliente.connect(connOpts);

                instanciarCron(receptorTemperatura);

                if (cliente.isConnected()) {
                    logger.info("CONECTADO EL SENSOR DE TEMPERATURA");
                    cliente.subscribe(heladera.getTopicMQTT() + "temperatura", MqttConfig.qos);
                    logger.info("SE SUSCRIBIO EL SENSOR DE TEMPERATURA");

                }
                break;
            case "Movimiento":
                logger.info("Cliente : " + "heladera" + heladera.getId() + "movimiento");
                MqttClient cliente2 = new MqttClient(MqttConfig.broker, "heladera" + heladera.getNombre() + "movimiento", new MemoryPersistence());

                SensorMovimiento sensorMovimiento = new SensorMovimiento();
                ReceptorSensorMovimiento receptorMovimiento = new ReceptorSensorMovimiento(ServiceLocator.instanceOf(ReporteDeIncidentesBuilder.class), ServiceLocator.instanceOf(RegistroDeFallaRepository.class), ServiceLocator.instanceOf(HeladeraRepository.class), ServiceLocator.instanceOf(TipoIncidenteRepository.class));
                receptorMovimiento.setHeladera(heladera);
                sensorMovimiento.setReceptorSensorMovimiento(receptorMovimiento);
                cliente2.setCallback(sensorMovimiento);
                cliente2.connect(connOpts);
                if (cliente2.isConnected()) {
                    logger.info("CONECTADO EL SENSOR DE MOVIMIENTO");
                    cliente2.subscribe(heladera.getTopicMQTT() + "movimiento", MqttConfig.qos);
                    logger.info("SE SUSCRIBIO EL SENSOR DE MOVIMIENTO");

                }
                break;


            default:
        }

    }

    public void instanciarCron(ReceptorSensorTemperatura receptor){
        FallaConexion cron = new FallaConexion(receptor, ServiceLocator.instanceOf(ReporteDeIncidentesBuilder.class), ServiceLocator.instanceOf(TipoIncidenteRepository.class), ServiceLocator.instanceOf(RegistroDeFallaRepository.class),ServiceLocator.instanceOf(HeladeraRepository.class));

        cron.iniciarCron();
    }

    public void validarNombreHeladera(@NotNull Context context) {
        String nombreHeladera = context.queryParam("nombre");
        boolean exists = this.repoGeneral.contraloarSiExisteHeladeraPorNombre(nombreHeladera);
        context.json(Map.of("exists", exists));
    }

    public void validarTopicMQTT(@NotNull Context context) {
        String topicMqtt = context.queryParam("topic");
        boolean exists = this.repoGeneral.controlarSiExisteTopicMQTTDeHeladera(topicMqtt);
        context.json(Map.of("exists", exists));
    }
}
