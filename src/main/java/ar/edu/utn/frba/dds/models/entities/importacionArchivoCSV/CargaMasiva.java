package ar.edu.utn.frba.dds.models.entities.importacionArchivoCSV;

import ar.edu.utn.frba.dds.models.entities.colaboracion.Colaboracion;
import ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta.Tarjeta;
import ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta.TipoTarjeta;
import ar.edu.utn.frba.dds.models.entities.notificacion.Email;
import ar.edu.utn.frba.dds.models.entities.notificacion.Notificador;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.Mensaje;
import ar.edu.utn.frba.dds.models.entities.colaboracion.TipoDeColaboraciones.DonacionDinero;
import ar.edu.utn.frba.dds.models.entities.colaboracion.TipoDeColaboraciones.DonacionViandas;
import ar.edu.utn.frba.dds.models.entities.colaboracion.TipoDeColaboraciones.EntregaTarjetas;
import ar.edu.utn.frba.dds.models.entities.colaboracion.TipoDeColaboraciones.RedistribucionViandas;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.NotificacionBuilder;
import ar.edu.utn.frba.dds.models.entities.persona.*;
import ar.edu.utn.frba.dds.models.entities.persona.builder.IColaboradorBuilder;
import ar.edu.utn.frba.dds.models.entities.suscripciones.EnviadorAsincronico;
import ar.edu.utn.frba.dds.models.repositories.ColaboracionRepository;
import ar.edu.utn.frba.dds.models.repositories.ColaboradorRepository;
import ar.edu.utn.frba.dds.models.repositories.UsuarioRepository;
import lombok.Getter;
import org.hibernate.metamodel.relational.Loggable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.mail.MessagingException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

@Getter
public class CargaMasiva {
    private Notificador enviador;
    private NotificacionBuilder notificacionBuilder;
    private IColaboradorBuilder colaboradorBuilder;
    private ColaboradorRepository colaboradorRepository;
    private UsuarioRepository usuarioRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private Logger logger = Logger.getLogger(CargaMasiva.class.getName());

    public CargaMasiva(Notificador enviador, NotificacionBuilder notificacionBuilder, IColaboradorBuilder colaboradorBuilder, ColaboradorRepository colaboradorRepository, UsuarioRepository usuarioRepository) {
        this.enviador = enviador;
        this.notificacionBuilder = notificacionBuilder;
        this.colaboradorBuilder = colaboradorBuilder;
        this.colaboradorRepository = colaboradorRepository;
        this.usuarioRepository = usuarioRepository;
    }


    public void agregarColaborador (Colaborador colaborador){

        this.colaboradorRepository.guardar3(colaborador);
    }


