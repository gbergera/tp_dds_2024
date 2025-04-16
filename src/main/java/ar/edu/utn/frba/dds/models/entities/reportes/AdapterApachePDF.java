package ar.edu.utn.frba.dds.models.entities.reportes;
import ar.edu.utn.frba.dds.models.entities.reportes.config.Config;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.List;


public class AdapterApachePDF {
    private String nombreDeArchivo;

    public AdapterApachePDF(String nombreDeArchivo) {
        this.nombreDeArchivo = nombreDeArchivo;
    }

    public String exportar(Documento documento){
        PDDocument doc = new PDDocument();
        PDPage myPage = new PDPage();
        doc.addPage(myPage);
        PDPageContentStream cont = null;
        try {
            cont = new PDPageContentStream(doc, myPage);
            cont.beginText();
            cont.setNonStrokingColor(30, 133, 133);
            cont.setFont(PDType1Font.HELVETICA_BOLD, 18);
            cont.newLineAtOffset(230, 750);
            cont.showText("Reporte " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            cont.endText();

            cont.beginText();
            cont.setFont(PDType1Font.HELVETICA, 12);
            cont.setLeading(14.5f);
            cont.newLineAtOffset(25, 700);
            cont.endText();

            agregarDatosComoTabla(cont, documento.getDatos(), doc);

            cont.close();

            doc.save(rutaCompletaDelArchivo());

            doc.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (cont != null) {
                try {
                    cont.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                doc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rutaCompletaDelArchivo();
    }

    private String rutaCompletaDelArchivo(){
        return Paths.get(Config.RUTA_EXPORTACION, this.nombreDeArchivo).toString();
    }

    private void agregarDatosComoTabla(PDPageContentStream pagina, Map<String, List<String>> datos, PDDocument doc) throws IOException {
        float margin = 50;
        float yStart = 700;
        float tableWidth = 500;
        float yPosition = yStart;
        float rowHeight = 20;
        float cellMargin = 5;

        for (Map.Entry<String, List<String>> entry : datos.entrySet()) {
            String titulo = entry.getKey();
            List<String> lineas = entry.getValue();

            pagina.beginText();
            pagina.setNonStrokingColor(30, 133, 133);
            pagina.setFont(PDType1Font.HELVETICA, 16);
            yPosition -= (rowHeight * 1.5);
            pagina.newLineAtOffset(margin, yPosition);
            switch (titulo) {
                case "ViandasColaborador":
                    pagina.showText("Viandas donadas por Colaborador");
                    break;
                case "ReportesFallaHeladera":
                    pagina.showText("Fallas por Heladera");
                    break;
                case "ViandasRetiradasColocadas":
                    pagina.showText("Movimiento de viandas por Heladera");
                    break;
                default:
                    pagina.showText(titulo);
            }
            pagina.endText();
            yPosition -= (rowHeight * 0.5);

            String[] headers;
            if (titulo.contains("ViandasColaborador")) {
                headers = new String[]{"Colaborador", "Viandas Donadas"};
            } else if (titulo.contains("Falla")) {
                headers = new String[]{"Heladera", "Fallas"};
            } else {
                headers = new String[]{"Heladera", "Viandas Retiradas", "Viandas Colocadas"};
            }

            float[] columnWidths = new float[headers.length];
            for (int i = 0; i < headers.length; i++) {
                columnWidths[i] = tableWidth / headers.length;
            }

            for (int i = 0; i < headers.length; i++) {
                pagina.setLineWidth(0.5f);
                pagina.addRect(margin + i * columnWidths[i], yPosition - rowHeight, columnWidths[i], rowHeight);
                pagina.stroke();
                pagina.beginText();
                pagina.setNonStrokingColor(0, 0, 0);
                pagina.setFont(PDType1Font.HELVETICA_BOLD, 12);
                pagina.newLineAtOffset(margin + i * columnWidths[i] + cellMargin, yPosition - 15);
                pagina.showText(headers[i]);
                pagina.endText();
            }
            yPosition -= rowHeight;

            for (String linea : lineas) {
                String[] partes = linea.split(";");

                for (int i = 0; i < columnWidths.length; i++) {
                    pagina.setLineWidth(0.5f);
                    pagina.addRect(margin + i * columnWidths[i], yPosition - rowHeight, columnWidths[i], rowHeight);
                    pagina.stroke();
                    pagina.beginText();
                    pagina.setNonStrokingColor(0, 0, 0);
                    pagina.setFont(PDType1Font.HELVETICA, 12);
                    pagina.newLineAtOffset(margin + i * columnWidths[i] + cellMargin, yPosition - 15);

                    if (i < partes.length) {
                        String valor = partes[i].replaceAll("Fallas:", "").replaceAll("Viandas Retiradas:", "")
                                .replaceAll("Colaborador:", "").replaceAll("Viandas Donadas:", "").replaceAll("Heladera:", "")
                                .replaceAll("Viandas Colocadas:", "");
                        pagina.showText(valor.trim());
                    } else {
                        pagina.showText("");
                    }
                    pagina.endText();
                }
                yPosition -= rowHeight;

                if (yPosition < margin) {
                    pagina.close();
                    PDPage newPage = new PDPage();
                    doc.addPage(newPage);
                    pagina = new PDPageContentStream(doc, newPage);
                    yPosition = yStart;
                }
            }
            yPosition -= rowHeight;
        }
        pagina.close();
    }





    private void agregarDatos(PDPageContentStream pagina, Map<String, List<String>> datos) throws IOException {
        float y = 700;
        for (Map.Entry<String, List<String>> entry : datos.entrySet()) {
            //String titulo = entry.getKey();
            String titulo ="Reportes";
            List<String> lineas = entry.getValue();

            // Escribir el título
            pagina.setFont(PDType1Font.HELVETICA_BOLD, 14);
            pagina.showText(titulo);
            pagina.newLine();

            // Escribir las líneas de datos
            pagina.setFont(PDType1Font.HELVETICA, 12);
            for (String linea : lineas) {
                pagina.showText("  - " + linea);
                pagina.newLine();
            }

            // Separación entre secciones de reporte
            pagina.newLine();
        }
    }
    private void agregarHeaderFooter(PDPageContentStream cont, PDPage page) throws IOException {
        cont.beginText();
        cont.setFont(PDType1Font.TIMES_ITALIC, 10);
        cont.newLineAtOffset(25, 800);
        cont.showText("Encabezado del Reporte");
        cont.endText();

        cont.beginText();
        cont.setFont(PDType1Font.HELVETICA, 10);
        cont.newLineAtOffset(25, 25);
        cont.showText("Reporte generado Semanalmente por la aplicación");
        cont.endText();
    }
}
