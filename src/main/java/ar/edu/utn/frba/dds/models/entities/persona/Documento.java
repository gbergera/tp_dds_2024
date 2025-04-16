package ar.edu.utn.frba.dds.models.entities.persona;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Embeddable
public class Documento {

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento")
    private TipoDocumento tipoDocumento;

    @Column(name = "nro_de_documento")
    private String numeroDocumento;
}
