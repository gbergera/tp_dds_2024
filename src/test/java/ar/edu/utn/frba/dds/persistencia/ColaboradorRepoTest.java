package ar.edu.utn.frba.dds.persistencia;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Vianda;
import ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta.*;
import ar.edu.utn.frba.dds.models.entities.notificacion.Whatsapp;
import ar.edu.utn.frba.dds.models.entities.persona.*;
import ar.edu.utn.frba.dds.models.entities.ubicacion.Ubicacion;
import ar.edu.utn.frba.dds.models.repositories.ColaboradorRepository;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ColaboradorRepoTest implements WithSimplePersistenceUnit {
    /*

    private ColaboradorRepository colaboradorRepository;
    private Colaborador colaborador;

    @BeforeEach
    public void setUp() throws MqttException {

        Whatsapp whatsapp = new Whatsapp();


        colaborador = new Colaborador();
        colaborador.setNombre("Juan");
        colaborador.setApellido("Test");
        colaborador.setTelefono("12345678");
        colaborador.setMail("juan@gmail.com");
        colaborador.setPuntos(0.0);
        colaborador.setTipoPersona(TipoPersona.FISICA);
        colaborador.setMetodoDeNotificacion(whatsapp);


        // Crear y agregar documento
        Documento documento = new Documento();
        documento.setTipoDocumento(TipoDocumento.DNI);
        documento.setNumeroDocumento("12345678");
        colaborador.setDocumento(documento);

        // Crear y agregar ubicacion
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setLatitud(123.456F);
        ubicacion.setLongitud(456.789F);
        colaborador.setUbicacion(ubicacion);

        Whatsapp whatsapp1 = new Whatsapp();
        colaborador.setMetodoDeNotificacion(whatsapp1);

        Tarjeta tarjeta = new Tarjeta();
        tarjeta.setCodigoAlfaNumerico("123456");
        tarjeta.setEstaHabilitada(true);
        tarjeta.setTipoTarjeta(TipoTarjeta.COLABORADOR);
        colaborador.agregarTarjeta(tarjeta);

        Heladera heladera = new Heladera();
        heladera.setNombre("Heladera 1");
        heladera.setPeso(100);
        heladera.setFechaInicioFuncionamiento(LocalDate.now());
        heladera.setActiva(true);

        // Persist Heladera before using it in SolicitudApertura
        entityManager().persist(heladera);

        UsoTarjeta usoTarjeta = new UsoTarjeta();
        usoTarjeta.setFechaDeUso(LocalDate.now());
        usoTarjeta.setHeladeraUsada(heladera);
        usoTarjeta.setVianda(new Vianda());

        SolicitudApertura solicitudApertura = new SolicitudApertura();
        solicitudApertura.setFechaSolicitud(LocalDateTime.now());
        solicitudApertura.setFueExitosa(true);
        solicitudApertura.setHeladeraAAbrir(heladera);

        Apertura apertura = new Apertura();
        apertura.setFechaYHora(LocalDateTime.now());
        apertura.setHeladera(heladera);
        apertura.setSolicitudApertura(solicitudApertura);


        tarjeta.agregarUso(usoTarjeta);
        tarjeta.agregarApertura(apertura);
        tarjeta.agregarHabilitacion(solicitudApertura);

        this.colaboradorRepository = new ColaboradorRepository();
    }

    @Test
    public void testGuardarYBuscarColaborador() {
        colaboradorRepository.guardar(colaborador);
        Colaborador encontrado = (Colaborador) colaboradorRepository.buscar(colaborador.getId());
        assertNotNull(encontrado);
        assertEquals("Juan", encontrado.getNombre());
    }

    @Test
    public void testBuscarTodos() {
        List colaboradores = colaboradorRepository.buscarTodos();
        assertFalse(colaboradores.isEmpty());
    }

     */

}
