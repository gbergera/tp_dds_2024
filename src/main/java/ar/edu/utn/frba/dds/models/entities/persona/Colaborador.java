package ar.edu.utn.frba.dds.models.entities.persona;

import ar.edu.utn.frba.dds.converters.MetodoDeNotificacionConverter;
import ar.edu.utn.frba.dds.models.entities.colaboracion.Colaboracion;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta.Tarjeta;
import ar.edu.utn.frba.dds.models.entities.excepciones.PuntosInsuficienteException;
import ar.edu.utn.frba.dds.models.entities.formulario.FormularioContestado;
import ar.edu.utn.frba.dds.models.entities.notificacion.MetodoDeNotificacion;
import ar.edu.utn.frba.dds.models.entities.puntos.Oferta;
import ar.edu.utn.frba.dds.models.entities.suscripciones.Suscripcion;
import ar.edu.utn.frba.dds.models.entities.ubicacion.DireccionAdaptada;
import ar.edu.utn.frba.dds.models.entities.ubicacion.Ubicacion;
import ar.edu.utn.frba.dds.models.entities.ubicacion.recomendador.Recomendador;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "colaborador")
public class Colaborador implements INotificable{
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "fechaNacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "mail")
    private String mail;

    @Column(name = "chatTelegramId")
    private String chatTelegramId;

    @Column(name = "usuarioTelegram")
    private String usuarioTelegram;

    @Column(name = "tipoPersona")
    @Enumerated(EnumType.STRING)
    private TipoPersona tipoPersona;

    @Convert(converter = MetodoDeNotificacionConverter.class)
    @Column(name = "metodo_notificacion")
    private MetodoDeNotificacion metodoDeNotificacion;

    @OneToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "formulario_id", referencedColumnName = "id")
    private FormularioContestado formulario;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinColumn(name = "colaborador_id")
    private List<Colaboracion> colaboraciones;


    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(name = "colaborador_oferta",
            joinColumns = @JoinColumn(name = "colaborador_id"),
            inverseJoinColumns = @JoinColumn(name = "oferta_id"))
    private List<Oferta> ofertasCanjeadas = new ArrayList<>();

    @Column(name= "puntos")
    private Double puntos;

    @Column(name= "activo")
    private boolean activo;

    @Embedded
    private Documento documento;

    @Transient
    private Ubicacion ubicacion;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "direccion_id", referencedColumnName = "id")
    private DireccionAdaptada direccion;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "colaborador_id")
    private List<Tarjeta> tarjetas;

    public void agregarTarjeta(Tarjeta tarjeta) {
        this.tarjetas.add(tarjeta);
    }

    public Tarjeta obtenerUltimaTarjeta() {
        if (this.tarjetas.isEmpty()) {
            return null;
        }
        return this.tarjetas.get(tarjetas.size() - 1);
    }

    public void agregarColaboracion(Colaboracion colaboracion){
        this.colaboraciones.add(colaboracion);
    }

    public void canjearOferta(Oferta oferta) throws PuntosInsuficienteException {
        if (this.puntos == null) {
            this.puntos = 0.0;
        }
       if(this.puntos > oferta.getPuntos()){
           this.puntos -= oferta.getPuntos();
              this.ofertasCanjeadas.add(oferta);
       }
       else{
           throw new PuntosInsuficienteException("No tienes suficientes puntos para canjear esta oferta");
       }
    }


    public List<Ubicacion> pedirUbicaciones() throws IOException {
       List<Ubicacion> ubicaciones = Recomendador.pedirRecomendacion(this.getUbicacion().getLatitud(), this.getUbicacion().getLongitud(), this.getUbicacion().getRadio());
       for(Ubicacion ubicacion : ubicaciones){
       }
      return ubicaciones;
    }

    public void suscribirseA(Heladera heladera, Suscripcion suscripcion){
      heladera.agregarSuscripcion(suscripcion);
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

    public Colaborador(){
        this.colaboraciones = new ArrayList<>();
        this.tarjetas = new ArrayList<>();
        this.activo = true;
        this.puntos = 0.0;
    }

}
