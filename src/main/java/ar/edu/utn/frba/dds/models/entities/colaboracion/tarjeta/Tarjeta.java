package ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta;

import ar.edu.utn.frba.dds.models.entities.colaboracion.PersonaSituacionVulnerable;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import lombok.*;

import javax.persistence.*;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "tarjeta")
public class Tarjeta {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "codigo_alfanumerico")
    private String codigoAlfaNumerico;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "tarjeta_id")
    private List<UsoTarjeta> listaDeUsos;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_vulnerable_id")
    private PersonaSituacionVulnerable persona;

    @Column(name = "habilitada")
    private Boolean estaHabilitada;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "tarjeta_id")
    private List<SolicitudApertura> habilitaciones;

    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "tarjeta_id")
    private List<Apertura> registroDeAperturas;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_tarjeta")
    private TipoTarjeta tipoTarjeta;

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "repartidor_id")
    private Colaborador repartidorDeTarjeta;

    @Column(name = "fecha_inicio_de_uso")
    private LocalDate fechaInicioDeUso;

    @Column(name = "fecha_fin_de_uso")
    private LocalDate fechaFinDeUso;


    public Tarjeta() {
        this.listaDeUsos = new ArrayList<>();
        this.habilitaciones = new ArrayList<>();
        this.registroDeAperturas = new ArrayList<>();
        this.codigoAlfaNumerico = generarCodigoAlfanumerico();
    }

    public void agregarUso(UsoTarjeta uso){
        this.listaDeUsos.add(uso);
    }

    public Integer usosDiarios(){
        return 4 + (2 * persona.getHijos().size());
    }

    public Integer cantidadUsosDiariosDisponibles(LocalDate fecha){
        Integer usadosEnElDia = 0;
        usadosEnElDia = this.listaDeUsos.stream().filter(u -> u.getFechaDeUso().equals(fecha)).toList().size();
        return this.usosDiarios() - usadosEnElDia;
    }
    public void darDeBaja(){
        this.estaHabilitada = false;
        this.fechaFinDeUso = LocalDate.now();
    }

    public void agregarHabilitacion(SolicitudApertura solicitud){
        this.habilitaciones.add(solicitud);
    }

    public void agregarApertura(Apertura apertura){
        this.registroDeAperturas.add(apertura);
    }

    public static String generarCodigoAlfanumerico() {
        SecureRandom random = new SecureRandom();
        StringBuilder codigo = new StringBuilder(TarjetaConfig.LONGITUD_CODIGO);

        for (int i = 0; i < TarjetaConfig.LONGITUD_CODIGO; i++) {
            int indice = random.nextInt(TarjetaConfig.CARACTERES.length());
            codigo.append(TarjetaConfig.CARACTERES.charAt(indice));
        }

        return codigo.toString();
    }
}
