package ar.edu.utn.frba.dds.models.entities.reportes;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import javax.persistence.EntityTransaction;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ViandasRetiradasColocadas implements Reporte, WithSimplePersistenceUnit {
    private List<Heladera> heladeras;

    public ViandasRetiradasColocadas(List<Heladera> heladeras) {
        this.heladeras = heladeras;
    }

    @Override
    public List<String> generar(LocalDateTime fecha) {
        List<String> reporte = new ArrayList<>();
        EntityTransaction tx = entityManager().getTransaction();
        tx.begin();
        for (Heladera heladera : heladeras) {
            String lineaReporte = "Heladera: " + heladera.getNombre() + "; Viandas Retiradas: " + heladera.getViandasRetiradasHistorial()
                    + "; Viandas Colocadas: " + heladera.getViandasColocadasHistorial();
            reporte.add(lineaReporte);
            heladera.resetearContadorViandasReporte();
            entityManager().merge(heladera);
        }
        tx.commit();
        return reporte;
    }
}
