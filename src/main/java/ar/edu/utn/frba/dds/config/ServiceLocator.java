package ar.edu.utn.frba.dds.config;

import ar.edu.utn.frba.dds.controllers.*;
import ar.edu.utn.frba.dds.models.entities.contrasenia.ValidacionContraseniaDebil;
import ar.edu.utn.frba.dds.models.entities.contrasenia.ValidacionDiferenteAlNombre;
import ar.edu.utn.frba.dds.models.entities.contrasenia.ValidacionLongitud;
import ar.edu.utn.frba.dds.models.entities.contrasenia.ValidadorContrasenia;
import ar.edu.utn.frba.dds.models.entities.importacionArchivoCSV.ImportadorCSV;
import ar.edu.utn.frba.dds.models.entities.incidente.builder.ReporteDeIncidentesBuilder;
import ar.edu.utn.frba.dds.models.entities.notificacion.Notificador;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.NotificacionBuilder;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.NotificacionNuevoColaborador;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.NotificacionTecnico;
import ar.edu.utn.frba.dds.models.entities.persona.builder.ColaboradorBuilder;
import ar.edu.utn.frba.dds.models.entities.persona.builder.IColaboradorBuilder;
import ar.edu.utn.frba.dds.models.entities.puntos.CalculadorDePuntaje;
import ar.edu.utn.frba.dds.models.entities.reportes.AdapterApachePDF;
import ar.edu.utn.frba.dds.models.entities.reportes.ExportadorAPDF;
import ar.edu.utn.frba.dds.models.entities.reportes.GeneradorDeReportes;
import ar.edu.utn.frba.dds.services.sensores.ReceptorSensorMovimiento;
import ar.edu.utn.frba.dds.services.sensores.ReceptorSensorTemperatura;
import ar.edu.utn.frba.dds.models.repositories.*;
import ar.edu.utn.frba.dds.services.ServicioApertura;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ServiceLocator {
    private static Map<String, Object> instances = new HashMap<>();


    public static <T> T instanceOf(Class<T> componentClass) {
        String componentName = componentClass.getName();

        if (!instances.containsKey(componentName)) {
            if (componentName.equals(UsuarioController.class.getName())) {
                UsuarioController instance = new UsuarioController(instanceOf(UsuarioRepository.class), instanceOf(ColaboradorRepository.class), instanceOf(TecnicoRepository.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(ColaboradorController.class.getName())) {
                ColaboradorController instance = new ColaboradorController(instanceOf(ColaboradorRepository.class), instanceOf(UsuarioRepository.class), instanceOf(FormularioRepository.class), instanceOf(ColaboracionRepository.class), instanceOf(GeneralRepository.class));
                instances.put(componentName, instance);
            }else if (componentName.equals(SuscripcionesController.class.getName())) {
                SuscripcionesController instance = new SuscripcionesController(instanceOf(SuscripcionRepository.class),(instanceOf(ColaboradorRepository.class)),(instanceOf(HeladeraRepository.class)));
                instances.put(componentName, instance);
            } else if (componentName.equals(RegistroDeFallaController.class.getName())) {
                RegistroDeFallaController instance = new RegistroDeFallaController(instanceOf(RegistroDeFallaRepository.class),instanceOf(TipoIncidenteRepository.class),instanceOf(ColaboradorRepository.class),instanceOf(HeladeraRepository.class),instanceOf(TipoIncidenteRepository.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(HeladeraController.class.getName())) {
                HeladeraController instance = new HeladeraController(instanceOf(HeladeraRepository.class),instanceOf(AlertaRepository.class), instanceOf(ModeloHeladeraRepository.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(TecnicoController.class.getName())) {
                TecnicoController instance = new TecnicoController(instanceOf(TecnicoRepository.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(ColaboracionController.class.getName())) {
                ColaboracionController instance = new ColaboracionController(instanceOf(ColaboracionRepository.class), instanceOf(HeladeraRepository.class), instanceOf(ColaboradorRepository.class), instanceOf(GeneralRepository.class), instanceOf(SolicitudAperturaRepository.class), instanceOf(ModeloHeladeraRepository.class));
                instances.put(componentName, instance);
            }  else if (componentName.equals(AdminController.class.getName())) {
                AdminController instance = new AdminController(instanceOf(UsuarioRepository.class), instanceOf(HeladeraRepository.class), instanceOf(ColaboradorRepository.class), instanceOf(FormularioRepository.class), instanceOf(TecnicoRepository.class), instanceOf(GeneralRepository.class), instanceOf(VisitasRepository.class));
                instances.put(componentName, instance);
            }else if (componentName.equals(PersonaVulnerableController.class.getName())) {
                PersonaVulnerableController instance = new PersonaVulnerableController(instanceOf(PersonaVulnerableRepository.class), instanceOf(ColaboradorRepository.class));
                instances.put(componentName, instance);
            }else if (componentName.equals(VisitasController.class.getName())) {
                VisitasController instance = new VisitasController(instanceOf(VisitasRepository.class), instanceOf(ColaboradorRepository.class));
                instances.put(componentName, instance);
            }else if (componentName.equals(TipoIncidenteController.class.getName())) {
                TipoIncidenteController instance = new TipoIncidenteController(instanceOf(TipoIncidenteRepository.class));
                instances.put(componentName, instance);
            }else if (componentName.equals(ReporteController.class.getName())) {
                ReporteController instance = new ReporteController(instanceOf(GeneradorDeReportes.class));
                instances.put(componentName, instance);
            } else if (componentName.equals(UsuarioRepository.class.getName())) {
                UsuarioRepository instance = new UsuarioRepository();
                instances.put(componentName, instance);
            }else if (componentName.equals(GeneralRepository.class.getName())) {
                GeneralRepository instance = new GeneralRepository();
                instances.put(componentName, instance);
            } else if (componentName.equals(ColaboradorRepository.class.getName())) {
                ColaboradorRepository instance = new ColaboradorRepository();
                instances.put(componentName, instance);
            } else if (componentName.equals(HeladeraRepository.class.getName())) {
                HeladeraRepository instance = new HeladeraRepository();
                instances.put(componentName, instance);
            } else if (componentName.equals(TecnicoRepository.class.getName())) {
                TecnicoRepository instance = new TecnicoRepository();
                instances.put(componentName, instance);
            } else if (componentName.equals(ColaboracionRepository.class.getName())) {
                ColaboracionRepository instance = new ColaboracionRepository();
                instances.put(componentName, instance);
            } else if (componentName.equals(FormularioRepository.class.getName())) {
                FormularioRepository instance = new FormularioRepository();
                instances.put(componentName, instance);
            } else if (componentName.equals(SuscripcionRepository.class.getName())){
                SuscripcionRepository instance = new SuscripcionRepository();
                instances.put(componentName, instance);
            } else if(componentName.equals(RegistroDeFallaRepository.class.getName())) {
                RegistroDeFallaRepository instance = new RegistroDeFallaRepository();
                instances.put(componentName, instance);
            } else if(componentName.equals(ModeloHeladeraRepository.class.getName())) {
                ModeloHeladeraRepository instance = new ModeloHeladeraRepository();
                instances.put(componentName, instance);
            }else if(componentName.equals(VisitasRepository.class.getName())) {
                VisitasRepository instance = new VisitasRepository();
                instances.put(componentName, instance);
            } else if (componentName.equals(ValidadorContrasenia.class.getName())) {
                String archivoContraseniasDebiles = "src/main/resources/top10passwords.txt";
                ValidacionContraseniaDebil validacionDebil = new ValidacionContraseniaDebil(archivoContraseniasDebiles);
                ValidacionLongitud validacionLongitud = new ValidacionLongitud(64, 8);
                ValidacionDiferenteAlNombre validacionDiferenteAlNombre = new ValidacionDiferenteAlNombre();
                ValidadorContrasenia instance = new ValidadorContrasenia();
                instance.agregarEstrategiaDeValidacion(validacionDebil);
                instance.agregarEstrategiaDeValidacion(validacionLongitud);
                instance.agregarEstrategiaDeValidacion(validacionDiferenteAlNombre);
                instances.put(componentName, instance);
            } else if (componentName.equals(PersonaVulnerableRepository.class.getName())) {
                PersonaVulnerableRepository instance = new PersonaVulnerableRepository();
                instances.put(componentName, instance);
            } else if (componentName.equals(TipoIncidenteRepository.class.getName())) {
                TipoIncidenteRepository instance = new TipoIncidenteRepository();
                instances.put(componentName, instance);
            }
            else if (componentName.equals(RepositorioDeReportes.class.getName())) { // Agrega la instancia de ReporteRepository
                RepositorioDeReportes instance = new RepositorioDeReportes();
                instances.put(componentName, instance);
            }else if (componentName.equals(AlertaRepository.class.getName())) { // Agrega la instancia de ReporteRepository
                AlertaRepository instance = new AlertaRepository();
                instances.put(componentName, instance);
            }else if (componentName.equals(SolicitudAperturaRepository.class.getName())) { // Agrega la instancia de ReporteRepository
                SolicitudAperturaRepository instance = new SolicitudAperturaRepository();
                instances.put(componentName, instance);
            }else if (componentName.equals(TarjetaRepository.class.getName())) { // Agrega la instancia de ReporteRepository
                TarjetaRepository instance = new TarjetaRepository();
                instances.put(componentName, instance);
            }else if (componentName.equals(Notificador.class.getName()))    {
                Notificador instance = new Notificador();
                instances.put(componentName, instance);
            }else if (componentName.equals(NotificacionTecnico.class.getName()))    {
                NotificacionTecnico instance = new NotificacionTecnico();
                instances.put(componentName, instance);
            } else if (componentName.equals(NotificacionNuevoColaborador.class.getName()))    {
                NotificacionBuilder instance = new NotificacionNuevoColaborador();
                instances.put(componentName, instance);
            } else if (componentName.equals(ColaboradorBuilder.class.getName()))    {
                ColaboradorBuilder instance = new ColaboradorBuilder();
                instances.put(componentName, instance);
            } else if (componentName.equals(ImportadorCSV.class.getName()))    {
                ImportadorCSV instance = new ImportadorCSV();
                instances.put(componentName, instance);
            } else if (componentName.equals(ServicioApertura.class.getName()))    {
                ServicioApertura instance = new ServicioApertura(instanceOf(SolicitudAperturaRepository.class), instanceOf(HeladeraRepository.class), instanceOf(TarjetaRepository.class));
                instances.put(componentName, instance);
            }else if (componentName.equals(ReporteDeIncidentesBuilder.class.getName()))    {
                ReporteDeIncidentesBuilder instance = new ReporteDeIncidentesBuilder();
                instances.put(componentName, instance);
            } else if (componentName.equals(CalculadorDePuntaje.class.getName()))  {
                CalculadorDePuntaje instance = new CalculadorDePuntaje();
                instances.put(componentName, instance);
            } else if (componentName.equals(GeneradorDeReportes.class.getName())) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy");
                String fechaActual = LocalDate.now().format(formatter);
                String nombreArchivoPDF = "reporte_semanal_" + fechaActual + ".pdf";
                GeneradorDeReportes instance = new GeneradorDeReportes(new ExportadorAPDF(new AdapterApachePDF(nombreArchivoPDF)), ServiceLocator.instanceOf(HeladeraRepository.class));
                instances.put(componentName, instance);
            }
        }

        return (T) instances.get(componentName);
    }
}
