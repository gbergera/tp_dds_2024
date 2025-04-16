package ar.edu.utn.frba.dds.models.entities.colaboracion.comida;

import ar.edu.utn.frba.dds.models.entities.incidente.ReporteDeIncidentes;
import ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta.Tarjeta;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.suscripciones.Suscripcion;
import ar.edu.utn.frba.dds.models.entities.ubicacion.DireccionAdaptada;
import ar.edu.utn.frba.dds.models.entities.ubicacion.Ubicacion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.mail.MessagingException;
import javax.persistence.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@Entity
@Table(name = "heladera")
public class Heladera {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
            name = "heladera_ubicacion",
            joinColumns = @JoinColumn(name = "heladera_id"),
            inverseJoinColumns = @JoinColumn(name = "ubicacion_id"))
    @OrderBy("id ASC") // Ordena por id en orden ascendente
    private List<Ubicacion> ubicaciones = new ArrayList<>();


    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(
            name = "heladera_direccion",
            joinColumns = @JoinColumn(name = "heladera_id"),
            inverseJoinColumns = @JoinColumn(name = "direccion_id"))
    @OrderBy("id ASC") // Ordena por id en orden ascendente
    private List<DireccionAdaptada> direcciones = new ArrayList<>();


    @OneToMany(mappedBy = "heladera", fetch = FetchType.LAZY)
    private List<Vianda> viandas = new ArrayList<>();

    @Column(name = "peso")
    private Integer peso;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    @JoinColumn(name = "heladera_id")
    private List<Suscripcion> suscripciones = new ArrayList<Suscripcion>();

    @Column(name = "fecha_inicio_funcionamiento")
    private LocalDate fechaInicioFuncionamiento;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "administrador_id")
    private Colaborador administrador;

    @Column(name = "activa")
    private Boolean activa;

    @Column(name = "fecha_fin_funcionamiento")
    private LocalDate fechaFinFuncionamiento;

    @JsonIgnore
    @OneToMany(mappedBy = "heladeraAfectada", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private List<ReporteDeIncidentes> registroDeFallos = new ArrayList<ReporteDeIncidentes>();

    //NO SE PERSISTE
    @JsonIgnore
    @Transient
    private List<Tarjeta> tarjetasHabilitadas;

    @Column(name = "cant_viandas_colocadas")
    private Integer viandasColocadas = 0;

    @Column(name = "cant_viandas_retiradas")
    private Integer viandasRetiradasHistorial = 0;
    @Column(name = "cant_viandas_colocadas_historial")
    private Integer viandasColocadasHistorial = 0;

    @Column(name = "ultima_temperatura_registrada")
    private Double ultimaTemperaturaRegistrada;
    @Column(name = "temperatura_minima")
    private Double temperaturaMinima;
    @Column(name = "temperatura_maxima")
    private Double temperaturaMaxima;

    @Column(name = "capacidad_de_viandas")
    private Integer capacidad;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER )
    @JoinColumn(name = "modelo_id")
    private ModeloHeladera modelo;

    @JsonIgnore
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER )
    @JoinTable(
            name = "heladera_heladeraCercana",
            joinColumns = @JoinColumn(name = "heladera_id"),
            inverseJoinColumns = @JoinColumn(name = "heladera_cercana_id"))
    private List<Heladera> heladerasCercanas = new ArrayList<>();
    @Transient
    private String direccionCompleta;

    @Column(name = "topicMQTT")
    private String topicMQTT;
    public void verificarCondicionSuscripcion(){
        this.suscripciones.forEach(s -> {
            try {
                s.verificarCondicion(this);
            } catch (MessagingException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
    public void agregarFalla(ReporteDeIncidentes reporte){
        this.registroDeFallos.add(reporte);
    }
    public void agregarSuscripcion(Suscripcion suscripcion){
        this.suscripciones.add(suscripcion);
    }

    public Integer diasActiva(){
        if (this.activa) {
            return (int) ChronoUnit.DAYS.between(this.fechaInicioFuncionamiento, LocalDate.now());
        } else {
            return (int) ChronoUnit.DAYS.between(this.fechaInicioFuncionamiento, this.fechaFinFuncionamiento);
        }
    }
    public void agregarVianda(Vianda vianda){
        this.viandas.add(vianda);
        vianda.setHeladera(this);
        this.verificarCondicionSuscripcion();
        this.viandasColocadas = this.viandas.size();
        this.viandasColocadasHistorial++;
    }

    public void retirarVianda(Vianda vianda){
        this.viandas.remove(vianda);
        vianda.setHeladera(null);
        this.verificarCondicionSuscripcion();
        this.viandasColocadas = this.viandas.size();
        this.viandasRetiradasHistorial++;
    }

    public void resetearContadorViandasReporte() {
        this.viandasColocadasHistorial = 0;
        this.viandasRetiradasHistorial = 0;
    }

    public Integer cantDeViandas(){
        return this.viandas.size();
    }

    public Ubicacion ubicacionActual(){
        if (ubicaciones == null || ubicaciones.isEmpty()) {
            return null;
        }

        return ubicaciones.get(ubicaciones.size() - 1);
    }

    public DireccionAdaptada direccionActual(){
        if (direcciones == null || direcciones.isEmpty()) {
            return null;
        }

        return direcciones.get(direcciones.size() - 1);
    }

    public void cambiarUbicacion(Ubicacion nuevaUbicacion){
        this.ubicaciones.add(nuevaUbicacion);
    }

    public void cambiarDireccion(DireccionAdaptada nuevaDireccion){
        this.direcciones.add(nuevaDireccion);
    }
    public Map<Colaborador, Integer> contarViandasDonadasPorColaborador() {
        Map<Colaborador, Integer> viandasPorColaborador = new HashMap<>();
        for (Vianda vianda : viandas) {
            Colaborador donador = vianda.getColaborador();
            viandasPorColaborador.put(donador, viandasPorColaborador.getOrDefault(donador, 0) + 1);
        }
        return viandasPorColaborador;
    }

    public void agregarHeladeraCercana(Heladera heladera){
        if(!this.heladerasCercanas.contains(heladera)){
            this.heladerasCercanas.add(heladera);
            heladera.agregarHeladeraCercana(this);
        }
    }


}
