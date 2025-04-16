package ar.edu.utn.frba.dds.SistemaDePuntos;

import ar.edu.utn.frba.dds.models.entities.colaboracion.Colaboracion;
import ar.edu.utn.frba.dds.models.entities.colaboracion.TipoDeColaboraciones.DonacionDinero;
import ar.edu.utn.frba.dds.models.entities.colaboracion.TipoDeColaboraciones.DonacionViandas;
import ar.edu.utn.frba.dds.models.entities.colaboracion.TipoDeColaboraciones.OfrecerProductos;
import ar.edu.utn.frba.dds.models.entities.persona.Colaborador;
import ar.edu.utn.frba.dds.models.entities.persona.TipoPersona;
import ar.edu.utn.frba.dds.models.entities.puntos.CalculadorDePuntaje;
import ar.edu.utn.frba.dds.models.entities.puntos.Oferta;
import ar.edu.utn.frba.dds.models.entities.puntos.Rubro;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;

public class TestCanje {
    @Test
    public void CanjeProductos() throws Exception {
        /*
        Colaborador colaborador = new Colaborador();
        colaborador.setNombre("mario");
        colaborador.setApellido("diaz");
        colaborador.setMail("marito@gmail.com");

        // Colaboración Dinero
        Colaboracion colaboracion1 = new DonacionDinero(100, LocalDate.of(2023, 5, 10));
        colaborador.agregarColaboracion(colaboracion1);


        // Colaboración Donación de viandas
        Colaboracion colaboracion2 = new DonacionViandas(LocalDate.of(2023, 5, 15), 50);
        colaborador.agregarColaboracion(colaboracion2);


        CalculadorDePuntaje calculadorDePuntaje = new CalculadorDePuntaje();
       calculadorDePuntaje.calcularPuntaje(colaborador);

        //tiene 125 puntos


        Colaborador empresa = new Colaborador();
        empresa.setTipoPersona(TipoPersona.JURIDICA);
        empresa.setColaboraciones(new ArrayList<Colaboracion>());
        empresa.setNombre("EMPRESA SA");

        Rubro computacion = new Rubro();
        Oferta mouse = new Oferta();
        mouse.setRubro(computacion);
        mouse.setPuntos(120.0);
        mouse.setDetalle("MOUSE GAMER RE FACHERO");

        Oferta pc = new Oferta();
        pc.setRubro(computacion);
        pc.setPuntos(1200.0);
        pc.setDetalle("PC CARA");


        OfrecerProductos ofrecerProd = new OfrecerProductos();
        ofrecerProd.agregarOferta(mouse);
        ofrecerProd.agregarOferta(pc);


        Exception exception = Assertions.assertThrows(Exception.class, () -> {

            colaborador.canjearOferta(pc);
        });

        // Verifica el mensaje de la excepción
        Assertions.assertEquals("La persona no posee los puntos suficientes", exception.getMessage());

        colaborador.canjearOferta(mouse);
        Assertions.assertEquals(5, colaborador.getPuntos());

         */
    }

}
