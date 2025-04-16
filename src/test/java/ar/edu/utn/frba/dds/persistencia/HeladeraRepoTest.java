package ar.edu.utn.frba.dds.persistencia;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.ubicacion.DireccionAdaptada;
import ar.edu.utn.frba.dds.models.entities.ubicacion.Ubicacion;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Localidad;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Municipio;
import ar.edu.utn.frba.dds.models.entities.ubicacion.georef.entities.Provincia;
import ar.edu.utn.frba.dds.models.repositories.HeladeraRepository;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HeladeraRepoTest implements WithSimplePersistenceUnit {
/*
    private HeladeraRepository heladeraRepository;
    private Heladera heladera;

    @BeforeEach
    public void setUp() {
        heladera = new Heladera();
        heladera.setNombre("Heladera Test");
        heladera.setPeso(100);
        heladera.setFechaInicioFuncionamiento(LocalDate.now());
        heladera.setActiva(true);
        heladera.setUltimaTemperaturaRegistrada(30.0);

        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setLatitud(123.456F);
        ubicacion.setLongitud(456.789F);
        heladera.cambiarUbicacion(ubicacion);

        DireccionAdaptada direccion = new DireccionAdaptada();
        direccion.setCalle("Cabilo");
        direccion.setNumero(123);
        Localidad localidad = new Localidad();
        localidad.setNombre("Belgrano");
        Municipio municipio = new Municipio();
        municipio.setNombre("CABA");
        Provincia provincia = new Provincia();
        provincia.setNombre("Buenos Aires");
        municipio.setProvincia(provincia);
        localidad.setMunicipio(municipio);
        direccion.setLocalidad(localidad);
        heladera.cambiarDireccion(direccion);
        heladera.setViandasColocadas(13);

        this.heladeraRepository = new HeladeraRepository();
    }


    @Test
    public void testGuardarYBuscarHeladera() {
        heladeraRepository.guardar(heladera);
        Heladera encontrada = (Heladera) heladeraRepository.buscar(heladera.getId());
        assertNotNull(encontrada);
        assertEquals("Heladera Test", encontrada.getNombre());
    }

    @Test
    public void testBuscarTodas() {
        List heladeras = heladeraRepository.buscarTodos();
        assertFalse(heladeras.isEmpty());
    }

 */
}
