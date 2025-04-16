package ar.edu.utn.frba.dds.CSVTest;

import ar.edu.utn.frba.dds.models.entities.colaboracion.Colaboracion;
import ar.edu.utn.frba.dds.models.entities.notificacion.Notificador;
import ar.edu.utn.frba.dds.models.entities.importacionArchivoCSV.CargaMasiva;
import ar.edu.utn.frba.dds.models.entities.importacionArchivoCSV.ColaboradorDTO;
import ar.edu.utn.frba.dds.models.entities.importacionArchivoCSV.ImportadorCSV;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.NotificacionBuilder;
import ar.edu.utn.frba.dds.models.entities.notificacion.builder.NotificacionNuevoColaborador;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.persona.Documento;
import ar.edu.utn.frba.dds.models.entities.persona.TipoDocumento;
import ar.edu.utn.frba.dds.models.entities.persona.builder.ColaboradorBuilder;
import ar.edu.utn.frba.dds.models.entities.persona.builder.IColaboradorBuilder;
import ar.edu.utn.frba.dds.models.repositories.ColaboradorRepository;
import ar.edu.utn.frba.dds.models.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.mail.MessagingException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CSVTest {
/*
    private CargaMasiva cargaMasiva;
    Notificador enviadorMailMock = mock(Notificador.class);
    NotificacionBuilder notificacionBuilderMock = new NotificacionNuevoColaborador();
    ColaboradorBuilder colaboradorBuilderMock = new ColaboradorBuilder();
    ColaboradorRepository repoDeColaboradores = new ColaboradorRepository();
    UsuarioRepository repoDeUsuarios = new UsuarioRepository();
    @BeforeEach
    public void setup() {
        cargaMasiva = new CargaMasiva(enviadorMailMock, notificacionBuilderMock, colaboradorBuilderMock, repoDeColaboradores, repoDeUsuarios);

    }

    @Test
    public void procesarDatosColaboradorExistente() throws MessagingException, IOException {
        Colaborador colaboradorExistente = new Colaborador();
        Documento documento = new Documento();
        documento.setTipoDocumento(TipoDocumento.DNI);
        documento.setNumeroDocumento("11111111");
        colaboradorExistente.setDocumento(documento);
        colaboradorExistente.setNombre("Jose");
        colaboradorExistente.setApellido("gimenez");
        colaboradorExistente.setMail("juangimenez@gmail.com");


        cargaMasiva.agregarColaborador(colaboradorExistente);

        ColaboradorDTO dto = new ColaboradorDTO(
                "DNI", "11111111", "Jose", "gimenez", "joniraij@gmail.com",
                LocalDate.of(2024, 5, 20), "DINERO", 1000
        );

        cargaMasiva.procesarDatos(dto);

        assertEquals(1, colaboradorExistente.getColaboraciones().size());
        Colaboracion colaboracion = colaboradorExistente.getColaboraciones().get(0);
        assertEquals(LocalDate.of(2024, 5, 20), colaboracion.getFechaColaboracion());
        assertEquals("DonacionDinero", colaboracion.getClass().getSimpleName());
        assertEquals(1000, colaboracion.getCantidad());

        verify(enviadorMailMock, never()).enviar(any());


    }

    @Test
    public void procesarDatosNuevoColaborador() throws MessagingException, IOException {
        ColaboradorDTO dto = new ColaboradorDTO(
                "DNI", 4545875, "JONATAN", "RAIJMAN", "joniraij@gmail.com",
                LocalDate.of(2024, 5, 20), "DINERO", 1000
        );

        cargaMasiva.procesarDatos(dto);

        Colaborador nuevoColaborador = cargaMasiva.getTodosLosColaboradores().get(0);
        assertEquals("JONATAN", nuevoColaborador.getNombre());
        assertEquals("RAIJMAN", nuevoColaborador.getApellido());
        assertEquals("joniraij@gmail.com", nuevoColaborador.getMail());
        assertEquals(TipoDocumento.DNI, nuevoColaborador.getDocumento().getTipoDocumento());
        assertEquals(4545875, nuevoColaborador.getDocumento().getNumeroDocumento());

        assertEquals(1, nuevoColaborador.getColaboraciones().size());
        Colaboracion colaboracion = nuevoColaborador.getColaboraciones().get(0);
        assertEquals(LocalDate.of(2024, 5, 20), colaboracion.getFechaColaboracion());
        assertEquals("DonacionDinero", colaboracion.getClass().getSimpleName());
        assertEquals(1000, colaboracion.getCantidad());

        verify(enviadorMailMock).enviar(any());
    }

    @Test
    public void cargaCSVTest() throws MessagingException, IOException {
        // Crear instancia de ImportadorCSV con el mock de EnviadorMail
        ImportadorCSV importadorCSV = new ImportadorCSV();
        // Ejecutar la importación del archivo CSV de prueba
        importadorCSV.cargarCSV("src/test/java/ar/edu/utn/frba/dds/CSVTest/ejemplo.csv", enviadorMailMock, notificacionBuilderMock, colaboradorBuilderMock);

        verify(enviadorMailMock, times(5)).enviar(any());
    }
    */
}