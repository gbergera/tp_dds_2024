package ar.edu.utn.frba.dds.models.entities.importacionArchivoCSV;

import ar.edu.utn.frba.dds.models.entities.notificacion.Notificador;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.NotificacionBuilder;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.persona.builder.IColaboradorBuilder;
import ar.edu.utn.frba.dds.models.repositories.ColaboracionRepository;
import ar.edu.utn.frba.dds.models.repositories.ColaboradorRepository;
import ar.edu.utn.frba.dds.models.repositories.UsuarioRepository;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import org.slf4j.simple.SimpleLogger;

import javax.mail.MessagingException;
import javax.persistence.EntityTransaction;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImportadorCSV {
    public void cargarCSV(String nombreFicheroCSV, Notificador enviador, NotificacionBuilder notificacionBuilder,
                          IColaboradorBuilder colaboradorBuilder, ColaboradorRepository colaboradorRepository,
                          UsuarioRepository usuarioRepository) {
        CargaMasiva cargaMasiva = new CargaMasiva(enviador, notificacionBuilder, colaboradorBuilder, colaboradorRepository, usuarioRepository);
        BufferedReader br = null;

        Logger logger = Logger.getLogger(ImportadorCSV.class.getName());


        colaboradorRepository.abrirTransaccion();

        try {
            br = new BufferedReader(new FileReader(nombreFicheroCSV));
            String linea;
            List<ColaboradorDTO> dataValueObjects = new ArrayList<>();

            while ((linea = br.readLine()) != null) {
                String[] datoslinea = linea.split(",");
                if (datoslinea.length != 8) {
                    continue; // Skip this line
                }

                try {
                    String tipoDocumento = datoslinea[0].trim();
                    String documento = datoslinea[1].trim();
                    String nombre = datoslinea[2].trim();
                    String apellido = datoslinea[3].trim();
                    String mail = datoslinea[4].trim();
                    LocalDate fechaColaboracion = LocalDate.parse(datoslinea[5].trim());
                    String formaColaboracion = datoslinea[6].trim();
                    Integer cantidad = Integer.valueOf(datoslinea[7].trim());

                    dataValueObjects.add(new ColaboradorDTO(
                            tipoDocumento,
                            documento,
                            nombre,
                            apellido,
                            mail,
                            fechaColaboracion,
                            formaColaboracion,
                            cantidad
                    ));
                } catch (Exception e) {
                    logger.log(Level.SEVERE, "Error al procesar la línea: " + linea+ " - " + e.getMessage(), e);
                }
            }

            cargaMasiva.procesarDatos(dataValueObjects);
            colaboradorRepository.commitTransaccion();

        } catch (Exception e) {
            colaboradorRepository.rollbackTransaccion();
            throw new RuntimeException("Error al procesar el archivo: " + e.getMessage(), e);
        } finally {
            // Asegúrate de cerrar el BufferedReader
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    throw new RuntimeException("Error al cerrar el BufferedReader: " + e.getMessage(), e);
                }
            }
        }
    }


}
