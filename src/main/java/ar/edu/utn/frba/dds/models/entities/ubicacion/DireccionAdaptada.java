package ar.edu.utn.frba.dds.models.entities.ubicacion;

import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.ServiceGeoref;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Localidad;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Provincia;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Setter
@Entity
@Table(name = "direccion")
public class DireccionAdaptada {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "calle")
    private String calle;

    @Column(name = "numero")
    private Integer numero;

    @JsonIgnore
    @ManyToOne(cascade =  {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "localidad_id")
    private Localidad localidad;

    @Column(name = "cached_direccion_completa")
    private String cachedDireccionCompleta;

    @PrePersist
    public void prePersist() throws IOException {
        actualizarDireccionCompleta();
    }

    public String direccionCompleta() throws IOException {
        if (cachedDireccionCompleta == null) {
            try {
                actualizarDireccionCompleta();
            } catch (IOException e) {
                // Log the exception and set a default value
                Logger.getLogger(DireccionAdaptada.class.getName()).log(Level.WARNING, "Error actualizando Direccion", e);
                cachedDireccionCompleta = this.calle + " " + this.numero + ", " + this.localidad.getMunicipio().getNombre() + ", " + this.localidad.getMunicipio().getProvincia().getNombre();
            }
        }
        return cachedDireccionCompleta;
    }

    public void actualizarDireccionCompleta() throws IOException {
        ServiceGeoref servicio = ServiceGeoref.instancia();
        var direcciones = servicio.direccionExactaSegunProvincia(calle, numero, localidad.getMunicipio().getProvincia().getNombre()).direcciones;
        if (direcciones.isEmpty()) {
            cachedDireccionCompleta = this.calle + " " + this.numero + ", " + this.localidad.getMunicipio().getNombre() + ", " + this.localidad.getMunicipio().getProvincia().getNombre();
        } else {
            cachedDireccionCompleta = Objects.requireNonNullElseGet(direcciones.get(0).nomenclatura, () -> this.calle + " " + this.numero + ", " + this.localidad.getMunicipio().getNombre() + ", " + this.localidad.getMunicipio().getProvincia().getNombre());
        }
    }


}
