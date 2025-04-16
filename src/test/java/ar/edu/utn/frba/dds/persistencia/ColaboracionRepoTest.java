package ar.edu.utn.frba.dds.persistencia;

import ar.edu.utn.frba.dds.models.entities.colaboracion.Frecuencia;
import ar.edu.utn.frba.dds.models.entities.colaboracion.PersonaSituacionVulnerable;
import ar.edu.utn.frba.dds.models.entities.colaboracion.Situacion;
import ar.edu.utn.frba.dds.models.entities.colaboracion.TipoDeColaboraciones.*;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Comida;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Heladera;
import ar.edu.utn.frba.dds.models.entities.colaboracion.comida.Vianda;
import ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta.Tarjeta;
import ar.edu.utn.frba.dds.models.entities.colaboracion.tarjeta.TipoTarjeta;
import ar.edu.utn.frba.dds.models.entities.formulario.Opcion;
import ar.edu.utn.frba.dds.models.entities.notificacion.Whatsapp;
import ar.edu.utn.frba.dds.models.entities.persona.*;
import ar.edu.utn.frba.dds.models.entities.puntos.Oferta;
import ar.edu.utn.frba.dds.models.entities.ubicacion.DireccionAdaptada;
import ar.edu.utn.frba.dds.models.repositories.ColaboradorRepository;
import ar.edu.utn.frba.dds.models.repositories.UsuarioRepository;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ColaboracionRepoTest implements WithSimplePersistenceUnit {

/*
    private ColaboradorRepository colaboradorRepository;
    private UsuarioRepository usuarioRepository;

    private Colaborador colaborador;

    @BeforeEach
    public void setUp() {
        colaborador = new Colaborador();
        colaborador.setNombre("JOSE");
        colaborador.setApellido("PEREZ");
        colaborador.setTelefono("454545645");
        colaborador.setMail("vghhghcn@gmail.com");
        colaborador.setPuntos(0.0);

        colaborador.setTipoPersona(TipoPersona.FISICA);

        Whatsapp whatsapp = new Whatsapp();
        colaborador.setMetodoDeNotificacion(whatsapp);

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario("JOSE");
        usuario.setContrasenia("1234");
        colaborador.setUsuario(usuario);
        Documento documento = new Documento();
        documento.setTipoDocumento(TipoDocumento.DNI);
        documento.setNumeroDocumento("12345678");
        colaborador.setDocumento(documento);

        DireccionAdaptada direccion = new DireccionAdaptada();
        direccion.setCalle("MANUELA PEDRAZA");
        direccion.setNumero(123);
        colaborador.setDireccion(direccion);


        this.usuarioRepository = new UsuarioRepository();
        this.colaboradorRepository = new ColaboradorRepository();
    }

    @Test
    public void testAgregarColaboraciones() {
        usuarioRepository.guardar(colaborador.getUsuario());
        colaboradorRepository.guardar(colaborador);

        //DONACION DINERO
        DonacionDinero donacionDinero = new DonacionDinero(10000, LocalDate.now());
        donacionDinero.setFrecuencia(Frecuencia.UNICA);

        //DONACION VIANDAS
        DonacionViandas donacionViandas = new DonacionViandas(LocalDate.now(), 2);
        Heladera heladera = new Heladera();
        heladera.setNombre("Heladera 1");
        Vianda vianda1 = new Vianda();
        Comida comida = new Comida();
        comida.setNombre("Milanesa");

        vianda1.setComida(comida);
        vianda1.setFechaCaducidad(LocalDate.now());
        vianda1.setFechaDonacion(LocalDate.now());
        vianda1.setEntregada(false);
        vianda1.setColaborador(colaborador);
        vianda1.setHeladera(heladera);

        Vianda vianda2 = new Vianda();
        vianda2.setComida(comida);
        vianda2.setFechaCaducidad(LocalDate.now());
        vianda2.setFechaDonacion(LocalDate.now());
        vianda2.setEntregada(false);
        vianda2.setColaborador(colaborador);
        vianda2.setHeladera(heladera);
        donacionViandas.agregarVianda(vianda1);
        donacionViandas.agregarVianda(vianda2);

        //ENTREGA TARJETAS
        EntregaTarjetas entregaTarjetas = new EntregaTarjetas(LocalDate.now(), 1);
        Tarjeta tarjeta1 = new Tarjeta();
        tarjeta1.setFechaInicioDeUso(LocalDate.now());
        tarjeta1.setCodigoAlfaNumerico("1234");
        tarjeta1.setEstaHabilitada(true);
        tarjeta1.setFechaFinDeUso(LocalDate.now().plusDays(30));
        tarjeta1.setTipoTarjeta(TipoTarjeta.PERSONA_VULNERABLE);
        tarjeta1.setRepartidorDeTarjeta(colaborador);
        PersonaSituacionVulnerable persona = new PersonaSituacionVulnerable();
        persona.setNombre("Juan");
        persona.setApellido("Perez");
        Documento documento = new Documento();
        documento.setTipoDocumento(TipoDocumento.DNI);
        documento.setNumeroDocumento("12345678");
        persona.setDocumento(documento);
        persona.setSituacion(Situacion.CALLE);
        persona.setColaboradorResponsable(colaborador);
        tarjeta1.setPersona(persona);
        entregaTarjetas.AgregarTarjeta(tarjeta1);

        //HACERSE CARGO HELADERA
        HacerseCargoHeladera hacerseCargoHeladera = new HacerseCargoHeladera();
        hacerseCargoHeladera.agregarHeladera(heladera);
        hacerseCargoHeladera.setFechaColaboracion(LocalDate.now());

        //OFRECER PRODUCTOS
        OfrecerProductos ofrecerProductos = new OfrecerProductos();
        ofrecerProductos.setFechaColaboracion(LocalDate.now());
        Oferta oferta = new Oferta();
        oferta.setDetalle("Oferta 1");
        oferta.setColaborador(colaborador);
        ofrecerProductos.agregarOferta(oferta);


        //REDISTRIBUCION DE VIANDAS
        RedistribucionViandas redistribucionDeViandas = new RedistribucionViandas();
        redistribucionDeViandas.setFechaColaboracion(LocalDate.now());
        redistribucionDeViandas.setCantidad(1);
        redistribucionDeViandas.setHeladeraOrigen(heladera);
        redistribucionDeViandas.setHeladeraDestino(heladera);


        colaborador.agregarColaboracion(donacionDinero);
        colaborador.agregarColaboracion(donacionViandas);
        colaborador.agregarColaboracion(entregaTarjetas);
        colaborador.agregarColaboracion(hacerseCargoHeladera);
        colaborador.agregarColaboracion(ofrecerProductos);
        colaborador.agregarColaboracion(redistribucionDeViandas);

        colaboradorRepository.actualizar(colaborador);
        
        Colaborador encontrado = null;

        List colaboradores = colaboradorRepository.buscarPorNombre("JOSE");
        if (!colaboradores.isEmpty()) {
            encontrado = (Colaborador) colaboradores.get(0);
        }

        assertNotNull(encontrado);
        assertEquals(6, encontrado.getColaboraciones().size());
    }

 */
}
