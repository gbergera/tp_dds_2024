package ar.edu.utn.frba.dds.models.entities.incidente;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Data
@Getter
@Setter
@Entity
@Table(name = "TipoIncidente")

public class TipoIncidente {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "descripcion")
    private String descripcion;

    public TipoIncidente(String descripcion) {
        this.descripcion = descripcion;
    }


    public TipoIncidente() {

    }
}
