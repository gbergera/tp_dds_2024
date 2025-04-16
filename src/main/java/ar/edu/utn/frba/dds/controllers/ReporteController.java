package ar.edu.utn.frba.dds.controllers;

import ar.edu.utn.frba.dds.config.ServiceLocator;
import ar.edu.utn.frba.dds.models.entities.reportes.*;

import ar.edu.utn.frba.dds.models.entities.reportes.config.Config;
import ar.edu.utn.frba.dds.models.repositories.HeladeraRepository;
import io.javalin.http.Context;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReporteController {
    private Logger logger = Logger.getLogger(ReporteController.class.getName());

    public ReporteController(GeneradorDeReportes generadorDeReportes) {

    }

    public List<ReporteGenerado> obtenerReportes() {
        List<ReporteGenerado> reportesGenerados = new ArrayList<>();
        Path directorioReportes = Paths.get("upload/reportes");


        try (DirectoryStream<Path> stream = Files.newDirectoryStream(directorioReportes, "*.pdf")) {
            for (Path entry : stream) {
                String fileName = entry.getFileName().toString();
                if (fileName.matches("reporte_semanal_\\d{2}_\\d{2}_\\d{4}\\.pdf")) {
                    String datePart = fileName.substring(16, fileName.lastIndexOf('.'));
                    LocalDate fecha = LocalDate.parse(datePart, DateTimeFormatter.ofPattern("dd_MM_yyyy"));
                    reportesGenerados.add(new ReporteGenerado(fecha, entry.toString()));
                }
            }
        }  catch (Exception e) {
            logger.log(Level.SEVERE, "Error al leer el directorio de reportes: " + e.getMessage(), e);
        }

        return reportesGenerados;
    }

    public void descargarReportePorFecha(String fecha, Context ctx) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_MM_yyyy");
        String formattedDate = LocalDate.parse(fecha, DateTimeFormatter.ofPattern("yyyy-MM-dd")).format(formatter);
        String nombreArchivoPDF = "reporte_semanal_" + formattedDate + ".pdf";
        Path rutaPDF = Paths.get(Config.RUTA_EXPORTACION, nombreArchivoPDF);

        // Generar el reporte si no existe
        if (!Files.exists(rutaPDF)) {
            try {
                GeneradorDeReportes generador = ServiceLocator.instanceOf(GeneradorDeReportes.class);
                generador.generarReporte(LocalDate.now());
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error al generar el reporte instantáneo: " + e.getMessage(), e);
                ctx.status(500);
                ctx.sessionAttribute("error", "Error al generar el reporte instantáneo.");
                ctx.redirect("/reportes");
                return;
            }
        }

        // Intentar descargar el archivo
        try {
            ctx.contentType("application/pdf");
            ctx.header("Content-Disposition", "attachment; filename=\"" + nombreArchivoPDF + "\"");
            Files.copy(rutaPDF, ctx.outputStream());
            ctx.outputStream().flush();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error al procesar la descarga del reporte: " + e.getMessage(), e);
            ctx.status(500);
            ctx.sessionAttribute("error", "Error al procesar la descarga del reporte.");
            ctx.redirect("/reportes");
        }
    }




}