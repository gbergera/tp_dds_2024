package ar.edu.utn.frba.dds.persistencia;

import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.notificacion.Email;
import ar.edu.utn.frba.dds.models.entities.persona.Tecnico;
import ar.edu.utn.frba.dds.models.entities.persona.TipoPersona;
import ar.edu.utn.frba.dds.models.entities.persona.Visita;
import ar.edu.utn.frba.dds.models.repositories.TecnicoRepository;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TecnicoRepoTest implements WithSimplePersistenceUnit {
/*
        private TecnicoRepository tecnicoRepository;
        private Tecnico tecnico;

        @BeforeEach
        public void setUp() {
            tecnico = new Tecnico();
            tecnico.setNombre("Lucas");
            tecnico.setApellido("Perez");
            tecnico.setTelefono("123");
            tecnico.setMail("adsdasd");
            tecnico.setChatTelegramId("123");
            tecnico.setUsuarioTelegram("juan");


            tecnico.setTipoPersona(TipoPersona.TECNICO);

            Email email = new Email();
            tecnico.setMetodoDeNotificacion(email);


            Visita visita = new Visita();
            visita.setFechaYHora(LocalDateTime.now());
            visita.setHeladeraVisitada(new Heladera());
            visita.setSolucionado(true);
            visita.setDescripcion("Descripcion");
            visita.setFoto("Foto");

            tecnico.agregarVisita(visita);
            this.tecnicoRepository = new TecnicoRepository();
        }

        @Test
        public void testGuardarYBuscarTecnico() {
            tecnicoRepository.guardar(tecnico);
            Tecnico encontrado = (Tecnico) tecnicoRepository.buscar(tecnico.getId());
            assertNotNull(encontrado);
            assertEquals("Lucas", encontrado.getNombre());
        }

        @Test
        public void testBuscarTodos() {
            List tecnicos = tecnicoRepository.buscarTodos();
            assertFalse(tecnicos.isEmpty());
        }

 */
}