    public void procesarDatos(List<ColaboradorDTO> dataValueObjects) throws MessagingException, IOException {
        for (ColaboradorDTO dataValueObject : dataValueObjects) {
            Colaboracion nuevaColaboracion = null;

            switch (dataValueObject.getFormaColaboracion()){
                case("DINERO"):
                    nuevaColaboracion = new DonacionDinero(dataValueObject.getCantidad(),
                            dataValueObject.getFechaColaboracion());
                    break;
                case("DONACION_VIANDAS"):
                    nuevaColaboracion = new DonacionViandas(dataValueObject.getFechaColaboracion(),
                            dataValueObject.getCantidad());
                    nuevaColaboracion.setFueExitosa(true);
                    break;
                case("REDISTRIBUCION_VIANDAS"):
                    nuevaColaboracion = new RedistribucionViandas(dataValueObject.getFechaColaboracion(),
                            dataValueObject.getCantidad());
                    nuevaColaboracion.setFueExitosa(true);
                    break;
                case("ENTREGA_TARJETAS"):
                    nuevaColaboracion = new EntregaTarjetas(dataValueObject.getFechaColaboracion(),
                            dataValueObject.getCantidad());
                    break;
                default:
                    break;
            }


            if(existeColaborador(TipoDocumento.valueOf(dataValueObject.getTipoDocumento()),dataValueObject.getDocumento())){
                logger.info("Colaborador Existe");
                Colaborador colaboradorEncontrado = traerColaborador(TipoDocumento.valueOf(dataValueObject.getTipoDocumento()),dataValueObject.getDocumento());
                colaboradorEncontrado.agregarColaboracion(nuevaColaboracion);
                this.colaboradorRepository.actualizar(colaboradorEncontrado);
            }
            else {

                Documento documento = new Documento();
                documento.setTipoDocumento(TipoDocumento.valueOf(dataValueObject.getTipoDocumento()));
                documento.setNumeroDocumento(String.valueOf(dataValueObject.getDocumento()));
                Email email = new Email();

                Colaborador nuevoColaborador = colaboradorBuilder.agregarNombre(dataValueObject.getNombre())
                        .agregarApellido(dataValueObject.getApellido())
                        .agregarDocumento(documento)
                        .agregarMail(dataValueObject.getMail())
                        .agregarColaboraciones(nuevaColaboracion)
                        .agregarMetodoNotificacion(email)
                        .construir();

                String username = generateUsername(nuevoColaborador);
                String password = generatePassword();

                Usuario usuario = new Usuario();
                usuario.setNombreUsuario(username);
                usuario.setEstado(true);
                String bCryptedPassword = bCryptPasswordEncoder.encode(password);
                usuario.setContrasenia(bCryptedPassword);
                this.usuarioRepository.guardar(usuario);
                nuevoColaborador.setUsuario(usuario);
                nuevoColaborador.setActivo(true);
                nuevoColaborador.setPuntos(0.0);
                nuevoColaborador.setTipoPersona(TipoPersona.FISICA);
                Tarjeta tarjeta = new Tarjeta();
                tarjeta.setTipoTarjeta(TipoTarjeta.COLABORADOR);
                tarjeta.setEstaHabilitada(true);
                tarjeta.setFechaInicioDeUso(LocalDate.now());
                nuevoColaborador.agregarTarjeta(tarjeta);



                Mensaje mensaje = notificacionBuilder.agregarReceptor(nuevoColaborador)
                        .agregarMensaje("<html>"
                                + "<body style='font-family: Arial, sans-serif; color: #333;'>"
                                + "    <h2 style='color: #0056b3;'>¡Bienvenido al Sistema!</h2>"
                                + "    <p>Estimado <strong>" + nuevoColaborador.getNombre() + " " + nuevoColaborador.getApellido() + "</strong>,</p>"
                                + "    <p>¡Estamos encantados de tenerte con nosotros en el Sistema para la Mejora del Acceso Alimentario en Contextos de Vulnerabilidad Socioeconómica!</p>"
                                + "    <p>A continuación, encontrarás tus credenciales de acceso:</p>"
                                + "    <table style='width: 100%; border-collapse: collapse; margin-top: 10px;'>"
                                + "        <tr>"
                                + "            <td style='padding: 8px; background-color: #f7f7f7; border: 1px solid #ddd;'><strong>Usuario:</strong></td>"
                                + "            <td style='padding: 8px; border: 1px solid #ddd;'>" + username + "</td>"
                                + "        </tr>"
                                + "        <tr>"
                                + "            <td style='padding: 8px; background-color: #f7f7f7; border: 1px solid #ddd;'><strong>Contraseña:</strong></td>"
                                + "            <td style='padding: 8px; border: 1px solid #ddd;'>" + password + "</td>"
                                + "        </tr>"
                                + "    </table>"
                                + "    <p style='margin-top: 20px;'>Por favor, ingresa al sistema y confirma que tus datos sean correctos. Si falta información, te pedimos que la completes a la brevedad.</p>"
                                + "    <p>¡Gracias por ser parte de nuestra comunidad!</p>"
                                + "    <p>Atentamente,</p>"
                                + "    <p><em>El equipo de soporte</em></p>"
                                + "</body>"
                                + "</html>")
                        .agregarMetodoNotificacion(nuevoColaborador.getMetodoDeNotificacion())
                        .construir();

                //Notificador.instancia().enviar(mensaje);
                //enviador.enviar(mensaje);
                EnviadorAsincronico.instancia().notificar(mensaje);


                this.agregarColaborador(nuevoColaborador);
                logger.info("ID: " + nuevoColaborador.getId() +
                        ", Nombre: " + nuevoColaborador.getNombre() +
                        ", Apellido: " + nuevoColaborador.getApellido() +
                        ", Documento: " + nuevoColaborador.getDocumento().getNumeroDocumento() +
                        ", Tipo de Documento: " + nuevoColaborador.getDocumento().getTipoDocumento() +
                        ", Email: " + nuevoColaborador.getMail());

            }
        }
    }

    private String generateUsername(Colaborador colaborador) {
        return colaborador.getNombre().toLowerCase() + "." + colaborador.getApellido().toLowerCase();
    }

    private String generatePassword() {
        return "Pass" + new Random().nextInt(1000000);
    }

    public Boolean existeColaborador(TipoDocumento tipoDocumento, String documento) {
        return this.colaboradorRepository.existePorDocumento(tipoDocumento, documento);
    }

    public Colaborador traerColaborador(TipoDocumento tipoDocumento, String documento) {
        return this.colaboradorRepository.buscarPorDocumento(tipoDocumento, documento);
    }
}