package ar.edu.utn.frba.dds.models.entities.reportes;

import java.time.LocalDateTime;
import java.util.List;

public interface Reporte {
    List<String> generar(LocalDateTime fecha);
}