package ar.edu.utn.frba.dds.models.entities.persona;

import ar.edu.utn.frba.dds.converters.MetodoDeNotificacionConverter;
import ar.edu.utn.frba.dds.models.entities.incidente.ReporteDeIncidentes;
import ar.edu.utn.frba.dds.models.entities.formulario.FormularioContestado;
import ar.edu.utn.frba.dds.models.entities.notificacion.MetodoDeNotificacion;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Localidad;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Data
@Entity
@Table(name = "tecnico")
public class Tecnico implements  INotificable{
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "mail")
    protected String mail;

    @Column(name = "chatTelegramId")
    private String chatTelegramId;

    @Column(name = "usuarioTelegram")
    private String usuarioTelegram;

    @Convert(converter = MetodoDeNotificacionConverter.class)
    @Column(name = "metodo_notificacion")
    private MetodoDeNotificacion metodoDeNotificacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipoPersona")
    private TipoPersona tipoPersona;

    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "formulario_contestado_id", referencedColumnName = "id")
    private FormularioContestado formulario;

    @JsonIgnore
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "localidad_id", referencedColumnName = "id")
    private Localidad areaCobertura;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "tecnico_id")
    private List<Visita> visitas;

    @OneToMany(mappedBy = "tecnico", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    private List<ReporteDeIncidentes> incidentesAAteneder;

    public Tecnico() {
        this.incidentesAAteneder = new ArrayList<>();
        this.visitas = new ArrayList<>();

    }

    public void setAreaCobertura(Localidad areaCobertura) {
        this.areaCobertura = areaCobertura;
        if (areaCobertura != null) {
            areaCobertura.agregarTecnico(this);
        }
    }


    @Override
    public String getCorreo() {
        return this.mail;
    }
    @Override
    public String getTelefono() {
        return this.telefono;
    }
    @Override
    public String getTelegramChatId() {
        return this.chatTelegramId;
    }

    public void agregarVisita(Visita visita) {
        this.visitas.add(visita);
    }

    public void agregarIncidente(ReporteDeIncidentes incidente) {
        this.incidentesAAteneder.add(incidente);
    }
}

