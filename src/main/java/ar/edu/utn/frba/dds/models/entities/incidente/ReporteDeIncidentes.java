package ar.edu.utn.frba.dds.models.entities.incidente;

import ar.edu.utn.frba.dds.config.ServiceLocator;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.notificacion.Email;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.Mensaje;
import ar.edu.utn.frba.dds.models.entities.notificacion.Notificador;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.NotificacionBuilder;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.NotificacionTecnico;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.persona.Tecnico;
import ar.edu.utn.frba.dds.models.entities.suscripciones.EnviadorAsincronico;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Localidad;
import ar.edu.utn.frba.dds.models.repositories.TecnicoRepository;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.mail.MessagingException;
import javax.persistence.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

@Setter
@Getter
@Data
@Entity
@Table(name = "reporte_de_incidentes")
public class ReporteDeIncidentes {
    @Id
    @GeneratedValue
    private Long id;


    @ManyToOne
    @JoinColumn(name = "heladera_id")
    private Heladera heladeraAfectada;

    @Column(name = "fecha_y_hora")
    private LocalDateTime fechaYHora;

    @ManyToOne
    @JoinColumn(name = "incidente_id")
    private TipoIncidente incidente;

    @ManyToOne
    @JoinColumn(name = "comunicador_id")
    private Colaborador comunicador;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "foto")
    private String foto;

    @ManyToOne
    @JoinColumn(name = "tecnico_id")
    private Tecnico tecnico;

    @Column(name = "solucionado")
    private Boolean solucionado = false;


    @Transient
    private NotificacionBuilder notificacionBuilder = ServiceLocator.instanceOf(NotificacionTecnico.class);

    public void buscaryAsignarTecnico(Localidad localidad) throws MessagingException, IOException {
        if (localidad.getTecnicos().isEmpty()) {
            this.tecnico = buscarTecnicoEnCualquierParte();

        } else {
            this.tecnico = localidad.escogerTecnicoAlAzar();
        }
        this.notificar();
    }

    private Tecnico buscarTecnicoEnCualquierParte() {
        List<Tecnico> tecnicos = ServiceLocator.instanceOf(TecnicoRepository.class).buscarTodos();
        if (tecnicos.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(tecnicos.size());
        return tecnicos.get(randomIndex);
    }

    public void notificar() throws MessagingException, IOException {
        if(tecnico != null){
            String contenidoHtml;
            if(tecnico.getMetodoDeNotificacion().getClass().equals(Email.class)){
                 contenidoHtml = "<html>"
                        + "<body style='font-family: Arial, sans-serif; color: #333;'>"
                        + "<div style='max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>"
                        + "    <h2 style='color: #0056b3; text-align: center;'>&#128276; Notificación Importante &#128276;</h2>"
                        + "    <p>Estimado <strong>" + tecnico.getNombre() + " " + tecnico.getApellido() + "</strong>,</p>"
                        + "    <p>Se ha reportado una falla en una de las heladeras asignadas. A continuación, los detalles:</p>"
                        + "    <table style='width: 100%; border-collapse: collapse;'>"
                        + "        <tr>"
                        + "            <td style='padding: 8px; background-color: #f7f7f7; border: 1px solid #ddd;'><strong>Ubicación:</strong></td>"
                        + "            <td style='padding: 8px; border: 1px solid #ddd;'>" + heladeraAfectada.direccionActual().direccionCompleta() + "</td>"
                        + "        </tr>"
                        + "        <tr>"
                        + "            <td style='padding: 8px; background-color: #f7f7f7; border: 1px solid #ddd;'><strong>Descripción de la falla:</strong></td>"
                        + "            <td style='padding: 8px; border: 1px solid #ddd;'>" + this.getDescripcion() + "</td>"
                        + "        </tr>"
                        + "        <tr>"
                        + "            <td style='padding: 8px; background-color: #f7f7f7; border: 1px solid #ddd;'><strong>Fecha y Hora:</strong></td>"
                        + "            <td style='padding: 8px; border: 1px solid #ddd;'>" + fechaYHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "</td>"
                        + "        </tr>"
                        + "    </table>"
                        + "    <p>Para más detalles y para realizar una visita, por favor ingrese al sistema.</p>"
                        + "    <p>Atentamente,</p>"
                        + "    <p><em>Equipo de Soporte Técnico</em></p>"
                        + "</div>"
                        + "</body>"
                        + "</html>";
            }else {
                contenidoHtml = "\uD83D\uDD14 Notificación Importante \uD83D\uDD14\n" +
                        "\n" +
                        "Estimado " + tecnico.getNombre() + " " + tecnico.getApellido() + "\n" +
                        "\n" +
                        "Se ha reportado una falla en una de las heladeras asignadas. A continuación, los detalles:\n" +
                        "\n" +
                        "\uD83D\uDCCD Ubicación: " + heladeraAfectada.direccionActual().direccionCompleta() +
                         "\n" +
                        " ⚠\uFE0F Descripción de la falla: " + this.getDescripcion() +
                        "\n" +
                        " \uD83D\uDCC5 Fecha y Hora: " + fechaYHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +"\n" +
                        "\n" +
                        "Para más detalles y para realizar una visita, por favor ingrese al sistema.\n" +
                        "\n" +
                        "Atentamente, Equipo de Soporte Técnico";
            }

        Mensaje mensaje = notificacionBuilder
                .agregarReceptor(tecnico)
                .agregarMensaje(contenidoHtml)
                .agregarMetodoNotificacion(tecnico.getMetodoDeNotificacion())
                .construir();

            EnviadorAsincronico.instancia().notificar(mensaje);
        }


    }

}
