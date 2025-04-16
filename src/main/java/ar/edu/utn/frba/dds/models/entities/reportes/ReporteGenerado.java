package ar.edu.utn.frba.dds.models.entities.reportes;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
public class ReporteGenerado {
    private LocalDate fecha;
    private String detalle;

    public ReporteGenerado(LocalDate fecha, String detalle) {
        this.fecha = fecha;
        this.detalle = detalle;
    }
}
