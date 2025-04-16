package ar.edu.utn.frba.dds.models.entities.reportes;


import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViandasColaborador implements Reporte{


    private List<Heladera> heladeras;

    public ViandasColaborador(List<Heladera> heladeras) {
        this.heladeras = heladeras;
    }


    @Override
    public List<String> generar(LocalDateTime fecha) {
        Map<Colaborador, Integer> viandasPorColaborador = new HashMap<>();

        for (Heladera heladera : heladeras) {
            Map<Colaborador, Integer> conteo = heladera.contarViandasDonadasPorColaborador();
            for (Map.Entry<Colaborador, Integer> entry : conteo.entrySet()) {
                Colaborador colaborador = entry.getKey();
                int cantidad = entry.getValue();
                viandasPorColaborador.put(colaborador, viandasPorColaborador.getOrDefault(colaborador, 0) + cantidad);
            }
        }

        List<String> reporte = new ArrayList<>();
        for (Map.Entry<Colaborador, Integer> entry : viandasPorColaborador.entrySet()) {
            Colaborador colaborador = entry.getKey();
            if (colaborador != null) {
                String lineaReporte = "Colaborador: " + colaborador.getNombre() + "; Viandas Donadas: " + entry.getValue();
                reporte.add(lineaReporte);
            } else {
                String lineaReporte = "Colaborador: desconocido; Viandas Donadas: " + entry.getValue();
                reporte.add(lineaReporte);
            }
        }

        return reporte;
    }
}
