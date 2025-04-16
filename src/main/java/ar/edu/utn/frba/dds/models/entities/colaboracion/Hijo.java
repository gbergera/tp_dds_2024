package ar.edu.utn.frba.dds.models.entities.colaboracion;

import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.units.qual.C;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;
@Getter
@Setter
@Entity
@Table(name = "hijo")
public class Hijo {
    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "apellido")
    private String apellido;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;


    public Boolean esMenor(){
        LocalDate fechaActual = LocalDate.now();
        Period periodo = Period.between(fechaNacimiento, fechaActual);

        return periodo.getYears()>= 18;
    }
}
