package ar.edu.utn.frba.dds.models.entities.persona;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "nombre")
    private String nombreUsuario;

    @Column(name = "contraseña")
    private String contrasenia;

    @Column(name = "estado")
    private Boolean estado;
}
