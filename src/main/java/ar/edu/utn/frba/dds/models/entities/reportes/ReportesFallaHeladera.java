package ar.edu.utn.frba.dds.models.entities.reportes;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.incidente.ReporteDeIncidentes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReportesFallaHeladera implements Reporte{
    private List<Heladera> heladeras;

    public ReportesFallaHeladera() {
        this.heladeras =new ArrayList<>();
    }

    public ReportesFallaHeladera(List<Heladera> heladeras) {
        this.heladeras = heladeras;
    }
    @Override
    public List<String> generar(LocalDateTime fecha) {
        Map<String, Integer> fallasPorHeladera = new HashMap<>();
        for (Heladera heladera : heladeras) {
            List <ReporteDeIncidentes> fallosAPartirDeFecha = heladera.getRegistroDeFallos().stream().filter(f -> f.getFechaYHora().isAfter(fecha)).toList();
            fallasPorHeladera.put(heladera.getNombre(), fallosAPartirDeFecha.size());
        }
        List<String> reporte = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : fallasPorHeladera.entrySet()) {
            reporte.add("Heladera: " + entry.getKey() + "; Fallas: " + entry.getValue());
        }
        return reporte;
    }
}

