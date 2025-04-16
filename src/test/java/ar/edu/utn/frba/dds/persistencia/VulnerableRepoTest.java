package ar.edu.utn.frba.dds.persistencia;

import ar.edu.utn.frba.dds.models.entities.colaboracion.Hijo;
import ar.edu.utn.frba.dds.models.entities.colaboracion.PersonaSituacionVulnerable;
import ar.edu.utn.frba.dds.models.entities.colaboracion.Situacion;
import ar.edu.utn.frba.dds.models.entities.persona.Documento;
import ar.edu.utn.frba.dds.models.entities.persona.TipoDocumento;
import ar.edu.utn.frba.dds.models.entities.ubicacion.DireccionAdaptada;
import ar.edu.utn.frba.dds.models.repositories.PersonaSituacionVulnerableRepository;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class VulnerableRepoTest implements WithSimplePersistenceUnit {
/*
    private PersonaSituacionVulnerableRepository personaSituacionVulnerableRepository;

    private PersonaSituacionVulnerable persona;

    @BeforeEach
    public void setUp() {
        persona = new PersonaSituacionVulnerable();
        persona.setNombre("Jose");
        persona.setApellido("Lopez");
        persona.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        persona.setFechaRegistro(LocalDateTime.now());
        persona.setSituacion(Situacion.CALLE);

        DireccionAdaptada direccion = new DireccionAdaptada();
        direccion.setCalle("Calle Falsa");
        direccion.setNumero(123);
        persona.setDireccion(direccion);

        Documento documento = new Documento();
        documento.setTipoDocumento(TipoDocumento.DNI);
        documento.setNumeroDocumento("12345678");
        persona.setDocumento(documento);

        List<Hijo> hijos = new ArrayList<>();
        Hijo hijo = new Hijo();
        hijo.setNombre("Hijo1");
        hijo.setApellido("Lpex");
        hijo.setFechaNacimiento(LocalDate.of(2010, 1, 1));
        hijos.add(hijo);
        persona.setHijos(hijos);

        this.personaSituacionVulnerableRepository = new PersonaSituacionVulnerableRepository();
    }

    @Test
    public void testGuardarYBuscarPersona() {
        personaSituacionVulnerableRepository.guardar(persona);
        PersonaSituacionVulnerable encontrada = (PersonaSituacionVulnerable) personaSituacionVulnerableRepository.buscar(persona.getId());
        assertNotNull(encontrada);
        assertEquals("Jose", encontrada.getNombre());
    }

    @Test
    public void testBuscarTodas() {
        List personas = personaSituacionVulnerableRepository.buscarTodos();
        assertFalse(personas.isEmpty());
    }

 */
}
