package ar.edu.utn.frba.dds.server.init;

import ar.edu.utn.frba.dds.config.ServiceLocator;
import ar.edu.utn.frba.dds.controllers.RegistroDeFallaController;
import ar.edu.utn.frba.dds.models.entities.colaboracion.Colaboracion;
import ar.edu.utn.frba.dds.models.entities.colaboracion.TipoDeColaboraciones.DonacionViandas;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Comida;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.ModeloHeladera;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Vianda;
import ar.edu.utn.frba.dds.models.entities.incidente.ReporteDeIncidentes;
import ar.edu.utn.frba.dds.models.entities.incidente.TipoIncidente;
import ar.edu.utn.frba.dds.models.entities.notificacion.Email;
import ar.edu.utn.frba.dds.models.entities.persona.*;
import ar.edu.utn.frba.dds.models.entities.suscripciones.Suscripcion;
import ar.edu.utn.frba.dds.models.entities.suscripciones.SuscripcionHeladeraDesperfecto;
import ar.edu.utn.frba.dds.models.entities.ubicacion.DireccionAdaptada;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Localidad;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Municipio;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Provincia;
import ar.edu.utn.frba.dds.models.repositories.*;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import org.checkerframework.checker.units.qual.C;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.mail.MessagingException;
import javax.persistence.EntityTransaction;
import ar.edu.utn.frba.dds.models.entities.formulario.Formulario;
import ar.edu.utn.frba.dds.models.entities.formulario.Opcion;
import ar.edu.utn.frba.dds.models.entities.formulario.Pregunta;
import ar.edu.utn.frba.dds.models.entities.formulario.TipoDePregunta;
import ar.edu.utn.frba.dds.models.entities.persona.Usuario;
import ar.edu.utn.frba.dds.models.entities.puntos.Rubro;
import ar.edu.utn.frba.dds.models.entities.ubicacion.Ubicacion;
import ar.edu.utn.frba.dds.models.repositories.UsuarioRepository;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class Initializer implements WithSimplePersistenceUnit {
    private static UsuarioRepository usuarioRepository = ServiceLocator.instanceOf(UsuarioRepository.class);
    private static ColaboradorRepository colaboradorRepository = ServiceLocator.instanceOf(ColaboradorRepository.class);
    private static FormularioRepository formularioRepository = ServiceLocator.instanceOf(FormularioRepository.class);
    private static HeladeraRepository heladeraRepository = ServiceLocator.instanceOf(HeladeraRepository.class);
    private static RegistroDeFallaRepository registroDeFallaRepository =  ServiceLocator.instanceOf(RegistroDeFallaRepository.class);
    private SuscripcionRepository suscripcionRepository =  ServiceLocator.instanceOf(SuscripcionRepository.class);
    private static TipoIncidenteRepository tipoIncidenteRepository = ServiceLocator.instanceOf(TipoIncidenteRepository.class);
    private static TecnicoRepository tecnicoRepository = ServiceLocator.instanceOf(TecnicoRepository.class);
    private static ModeloHeladeraRepository modeloHeladeraRepository = ServiceLocator.instanceOf(ModeloHeladeraRepository.class);
    private static GeneralRepository repoGeneral = ServiceLocator.instanceOf(GeneralRepository.class);
    private static Usuario usu = (Usuario) usuarioRepository.buscarPorNombre("admin");

    static BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    public static void init(){

        if(!repoGeneral.controlarSiExisteModeloHeladera("Drean")){
            ModeloHeladera modelo1 = new ModeloHeladera();
            modelo1.setNombre("Drean");
            modelo1.setTemperaturaMinima(4D);
            modelo1.setTemperaturaMaxima(15D);

            modeloHeladeraRepository.guardar(modelo1);
        }

        if(!repoGeneral.controlarSiExisteModeloHeladera("Gafa")){
            ModeloHeladera modelo2 = new ModeloHeladera();
            modelo2.setNombre("Gafa");
            modelo2.setTemperaturaMinima(3D);
            modelo2.setTemperaturaMaxima(10D);


            modeloHeladeraRepository.guardar(modelo2);
        }

        if(usu == null){
            Usuario defaultUser = new Usuario();
            String encodedPassword = passwordEncoder.encode("admin123");
            defaultUser.setNombreUsuario("admin");
            defaultUser.setContrasenia(encodedPassword);
            defaultUser.setEstado(true);
            usuarioRepository.guardar(defaultUser);

            TipoIncidente incidente3 = new TipoIncidente("Falla Tecnica");
            tipoIncidenteRepository.guardar(incidente3);
            TipoIncidente incidente4 = new TipoIncidente("ALERTA DE CONEXIÓN");
            tipoIncidenteRepository.guardar(incidente4);
            TipoIncidente incidente5 = new TipoIncidente("ALERTA DE FRAUDE");
            tipoIncidenteRepository.guardar(incidente5);
            TipoIncidente incidente6 = new TipoIncidente("ALERTA DE TEMPERATURA");
            tipoIncidenteRepository.guardar(incidente6);
        }

        if (formularioRepository.buscarFormulario(1L) == null){
            Formulario formulario = new Formulario();
            formulario.setNombre("Formulario Persona Juridica");
            formulario.setFechaFormulario(LocalDateTime.now());

            Pregunta pregunta1 = new Pregunta();
            pregunta1.setDetalle("Razón Social");
            pregunta1.setTipoDePregunta(TipoDePregunta.DESARROLLO);
            Pregunta pregunta2 = new Pregunta();
            pregunta2.setDetalle("Tipo de Organización");
            pregunta2.setTipoDePregunta(TipoDePregunta.SINGLE_CHOICE);
            Opcion Opcion = new Opcion();
            Opcion.setDetalle("Empresa");
            Opcion Opcion2 = new Opcion();
            Opcion2.setDetalle("Fundación");
            Opcion Opcion3 = new Opcion();
            Opcion3.setDetalle("Organización sin fines de lucro");
            pregunta2.agregarOpcion(Opcion);
            pregunta2.agregarOpcion(Opcion2);
            pregunta2.agregarOpcion(Opcion3);
            Pregunta pregunta3 = new Pregunta();
            pregunta3.setDetalle("Rubro");
            pregunta3.setTipoDePregunta(TipoDePregunta.DESARROLLO);

            formulario.agregarItem(pregunta1);
            formulario.agregarItem(pregunta2);
            formulario.agregarItem(pregunta3);

            formularioRepository.guardar(formulario);
        }

        if (formularioRepository.buscarFormulario(2L) == null){
            Formulario formulario = new Formulario();
            formulario.setNombre("Formulario Persona Fisica");
            formulario.setFechaFormulario(LocalDateTime.now());

            Pregunta pregunta1 = new Pregunta();
            pregunta1.setDetalle("Edad");
            pregunta1.setTipoDePregunta(TipoDePregunta.DESARROLLO);
            Pregunta pregunta2 = new Pregunta();
            pregunta2.setDetalle("Color Favorito");
            pregunta2.setTipoDePregunta(TipoDePregunta.SINGLE_CHOICE);
            Opcion Opcion = new Opcion();
            Opcion.setDetalle("Rojo");
            Opcion Opcion2 = new Opcion();
            Opcion2.setDetalle("Azul");
            Opcion Opcion3 = new Opcion();
            Opcion3.setDetalle("Verde");
            pregunta2.agregarOpcion(Opcion);
            pregunta2.agregarOpcion(Opcion2);
            pregunta2.agregarOpcion(Opcion3);

            formulario.agregarItem(pregunta1);
            formulario.agregarItem(pregunta2);

            formularioRepository.guardar(formulario);
        }


        if (colaboradorRepository.buscarPorNombre("Ezequiel").size() == 0) {
            Colaborador colaborador = new Colaborador();
            colaborador.setNombre("Ezequiel");
            colaborador.setApellido("Gonzalez");
            colaborador.setMail("ezequiel@gmail.com");
            colaborador.setTelefono("123456789");
            colaborador.setTipoPersona(TipoPersona.FISICA);
            Documento documento = new Documento();
            documento.setTipoDocumento(TipoDocumento.DNI);
            documento.setNumeroDocumento("123454678");
            colaborador.setDocumento(documento);
            colaborador.setFechaNacimiento(LocalDate.of(1999, 1, 1));
            colaborador.setPuntos(0.0);
            colaborador.setActivo(true);
            Usuario usuario = new Usuario();
            usuario.setEstado(true);
            usuario.setNombreUsuario("eze");
            String bCryptedPassword = passwordEncoder.encode("eze12345");
            usuario.setContrasenia(bCryptedPassword);
            usuarioRepository.guardar2(usuario);
            colaborador.setUsuario(usuario);
            colaboradorRepository.guardar2(colaborador);
        }

        if (colaboradorRepository.buscarPorNombre("empresa").size() == 0) {
            Colaborador colaborador = new Colaborador();
            colaborador.setNombre("empresa");
            colaborador.setMail("ezequiel@gmail.com");
            colaborador.setTelefono("123456789");
            colaborador.setTipoPersona(TipoPersona.JURIDICA);
            Documento documento = new Documento();
            documento.setTipoDocumento(TipoDocumento.CUIT);
            documento.setNumeroDocumento("123456678");
            colaborador.setDocumento(documento);
            colaborador.setPuntos(0.0);
            colaborador.setActivo(true);
            Usuario usuario = new Usuario();
            usuario.setEstado(true);
            usuario.setNombreUsuario("empresa");
            String bCryptedPassword = passwordEncoder.encode("empresa123");
            usuario.setContrasenia(bCryptedPassword);
            usuarioRepository.guardar2(usuario);
            colaborador.setUsuario(usuario);
            colaboradorRepository.guardar2(colaborador);
        }



    }
    public void guardar(ReporteDeIncidentes reporte){
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();
        registroDeFallaRepository.guardar(reporte);
        tx.commit();
    }
}
