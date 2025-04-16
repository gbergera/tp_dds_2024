package ar.edu.utn.frba.dds.models.entities.reportes;

import ar.edu.utn.frba.dds.config.ServiceLocator;
import ar.edu.utn.frba.dds.models.repositories.HeladeraRepository;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;

@Setter
@Getter
public class GeneradorDeReportes {
    private Documento documentoAExportar;
    private ExportadorAPDF exportador;
    private HeladeraRepository heladeraRepository;
    private Logger logger = Logger.getLogger(GeneradorDeReportes.class.getName());

    public GeneradorDeReportes(ExportadorAPDF exportador, HeladeraRepository heladeraRepository) {
        this.exportador = exportador;
        this.heladeraRepository = heladeraRepository;
    }

    public String exportarPDF() {
        return this.exportador.exportar(documentoAExportar);
    }

    public List<ReporteGenerado> generarReporte(LocalDate fechaInicio) {
        Map<String, List<String>> datosDelDocumento = new HashMap<>();


        ReportesFallaHeladera reporteFallas = new ReportesFallaHeladera(heladeraRepository.buscarTodos());
        List<String> datosFallas = reporteFallas.generar(LocalDateTime.of(fechaInicio, LocalTime.MIN));
        datosDelDocumento.put(reporteFallas.getClass().getSimpleName(), datosFallas);

        ViandasColaborador reporteViandas = new ViandasColaborador(heladeraRepository.buscarTodos());
        List<String> datosViandas = reporteViandas.generar(LocalDateTime.of(fechaInicio, LocalTime.MIN));
        datosDelDocumento.put(reporteViandas.getClass().getSimpleName(), datosViandas);

        ViandasRetiradasColocadas reporteRetiradasColocadas = new ViandasRetiradasColocadas(heladeraRepository.buscarTodos());
        List<String> datosRetiradasColocadas = reporteRetiradasColocadas.generar(LocalDateTime.of(fechaInicio, LocalTime.MIN));
        datosDelDocumento.put(reporteRetiradasColocadas.getClass().getSimpleName(), datosRetiradasColocadas);

        documentoAExportar = new Documento();
        documentoAExportar.setDatos(datosDelDocumento);

        String rutaPDF = exportarPDF();
        List<ReporteGenerado> reportesGenerados = new ArrayList<>();
        reportesGenerados.add(new ReporteGenerado(fechaInicio, rutaPDF));
        return reportesGenerados;
    }

    public void iniciarCron() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                LocalDate fechaActual = LocalDate.now();
                generarReporte(fechaActual);
              logger.info("REPORTE GENERADO --------------------CORRE CADA 7 DIAS---------------");
            }
        }, 0, 7 * 24 * 60 * 60 * 1000); // Repetir cada 7 días (en milisegundos)
    }
}
