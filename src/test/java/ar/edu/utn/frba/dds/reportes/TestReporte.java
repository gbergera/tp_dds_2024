package ar.edu.utn.frba.dds.reportes;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.reportes.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
//TODO
public class TestReporte {
/*
    private GeneradorDeReportes generadorDeReportes;
    private AdapterApachePDF adapterApachePDF;
    private ExportadorAPDF exportadorAPDF;

    @BeforeEach
    public void setUp() {
        adapterApachePDF = new AdapterApachePDF("testReporte.pdf");
        exportadorAPDF = new ExportadorAPDF(adapterApachePDF);
        //generadorDeReportes = new GeneradorDeReportes(exportadorAPDF);
    }

    @Test
    public void testGenerarYExportarPDF() throws IOException {
        // Crear un mock de Reporte
        Reporte reporteMock = (LocalDateTime fecha) -> Arrays.asList("Heladera: aaa, Fallas: 0", "Heladera: bbb, Fallas: 1", "Heladera: ccc, Fallas: 2");
        Reporte reporteMock2 = (LocalDateTime fecha) -> Arrays.asList("Colaborador: Jose, viandas Donadas: 15", "Colaborador: Marcos, viandas Donadas: 15", "Colaborador: Martin, viandas Donadas: 15");
        Reporte reporteMock3 = (LocalDateTime fecha) -> Arrays.asList("Heladera: aaa , Viandas Retiradas: 10 , Viandas Colocadas: 20","Heladera: bbb , Viandas Retiradas: 10 , Viandas Colocadas: 20");

        // Agregar el reporte al generador
        generadorDeReportes.agregarReporte(reporteMock);
        generadorDeReportes.agregarReporte(reporteMock2);
        generadorDeReportes.agregarReporte(reporteMock3);

        // Generar el reporte
        generadorDeReportes.generarReporte(LocalDate.of(2023, 7, 1));

        // Exportar el PDF
        generadorDeReportes.exportarPDF();

        // Verificar que el archivo se haya creado
        File archivoPDF = new File("src/test/java/ar/edu/utn/frba/dds/reportes/testReporte.pdf");
        assertTrue(archivoPDF.exists());

    }

 */
}
