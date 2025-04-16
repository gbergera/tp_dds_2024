package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.config.ServiceLocator;
import ar.edu.utn.frba.dds.converters.MetodoDeNotificacionConverter;
import ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta.Tarjeta;
import ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta.TipoTarjeta;
import ar.edu.utn.frba.dds.models.entities.contrasenia.*;
import ar.edu.utn.frba.dds.models.entities.excepciones.PuntosInsuficienteException;
import ar.edu.utn.frba.dds.models.entities.formulario.*;
import ar.edu.utn.frba.dds.models.entities.persona.*;
import ar.edu.utn.frba.dds.models.entities.puntos.Oferta;
import ar.edu.utn.frba.dds.models.entities.puntos.Rubro;
import ar.edu.utn.frba.dds.models.entities.ubicacion.DireccionAdaptada;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Localidad;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Municipio;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Provincia;
import ar.edu.utn.frba.dds.models.repositories.*;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.repositories.ColaboradorRepository;
import ar.edu.utn.frba.dds.server.utils.ICrudViewsHandler;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.hibernate.engine.jdbc.dialect.spi.DialectResolutionInfo;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.EntityTransaction;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ColaboradorController extends Controller implements ICrudViewsHandler {
    private ColaboradorRepository repoDeColaboradores;
    private UsuarioRepository repoDeUsuarios;
    private FormularioRepository repoDeFormularios;
    private ColaboracionRepository repoDeColaboraciones;
    private GeneralRepository generalRepo;
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public ColaboradorController(ColaboradorRepository colaboradorRepository, UsuarioRepository usuarioRepository, FormularioRepository formularioRepository,ColaboracionRepository colaboracionRepository, GeneralRepository generalRepository) {
        this.repoDeColaboradores = colaboradorRepository;
        this.repoDeUsuarios = usuarioRepository;
        this.repoDeFormularios = formularioRepository;
        this.repoDeColaboraciones = colaboracionRepository;
        this.generalRepo = generalRepository;
    }
    @Override
    public void index(Context context) {

    }

    @Override
    public void show(Context context) {
        Map<String, Object> model = new HashMap<>();
        Long colaboradorId = Long.valueOf(Objects.requireNonNull(context.sessionAttribute("colaborador-id")));
        Colaborador colaborador = (Colaborador) this.repoDeColaboradores.buscar(colaboradorId);
        model.put("colaborador", colaborador);
        TipoPersona tipoPersona = context.sessionAttribute("tipo-persona");
        model.put("isJuridica", tipoPersona == TipoPersona.JURIDICA);
        if (colaborador.getMetodoDeNotificacion() != null) {
            model.put("colaborador-notif", colaborador.getMetodoDeNotificacion().getClass().getSimpleName());
        } else {
            model.put("colaborador-notif", "No definido");
        }
        model.put("perfilActive", true);

        if (colaborador.getTipoPersona() == TipoPersona.FISICA) {
            context.render("perfil.hbs", model);
        } else {
            context.render("perfiljuridica.hbs", model);
        }
    }

    @Override
    public void create(Context context) {
        Map<String, Object> model = new HashMap<>();

       //FORMULARIO PARA JURIDICA
        Formulario formulario = (Formulario) repoDeFormularios.buscarUltimo("Juridica");
        model.put("formulario", formulario);

        //FORMULARIO PARA FISICA
        Formulario formulario2 = (Formulario) repoDeFormularios.buscarUltimo("Fisica");
        model.put("formulario-fisica", formulario2);

        // Add document types to the model
        model.put("tiposDocumento", TipoDocumento.values());

        context.render("registro.hbs", model);
    }

    @Override
    public void save(Context context) {
        ValidadorContrasenia validador = ServiceLocator.instanceOf(ValidadorContrasenia.class);

        Map<String, Object> model = new HashMap<>();

        if (this.repoDeUsuarios.buscarPorNombre( context.formParam("usuario")) != null) {
            model.put("error", "El nombre de usuario ya existe, por favor eliga otro");
            Formulario formulario = (Formulario) repoDeFormularios.buscarUltimo("Juridica");
            model.put("formulario", formulario);
            Formulario formulario2 = (Formulario) repoDeFormularios.buscarUltimo("Fisica");
            model.put("formulario-fisica", formulario2);
            context.render("registro.hbs", model);
            return;
        }


        try {
            validador.validar( context.formParam("usuario"), context.formParam("password"));
        } catch (ValidacionContraseniaException e) {
            model.put("error", e.getMessage());
            Formulario formulario = (Formulario) repoDeFormularios.buscarUltimo("Juridica");
            model.put("formulario", formulario);
            Formulario formulario2 = (Formulario) repoDeFormularios.buscarUltimo("Fisica");
            model.put("formulario-fisica", formulario2);
            context.render("registro.hbs", model);
            return;
        }

        if(!Objects.equals(context.formParam("password"), context.formParam("check-password"))){
            model.put("error", "Las contraseñas no coinciden");
            Formulario formulario = (Formulario) repoDeFormularios.buscarUltimo("Juridica");
            model.put("formulario", formulario);
            Formulario formulario2 = (Formulario) repoDeFormularios.buscarUltimo("Fisica");
            model.put("formulario-fisica", formulario2);
            context.render("registro.hbs", model);
        }

        else {
            Colaborador colaborador = new Colaborador();
            FormularioContestado formularioContestado = new FormularioContestado();
            if (Objects.equals(context.formParam("tipo-persona"), "fisica")) {
                colaborador.setTipoPersona(TipoPersona.FISICA);
                colaborador.setNombre(context.formParam("nombre-fisica"));
                colaborador.setApellido(context.formParam("apellido"));
                Documento documento = new Documento();
                documento.setTipoDocumento(Objects.requireNonNull(TipoDocumento.valueOf(context.formParam("tipo-doc-fisica"))));
                documento.setNumeroDocumento(Objects.requireNonNull(context.formParam("doc-fisica")));
                colaborador.setDocumento(documento);
                colaborador.setTelefono(context.formParam("tel-fisica"));
                colaborador.setMail(context.formParam("mail-fisica"));
                colaborador.setFechaNacimiento(LocalDate.parse(Objects.requireNonNull(context.formParam("fecha-fisica"))));

                String provinciaNombre = context.formParam("direccion-fisica.provincia");
                Provincia provincia = (Provincia) generalRepo.controlarSiExisteProvincia(provinciaNombre);
                if (provincia == null) {
                    provincia = new Provincia();
                    provincia.setNombre(provinciaNombre);
                }

                String municipioNombre = context.formParam("direccion-fisica.municipio");
                Municipio municipio = (Municipio) generalRepo.controlarSiExisteMunicipio(municipioNombre);
                if (municipio == null) {
                    municipio = new Municipio();
                    municipio.setNombre(municipioNombre);
                    municipio.setProvincia(provincia);
                }

                String localidadNombre = context.formParam("direccion-fisica.localidad");
                Localidad localidad = (Localidad) generalRepo.controlarSiExisteLocalidad(localidadNombre);
                if (localidad == null) {
                    localidad = new Localidad();
                    localidad.setNombre(localidadNombre);
                    localidad.setMunicipio(municipio);
                }

                DireccionAdaptada direccion = new DireccionAdaptada();
                direccion.setCalle(context.formParam("direccion-fisica.calle"));
                direccion.setNumero(Integer.valueOf(Objects.requireNonNull(context.formParam("direccion-fisica.numero"))));
                direccion.setLocalidad(localidad);
                colaborador.setDireccion(direccion);

                String metodoNotificacionStr = context.formParam("metodo-notificacion-fisica");
                MetodoDeNotificacionConverter converter = new MetodoDeNotificacionConverter();
                colaborador.setMetodoDeNotificacion(converter.convertToEntityAttribute(metodoNotificacionStr));
                colaborador.setPuntos(0.0);
                colaborador.setChatTelegramId(context.formParam("telegram-chat-id-fisica"));
                colaborador.setUsuarioTelegram(context.formParam("telegram-usuario-fisica"));

                formularioContestado.setFormulario(this.repoDeFormularios.buscarFormulario(Long.valueOf(Objects.requireNonNull(context.formParam("formulario-fisica-id")))));
                for (Pregunta pregunta : formularioContestado.getFormulario().getPreguntas()) {
                    Respuesta respuesta = new Respuesta();
                    respuesta.setPregunta(pregunta);

                    if (pregunta.getTipoDePregunta() == TipoDePregunta.DESARROLLO) {
                        respuesta.setDetalle(context.formParam("pregunta-fisica-" + pregunta.getId()));
                    } else if (pregunta.getTipoDePregunta() == TipoDePregunta.SINGLE_CHOICE) {
                        respuesta.setDetalle(context.formParam("pregunta-fisica-" + pregunta.getId()));
                    }

                    formularioContestado.agregarRespuesta(respuesta);
                    formularioContestado.setFechaContestado(LocalDateTime.now());
                }
                colaborador.setFormulario(formularioContestado);


                Usuario usuario = new Usuario();
                usuario.setEstado(true);
                usuario.setNombreUsuario(context.formParam("usuario"));
                String bCryptedPassword = bCryptPasswordEncoder.encode(context.formParam("password"));
                usuario.setContrasenia(bCryptedPassword);

                colaborador.setUsuario(usuario);

                this.repoDeUsuarios.guardar(usuario);

            }else if (Objects.equals(context.formParam("tipo-persona"), "juridica")) {
                colaborador.setTipoPersona(TipoPersona.JURIDICA);
                colaborador.setNombre(context.formParam("nombre-juridica"));
                colaborador.setTelefono(context.formParam("tel-juridica"));
                colaborador.setMail(context.formParam("mail-juridica"));
                Documento documento = new Documento();
                documento.setTipoDocumento(TipoDocumento.CUIT);
                documento.setNumeroDocumento(Objects.requireNonNull(context.formParam("cuit")));
                colaborador.setDocumento(documento);

                String metodoNotificacionStr = context.formParam("metodo-notificacion-juridica");
                MetodoDeNotificacionConverter converter = new MetodoDeNotificacionConverter();
                colaborador.setMetodoDeNotificacion(converter.convertToEntityAttribute(metodoNotificacionStr));
                colaborador.setChatTelegramId(context.formParam("telegram-chat-id-juridica"));
                colaborador.setUsuarioTelegram(context.formParam("telegram-usuario-juridica"));

                String provinciaNombre = context.formParam("direccion-juridica.provincia");
                Provincia provincia = (Provincia) generalRepo.controlarSiExisteProvincia(provinciaNombre);
                if (provincia == null) {
                    provincia = new Provincia();
                    provincia.setNombre(provinciaNombre);
                }

                String municipioNombre = context.formParam("direccion-juridica.municipio");
                Municipio municipio = (Municipio) generalRepo.controlarSiExisteMunicipio(municipioNombre);
                if (municipio == null) {
                    municipio = new Municipio();
                    municipio.setNombre(municipioNombre);
                    municipio.setProvincia(provincia);
                }

                String localidadNombre = context.formParam("direccion-juridica.localidad");
                Localidad localidad = (Localidad) generalRepo.controlarSiExisteLocalidad(localidadNombre);
                if (localidad == null) {
                    localidad = new Localidad();
                    localidad.setNombre(localidadNombre);
                    localidad.setMunicipio(municipio);
                }

                DireccionAdaptada direccion = new DireccionAdaptada();
                direccion.setCalle(context.formParam("direccion-juridica.calle"));
                direccion.setNumero(Integer.valueOf(Objects.requireNonNull(context.formParam("direccion-juridica.numero"))));
                direccion.setLocalidad(localidad);
                colaborador.setDireccion(direccion);

                formularioContestado.setFormulario(this.repoDeFormularios.buscarFormulario(Long.valueOf(context.formParam("formulario-id"))));
                for (Pregunta pregunta : formularioContestado.getFormulario().getPreguntas()) {
                    Respuesta respuesta = new Respuesta();
                    respuesta.setPregunta(pregunta);

                    if (pregunta.getTipoDePregunta() == TipoDePregunta.DESARROLLO) {
                        respuesta.setDetalle(context.formParam("pregunta-" + pregunta.getId()));
                    } else if (pregunta.getTipoDePregunta() == TipoDePregunta.SINGLE_CHOICE) {
                        respuesta.setDetalle(context.formParam("pregunta-" + pregunta.getId()));
                    }

                    formularioContestado.agregarRespuesta(respuesta);
                    formularioContestado.setFechaContestado(LocalDateTime.now());
                }
                colaborador.setFormulario(formularioContestado);


                Usuario usuario = new Usuario();
                usuario.setEstado(true);
                usuario.setNombreUsuario(context.formParam("usuario"));
                String bCryptedPassword = bCryptPasswordEncoder.encode(context.formParam("password"));
                usuario.setContrasenia(bCryptedPassword);

                colaborador.setUsuario(usuario);

                this.repoDeUsuarios.guardar(usuario);
            }

            Tarjeta tarjeta = new Tarjeta();
            tarjeta.setTipoTarjeta(TipoTarjeta.COLABORADOR);
            tarjeta.setEstaHabilitada(true);
            tarjeta.setFechaInicioDeUso(LocalDate.now());
            colaborador.agregarTarjeta(tarjeta);
            colaborador.setActivo(true);
            EntityTransaction tx = entityManager().getTransaction();
            if(!tx.isActive())
                tx.begin();
            this.repoDeColaboradores.guardar(colaborador);
            tx.commit();
            entityManager().refresh(colaborador);
            context.status(HttpStatus.CREATED);
            model.put("redireccion", "login");
            context.render("success.hbs", model);
        }

    }

    @Override
    public void edit(Context context) {

    }

    @Override
    public void update(Context context) {
        Long colaborador_id = Long.valueOf(Objects.requireNonNull(context.sessionAttribute("colaborador-id")));
        Colaborador colaborador = (Colaborador) this.repoDeColaboradores.buscar(colaborador_id);

        if (colaborador.getTipoPersona() == TipoPersona.FISICA){
            colaborador.setApellido(context.formParam("apellido"));
            colaborador.setFechaNacimiento(LocalDate.parse(Objects.requireNonNull(context.formParam("fechaNacimiento"))));

        }else{

        }


        colaborador.setNombre(context.formParam("nombre"));

        DireccionAdaptada direccion = colaborador.getDireccion();
        if (direccion == null) {
            direccion = new DireccionAdaptada();
        }
        direccion.setCalle(context.formParam("direccion.calle"));
        direccion.setNumero(Integer.valueOf(Objects.requireNonNull(context.formParam("direccion.numero"))));


        String provinciaNombre = context.formParam("direccion.provincia");
        Provincia provincia = (Provincia) generalRepo.controlarSiExisteProvincia(provinciaNombre);
        if (provincia == null) {
            provincia = new Provincia();
            provincia.setNombre(provinciaNombre);
        }

        String municipioNombre = context.formParam("direccion.municipio");
        Municipio municipio = (Municipio) generalRepo.controlarSiExisteMunicipio(municipioNombre);
        if (municipio == null) {
            municipio = new Municipio();
            municipio.setNombre(municipioNombre);
            municipio.setProvincia(provincia);
        }

        String localidadNombre = context.formParam("direccion.localidad");
        Localidad localidad = (Localidad) generalRepo.controlarSiExisteLocalidad(localidadNombre);
        if (localidad == null) {
            localidad = new Localidad();
            localidad.setNombre(localidadNombre);
            localidad.setMunicipio(municipio);
        }

        direccion.setLocalidad(localidad);
        colaborador.setDireccion(direccion);

        colaborador.setMail(context.formParam("email"));
        colaborador.setTelefono(context.formParam("telefono"));

        String metodoNotificacionStr = context.formParam("metodo-notificacion");
        MetodoDeNotificacionConverter converter = new MetodoDeNotificacionConverter();
        colaborador.setMetodoDeNotificacion(converter.convertToEntityAttribute(metodoNotificacionStr));
        colaborador.setChatTelegramId(context.formParam("telegram-chat-id"));
        colaborador.setUsuarioTelegram(context.formParam("telegram-usuario"));
        EntityTransaction tx = entityManager().getTransaction();
        if(!tx.isActive())
            tx.begin();
        this.repoDeColaboradores.actualizar(colaborador);
        tx.commit();
        context.status(HttpStatus.OK);
        context.redirect("/perfil");

    }

    @Override
    public void delete(Context context) {

    }

    public void canjearOferta(Context ctx) throws PuntosInsuficienteException {
        Long colaboradorId = Long.valueOf(Objects.requireNonNull(ctx.sessionAttribute("colaborador-id")));
        Colaborador colaborador = (Colaborador) this.repoDeColaboradores.buscar(colaboradorId);
        Long ofertaId = Long.valueOf(Objects.requireNonNull(ctx.pathParam("id")));
        Oferta oferta = repoDeColaboraciones.buscarOfertaPorId(ofertaId);
        if (oferta == null) {
            ctx.status(HttpStatus.BAD_REQUEST);
            ctx.result("No se encontró la oferta");
        } else {
            try {
                colaborador.canjearOferta(oferta);
                EntityTransaction tx = entityManager().getTransaction();
                if(!tx.isActive())
                    tx.begin();
                this.repoDeColaboradores.actualizar(colaborador);
                tx.commit();
                ctx.status(HttpStatus.OK);
            } catch (PuntosInsuficienteException e) {
                ctx.status(HttpStatus.BAD_REQUEST);
                ctx.result("No tienes suficientes puntos para canjear esta oferta");
            }
        }
    }
}
